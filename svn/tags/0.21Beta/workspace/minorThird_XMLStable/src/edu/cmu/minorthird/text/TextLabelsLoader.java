package edu.cmu.minorthird.text;

import edu.cmu.minorthird.util.*;
import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;

/**
 * Loads and saves the contents of a TextLabels into a file.
 * 
 * Labels can be loaded from operations (see importOps) or from a serialized
 * TextLabels object. Labels can be serialized or types can be saved as
 * operations, xml, or plain lists.
 * 
 * @author William Cohen
 * @author Max Talanov
 */

public class TextLabelsLoader {
	private static Logger log = Logger.getLogger(TextLabelsLoader.class);

	/**
	 * Spans in labels are a complete list of all spans.
	 */
	static final public int CLOSE_ALL_TYPES = 1;

	/**
	 * If a document has been labeled for a type, assume all spans of that type
	 * are there.
	 */
	static final public int CLOSE_TYPES_IN_LABELED_DOCS = 2;

	/** Make no assumptions about closure. */
	static final public int DONT_CLOSE_TYPES = 3;

	static final public int CLOSE_BY_OPERATION = 4;

	public static final String[] CLOSURE_NAMES = { "CLOSE_ALL_TYPES",
			"CLOSE_TYPES_IN_LABELED_DOCS", "DONT_CLOSE_TYPES",
			"CLOSE_BY_OPERATION" };

	private int closurePolicy = CLOSE_BY_OPERATION;

	private int warnings = 0;
	static private final int MAX_WARNINGS = 10;

	// this should be removed to properties file after bug-reporting to CMU
	private Boolean forceXMLWellForm = true;

	private String namespaceURI;

	private String prefix;

	private String rootElementName;

	private static final String defaultEncoding = "UTF8";

	private static final String xmlHeader = "<?xml version=\"1.0\"?> \n";

	private static final String xmlExtension = ".xml";

	/**
	 * Set the closure policy.
	 * 
	 * @param policy
	 *            one of CLOSE_ALL_TYPES, CLOSE_TYPES_IN_LABELED_DOCS,
	 *            DONT_CLOSE_TYPES
	 */
	public void setClosurePolicy(int policy) {
		this.closurePolicy = policy;
	}

	/**
	 * Create a new labeling by importing from a file with importOps.
	 */
	public MutableTextLabels loadOps(TextBase base, File file)
			throws IOException, FileNotFoundException {
		MutableTextLabels labels = new BasicTextLabels(base);
		importOps(labels, base, file);
		return labels;
	}

	/**
	 * Load lines modifying a TextLabels from a file. There are four allowed
	 * operations: addToType, closeType, closeAllTypes, setClosure
	 * 
	 * For addToType: The lines must be of the form:
	 * <code>addToType ID LOW LENGTH TYPE</code> where ID is a documentID in
	 * the given TextBase, LOW is a character index into that document, and
	 * LENGTH is the length in characters of the span that will be created as
	 * given type TYPE. If LENGTH==-1, then the created span will go to the end
	 * of the document.
	 * 
	 * For closeType: Lines must be <code>closeType ID TYPE</code> where ID is
	 * a documentID in the given TextBase and TYPE is the label type to close
	 * over that document.
	 * 
	 * For closeAllTypes: Lines must be <code>closeAllType ID</code> where ID
	 * is a documentID in the given TextBase. The document will be closed for
	 * all types present in the TextLabels <em>after all operations</em> are
	 * performed.
	 * 
	 * For setClosure: Lines must be <code>setClosure POLICY</code> where
	 * POLICY is one of the policy types defined in this class. It will
	 * immediately change the closure policy for the loader. This is best used
	 * at the beginning of the file to indicate one of the generic policies or
	 * the CLOSE_BY_OPERATION (default) policy.
	 */
	public void importOps(MutableTextLabels labels, TextBase base, File file)
			throws IOException, FileNotFoundException {
		base = labels.getTextBase();
		if (base == null)
			throw new IllegalStateException(
					"TextBase attached to labels must not be null");

		LineNumberReader in = new LineNumberReader(new FileReader(file));
		String line = null;
		List docList = new ArrayList();
		try {
			while ((line = in.readLine()) != null) {
				if (line.trim().length() == 0)
					continue;
				if (line.startsWith("#"))
					continue;
				log.debug("read line #" + in.getLineNumber() + ": " + line);
				StringTokenizer tok = new StringTokenizer(line);
				String op;
				try {
					op = advance(tok, in, file);
				} catch (IllegalArgumentException e) {
					throw getNewException(e, ", failed to find operation.");
				}
				if ("addToType".equals(op)) {
					addToType(tok, in, file, base, labels);
				} else if ("setSpanProp".equals(op)) {
					setSpanProp(tok, in, file, base, labels);
				} else if ("closeType".equals(op)) {
					String docId = advance(tok, in, file);
					String type = advance(tok, in, file);
					Span span = base.documentSpan(docId);
					if (span != null) {
						labels.closeTypeInside(type, span);
						log.debug("closed " + type + " on " + docId);
					} else {
						warnings++;
						if (warnings < MAX_WARNINGS) {
							log.warn("unknown id '" + docId + "' in closeType");
						} else if (warnings == MAX_WARNINGS) {
							log
									.warn("there will be no more warnings of this sort given");
						}
					}
				} else if ("closeAllTypes".equalsIgnoreCase(op)) {
					String docId = advance(tok, in, file);
					docList.add(docId);
				} else {
					throw new IllegalArgumentException("error on line "
							+ in.getLineNumber() + " of " + file.getName());
				}
			}
			// close over the doc list for all types seen
			for (int i = 0; i < docList.size(); i++) {
				String docId = (String) docList.get(i);
				Span span = base.documentSpan(docId);
				closeLabels(labels.getTypes(), labels, span);
			}

		} catch (IllegalArgumentException e) {
			throw getNewException(e, " on line: " + line);
		}
		in.close();
		closeLabels(labels, closurePolicy);
	}

	private void addToType(StringTokenizer tok, LineNumberReader in, File file,
			TextBase base, MutableTextLabels labels) {
		String id = advance(tok, in, file);
		String loStr = advance(tok, in, file);
		String lenStr = advance(tok, in, file);
		String type = advance(tok, in, file);
		String confidence = tok.hasMoreTokens() ? advance(tok, in, file) : null;
		int lo, len;
		try {
			lo = Integer.parseInt(loStr);
			len = Integer.parseInt(lenStr);
			Span span = base.documentSpan(id);
			if (span == null) {
				warnings++;
				if (warnings < MAX_WARNINGS) {
					log.warn("unknown id '" + id + "' in addToType " + lo + " "
							+ len);
				} else if (warnings == MAX_WARNINGS) {
					log
							.warn("there will be no more warnings of this sort given");
				}
			} else {
				Details details = null;
				if (confidence != null)
					details = new Details(StringUtil.atof(confidence));
				if (lo == 0 && len < 0)
					labels.addToType(span, type, details);
				else {
					// shortcut: char offsets "0 -1" means the whole document
					if (len < 0)
						len = span.asString().length() - lo;
					labels.addToType(span.charIndexSubSpan(lo, lo + len), type,
							details);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("bad number on line "
					+ in.getLineNumber() + " of " + file.getName());
		}
	}

	private void setSpanProp(StringTokenizer tok, LineNumberReader in,
			File file, TextBase base, MutableTextLabels labels) {
		String id = advance(tok, in, file);
		String loStr = advance(tok, in, file);
		String lenStr = advance(tok, in, file);
		String prop = advance(tok, in, file);
		String value = advance(tok, in, file);
		int lo, len;
		try {
			lo = Integer.parseInt(loStr);
			len = Integer.parseInt(lenStr);
			Span span = base.documentSpan(id);
			if (span == null) {
				warnings++;
				if (warnings < MAX_WARNINGS) {
					log.warn("unknown id '" + id + "'");
				} else if (warnings == MAX_WARNINGS) {
					log
							.warn("there will be no more warnings of this sort given");
				}
			} else {
				if (lo == 0 && len < 0)
					labels.setProperty(span, prop, value);
				else {
					if (len < 0)
						len = span.asString().length() - lo;
					labels.setProperty(span.charIndexSubSpan(lo, lo + len),
							prop, value);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("bad number on line "
					+ in.getLineNumber() + " of " + file.getName());
		}
	}

	private static IllegalArgumentException getNewException(
			IllegalArgumentException e, String addToMsg) {
		String msg = e.getMessage() + addToMsg;
		StackTraceElement[] trace = e.getStackTrace();
		IllegalArgumentException exception = new IllegalArgumentException(msg);
		exception.setStackTrace(trace);
		return exception;
	}

	private String advance(StringTokenizer tok, LineNumberReader in, File file) {
		if (!tok.hasMoreTokens())
			throw new IllegalArgumentException("error on line "
					+ in.getLineNumber() + " of " + file.getName()
					+ " failed to find token");
		return tok.nextToken();
	}

	/**
	 * Close labels on the labels according to the policy. This applies the same
	 * policy to all documents and types in the labels. To get finer control of
	 * closure use closeLabels(Set, MutableTextLabels, Span) or
	 * MutableTextLabels.closeTypeInside(...)
	 * 
	 * @param labels
	 * @param policy
	 */
	public void closeLabels(MutableTextLabels labels, int policy) {
		Set types = labels.getTypes();
		TextBase base = labels.getTextBase();
		switch (policy) {
		case CLOSE_ALL_TYPES:
			for (Span.Looper i = base.documentSpanIterator(); i.hasNext();) {
				Span document = i.nextSpan();
				closeLabels(types, labels, document);
			}
			break;
		case CLOSE_TYPES_IN_LABELED_DOCS:
			Set labeledDocs = new TreeSet();
			for (Iterator j = types.iterator(); j.hasNext();) {
				String type = (String) j.next();
				for (Span.Looper i = labels.instanceIterator(type); i.hasNext();) {
					Span span = i.nextSpan();
					labeledDocs.add(span.documentSpan());
				}
			}
			for (Iterator i = labeledDocs.iterator(); i.hasNext();) {
				Span document = (Span) i.next();
				closeLabels(types, labels, document);
			}
			break;
		case DONT_CLOSE_TYPES: // do nothing for this
			break;
		case CLOSE_BY_OPERATION: // already closed in theory
			break;
		default:
			log.warn("closure policy(" + policy + ") not recognized");
		}
	}

	/**
	 * Close all types in the typeSet on the given document
	 * 
	 * @param typeSet
	 *            set of types to close for this document
	 * @param labels
	 *            TextLabels holding the types
	 * @param document
	 *            Span to close types over
	 */
	private void closeLabels(Set types, MutableTextLabels labels, Span document) {
		for (Iterator j = types.iterator(); j.hasNext();) {
			String type = (String) j.next();
			labels.closeTypeInside(type, document);
		}
	}

	/** Read in a serialized TextLabels. */
	public MutableTextLabels loadSerialized(File file, TextBase base)
			throws IOException, FileNotFoundException {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(file)));
			MutableTextLabels labels = (MutableTextLabels) in.readObject();
			labels.setTextBase(base);
			in.close();
			return labels;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("can't read TextLabels from "
					+ file + ": " + e);
		}
	}

	/** Serialize a TextLabels. */
	public void saveSerialized(MutableTextLabels labels, File file)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(file)));
		out.writeObject(labels);
		out.flush();
		out.close();
	}

	/** Save extracted data in a format readable with loadOps. */
	public String printTypesAsOps(TextLabels labels) {
		StringBuffer out = new StringBuffer();
		ProgressCounter pc = new ProgressCounter("saving labels", "type",
				labels.getTypes().size());
		for (Iterator i = labels.getTypes().iterator(); i.hasNext();) {
			String type = (String) i.next();
			ProgressCounter pc2 = new ProgressCounter("saving type " + type,
					"span");
			for (Span.Looper j = labels.instanceIterator(type); j.hasNext();) {
				Span s = j.nextSpan();
				if (s.size() > 0) {
					int lo = s.getTextToken(0).getLo();
					int hi = s.getTextToken(s.size() - 1).getHi();
					Details details = labels.getDetails(s, type);
					if (details == null || details == Details.DEFAULT) {
						out.append("addToType " + s.getDocumentId() + " " + lo
								+ " " + (hi - lo) + " " + type + "\n");
					} else {
						out.append("addToType " + s.getDocumentId() + " " + lo
								+ " " + (hi - lo) + " " + type + " "
								+ details.getConfidence() + "\n");
					}
				} else {
					warnings++;
					if (warnings < MAX_WARNINGS) {
						log.warn("forgetting label on empty span type " + type
								+ ": " + s);
					} else if (warnings == MAX_WARNINGS) {
						log
								.warn("there will be no more warnings of this sort given");
					}
				}
				pc2.progress();
			}
			pc2.finished();
			Span.Looper it = labels.closureIterator(type);
			while (it.hasNext()) {
				Span s = it.nextSpan();
				Span doc = s.documentSpan();
				if (s.size() != doc.size()) {
					throw new UnsupportedOperationException(
							"can't save environment with closureSpans!=docSpans");
				}
				out
						.append("closeType " + s.getDocumentId() + " " + type
								+ "\n");
			}
			pc.progress();
		}
		pc.finished();
		ProgressCounter pc3 = new ProgressCounter("saving labels", "property",
				labels.getSpanProperties().size());
		for (Iterator i = labels.getSpanProperties().iterator(); i.hasNext();) {
			String prop = (String) i.next();
			for (Span.Looper j = labels.getSpansWithProperty(prop); j.hasNext();) {
				Span s = j.nextSpan();
				String val = labels.getProperty(s, prop);
				int lo = s.getTextToken(0).getLo();
				int hi = s.getTextToken(s.size() - 1).getHi();
				out.append("setSpanProp " + s.getDocumentId() + "  " + lo + " "
						+ (hi - lo) + " " + prop + " " + val + "\n");
			}
			pc3.progress();
		}
		pc3.finished();
		return out.toString();
	}

	/** Save extracted data in a format readable with loadOps. */
	public void saveTypesAsOps(TextLabels labels, File file) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(file), false,
				defaultEncoding);
		out.println(printTypesAsOps(labels));
		out.close();
	}

	/**
	 * Save spans of given type into the file, one per line. Linefeeds in
	 * strings are replaced with spaces.
	 */
	public void saveTypesAsStrings(TextLabels labels, File file,
			boolean includeOffset) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(file), false,
				defaultEncoding);
		for (Iterator j = labels.getTypes().iterator(); j.hasNext();) {
			String type = (String) j.next();
			for (Span.Looper i = labels.instanceIterator(type); i.hasNext();) {
				Span span = i.nextSpan();
				out.print(type);
				if (includeOffset) {
					out.print(":" + span.getDocumentId() + ":"
							+ span.getTextToken(0).getLo() + ":"
							+ span.getTextToken(span.size() - 1).getHi());
				}
				out.println("\t" + span.asString().replace('\n', ' '));
			}
		}
		out.close();
	}

	/**
	 * Save documents to specified directory with extracted types embedded as
	 * xml. This method will add xml extension.
	 */
	public void saveDocsWithEmbeddedTypes(TextLabels labels, File dir)
			throws IOException {
		Span currDoc;
		Span.Looper looper = labels.getTextBase().documentSpanIterator();
		PrintStream out;

		// Max Talanov: suppose this is bug
		/*
		 * if (!dir.mkdir()) { throw new IOException("Could not create directory
		 * named: " + dir); }
		 */

		while (looper.hasNext()) {
			// this call returns the entire document with all labels embedded as
			// xml
			currDoc = looper.nextSpan();
			out = new PrintStream(new FileOutputStream(new File(dir + "/"
					+ currDoc.getDocumentId() + xmlExtension)), false,
					defaultEncoding);
			out.println(createXMLmarkup(currDoc.getDocumentId(), labels));
			out.close();
		}
	}
	

	/**
	 * Save documents to specified directory with extracted types embedded as
	 * xml, in specified namespace and with specified prefix. This method will
	 * add xml extension.
	 */
	public void saveDocsWithEmbeddedTypes(TextLabels labels, File dir,
			String namespaceURI, String prefix, String rootElementName)
			throws IOException {
		Span currDoc;
		Span.Looper looper = labels.getTextBase().documentSpanIterator();
		PrintStream out;
		this.namespaceURI = namespaceURI;
		this.prefix = prefix;
		this.rootElementName = rootElementName;

		// Max Talanov: suppose this is bug
		/*
		 * if (!dir.mkdir()) { throw new IOException("Could not create directory
		 * named: " + dir); }
		 */

		while (looper.hasNext()) {
			// this call returns the entire document with all labels embedded as
			// xml
			currDoc = looper.nextSpan();
			out = new PrintStream(new FileOutputStream(new File(dir + "/"
					+ currDoc.getDocumentId() + xmlExtension)), false,
					defaultEncoding);
			out.println(createXMLmarkup(currDoc.getDocumentId(), labels));
			out.close();
		}
	}

	/**
	 * @deprecated use createXMLMarkup(String documentId,TextLabels labels)
	 * 
	 * Save extracted data in an XML format. Convert to string
	 * &lt;root>..&lt;type>...&lt;/type>..&lt;/root> nested things
	 * &lt;a>A&lt;b>B&lt;/b>C&lt;/a> are stored as nested things &lt;a>A&lt;set
	 * v=a,b>B&lt;/set>C&lt;/a> where single sets are simplified so mismatches
	 * like [A (B C] D)E are stored as &lt;a>a&lt;set v=a,b>B
	 * C&lt;/set>&lt;/a>&lt;b>D&lt;/b>E
	 */
	public String markupDocumentSpan(String documentId, TextLabels labels) {
		TreeMap boundaries = new TreeMap();
		for (Iterator i = labels.getTypes().iterator(); i.hasNext();) {
			String type = (String) i.next();
			for (Span.Looper j = labels.instanceIterator(type, documentId); j
					.hasNext();) {
				Span s = j.nextSpan();
				setBoundary(boundaries, "begin", type, s.getLeftBoundary());
				setBoundary(boundaries, "end", type, s.getRightBoundary());
			}
		}
		// now walk through boundaries and find out which set as
		// associated with each segment - want map from boundaries to
		// type sets
		String source = labels.getTextBase().documentSpan(documentId)
				.asString();
		StringBuffer buf = new StringBuffer("");
		buf.append("<root>");
		int currentPos = 0;
		Set currentTypes = new TreeSet();
		String lastMarkup = null;
		for (Iterator i = boundaries.keySet().iterator(); i.hasNext();) {
			Span b = (Span) i.next();
			// work out what types are in effect here
			Set ops = (Set) boundaries.get(b);
			for (Iterator j = ops.iterator(); j.hasNext();) {
				String[] op = (String[]) j.next();
				if ("begin".equals(op[0]))
					currentTypes.add(op[1]);
				else
					currentTypes.remove(op[1]);
			}
			// output next section of document
			int pos;
			if (b.documentSpanStartIndex() < b.documentSpan().size())
				pos = b.documentSpan().subSpan(b.documentSpanStartIndex(), 1)
						.getTextToken(0).getLo();
			else
				pos = b.documentSpan()
						.getTextToken(b.documentSpan().size() - 1).getHi();

			buf.append(source.substring(currentPos, pos));
			// close off last markup
			if (lastMarkup != null)
				buf.append("</" + lastMarkup + ">");
			// work out next markup symbol
			String markup = null;
			String value = null;
			if (currentTypes.size() == 1) {
				markup = (String) (currentTypes.iterator().next());
			} else if (currentTypes.size() > 1) {
				markup = "overlap";
				StringBuffer vBuf = new StringBuffer("");
				for (Iterator j = currentTypes.iterator(); j.hasNext();) {
					if (vBuf.length() > 0)
						vBuf.append(",");
					vBuf.append((String) j.next());
				}
				value = vBuf.toString();
			}
			if (markup != null && value != null) {
				buf.append("<" + markup + " value=\"" + value + "\">");
			} else if (markup != null) {
				buf.append("<" + markup + ">");
			}
			// update position, lastMarkup
			currentPos = pos;
			lastMarkup = markup;
		} // each boundary
		// close it all off
		buf.append(source.substring(currentPos, source.length()));
		buf.append("</root>");
		return buf.toString();
	}

	/**
	 * Save extracted data in an XML format. Convert to string
	 * &lt;root>..&lt;type>...&lt;/type>..&lt;/root>. <br>
	 * <br>
	 * In the even that labels overlap such as [A (B C] D)E an
	 * IllegalArgumentException is thrown because a well-formed XML document
	 * cannot be created. In case force well-formed XML flag is true this method
	 * will always generate well-formed XML
	 */
	public String createXMLmarkup(String documentId, TextLabels labels) {
		Span docSpan = labels.getTextBase().documentSpan(documentId);
		String docString = labels.getTextBase().documentSpan(documentId)
				.getDocumentContents();

		// Put all labels and their info in a list
		ArrayList<LabelInfo> unsortedLabels = new ArrayList<LabelInfo>();
		for (Iterator<String> i = labels.getTypes().iterator(); i.hasNext();) {
			String type = (String) i.next();
			for (Span.Looper j = labels.instanceIterator(type, documentId); j
					.hasNext();) {
				Span s = j.nextSpan();
				int start = s.documentSpanStartIndex();
				int end = start + s.size() - 1;
				unsortedLabels.add(new LabelInfo(s, type, start, end));
			}
		}

		// Sort the labels. If two spans are overlapping then throw an exception
		ArrayList<LabelInfo> sortedLabels = new ArrayList<LabelInfo>(
				unsortedLabels.size());
		while (unsortedLabels.size() > 0) {
			LabelInfo curLabel = (LabelInfo) unsortedLabels.remove(0);
			int position = -1;

			boolean overlap = false;

			// Iterate through sortedLabels
			for (int j = 0; j < sortedLabels.size(); j++) {
				LabelInfo compLabel = (LabelInfo) sortedLabels.get(j);
				// Find if there is an overlap and try to make it not
				// overlapping in case force XML welform is on
				if ((curLabel.start < compLabel.start && curLabel.end > compLabel.start)
						&& (curLabel.end < compLabel.end)) {
					if (forceXMLWellForm) {
						compLabel.start = curLabel.end;
						/*
						 * System.out.println(" res comp " + compLabel.start + " " +
						 * compLabel.end);
						 */
					} else {
						overlap = true;
					}
				} else if ((curLabel.start > compLabel.start && curLabel.start < compLabel.end)
						&& (curLabel.end > compLabel.end)) {
					if (forceXMLWellForm) {
						compLabel.end = curLabel.start;
						/*
						 * System.out.println(" res comp " + curLabel.type + " " +
						 * compLabel.type + " " + compLabel.start + " " +
						 * compLabel.end);
						 */
					} else {
						overlap = true;
					}
				}
				// Find position
				if ((curLabel.start < compLabel.start)
						|| ((curLabel.start == compLabel.start) && (curLabel.end >= compLabel.end))) {
					position = j;
					break;
				}
			}

			// If the label overlapped with another label, then throw an
			// exception
			/*
			 * if (overlap) throw new IllegalArgumentException( "Labels contain
			 * overlapping spans, cannot save as XML format.");
			 */

			// Otherwise add the label to the proper position in the sorted
			// list.
			if (position > -1) {
				sortedLabels.add(position, curLabel);
			} else
				sortedLabels.add(curLabel);
		}

		// Create sorted list of tags
		boolean hasPrefix = false;
		if (this.prefix != null && this.prefix.length() > 0)
			hasPrefix = true;
		ArrayList<TagInfo> sortedTags = new ArrayList<TagInfo>(sortedLabels
				.size() * 2);
		for (int i = 0; i < sortedLabels.size(); i++) {
			LabelInfo label = (LabelInfo) sortedLabels.get(i);
			if (hasPrefix) {
				sortedTags.add(new TagInfo(label.start, "<" + this.prefix + ":"
						+ label.type + ">", true, label.start, label.end,
						label.type));
			} else {
				sortedTags.add(new TagInfo(label.start, "<" + label.type + ">",
						true, label.start, label.end, label.type));
			}
		}

		// Get end tags on the place
		boolean added = false;
		boolean metSelf = false;
		while (sortedLabels.size() > 0) {
			LabelInfo label = (LabelInfo) sortedLabels.remove(0);
			added = false;
			metSelf = false;
			for (int y = 0; y < sortedTags.size(); y++) {
				TagInfo tag = (TagInfo) sortedTags.get(y);
				// bug: was not good for endings of enclosed tags
				if (label.end < tag.pos
						|| (label.end <= tag.pos && label.end <= tag.end
								&& metSelf && tag.start < tag.end)) {
					if (hasPrefix) {
						sortedTags.add(y, new TagInfo(label.end, "</"
								+ this.prefix + ":" + label.type + ">", false,
								label.start, label.end, label.type));
					} else {
						sortedTags.add(y, new TagInfo(label.end, "</"
								+ label.type + ">", false, label.start,
								label.end, label.type));
					}
					added = true;
					break;
				}
				if (label.type.equals(tag.type) && label.start == tag.start
						&& label.end == tag.end)
					metSelf = true;
			}
			if (!added) {
				if (hasPrefix) {
					sortedTags.add(new TagInfo(label.end, "</" + this.prefix
							+ ":" + label.type + ">", false, label.start,
							label.end, label.type));
				} else {
					sortedTags.add(new TagInfo(label.end, "</" + label.type
							+ ">", false, label.start, label.end, label.type));
				}

			}
		}

		// Create marked up XML StringBuffer
		StringBuffer buffer = new StringBuffer();
		buffer.append(xmlHeader);
		if (hasPrefix) {
			if (this.namespaceURI != null && this.namespaceURI.length() > 0) {
				buffer.append("<" + this.prefix + ":" + this.rootElementName + " xmlns:"
						+ this.prefix + "=\"" + this.namespaceURI + "\">");
			} else {
				buffer.append("<" + this.prefix + ":" + this.rootElementName + " xmlns:"
						+ this.prefix + "=\"\">");
			}
		} else {
			buffer.append("<" + this.rootElementName + ">");
		}
		int docPos = 0, pos = 0;
		while (sortedTags.size() > 0) {
			TagInfo curTag = (TagInfo) sortedTags.remove(0);

			if (curTag.pos < docSpan.size()) {
				if (curTag.isOpenTag)
					pos = docSpan.subSpan(curTag.pos, 1).getTextToken(0)
							.getLo();
				else
					pos = docSpan.subSpan(curTag.pos, 1).getTextToken(0)
							.getHi();
			} else
				pos = docString.length();
			if (docPos < pos) {
				// sorry but previous annotation was too greedy
				buffer.append(docString.substring(docPos, pos));
			}
			buffer.append(curTag.tag);
			docPos = pos;
		}
		buffer.append(docString.substring(docPos, docString.length()));
		if (hasPrefix) {
			buffer.append("</" + this.prefix + ":" + this.rootElementName + ">");
		} else {
			buffer.append("</" + this.rootElementName + ">");
		}
		return buffer.toString();
	}

	private class TagInfo {
		public int pos;
		public String tag;
		public boolean isOpenTag;
		public int end;
		public int start;
		public String type;

		public TagInfo(int pos, String tag, boolean isOpenTag) {
			this.pos = pos;
			this.tag = tag;
			this.isOpenTag = isOpenTag;
		}

		public TagInfo(int pos, String tag, boolean isOpenTag, int end) {
			this.pos = pos;
			this.tag = tag;
			this.isOpenTag = isOpenTag;
			this.end = end;
		}

		public TagInfo(int pos, String tag, boolean isOpenTag, int end,
				String type) {
			this.pos = pos;
			this.tag = tag;
			this.isOpenTag = isOpenTag;
			this.end = end;
			this.type = type;
		}

		public TagInfo(int pos, String tag, boolean isOpenTag, int start,
				int end, String type) {
			this.pos = pos;
			this.tag = tag;
			this.isOpenTag = isOpenTag;
			this.start = start;
			this.end = end;
			this.type = type;
		}

		public String toString() {
			return (pos + " : " + end + " : " + tag);
		}
	}

	private class LabelInfo {
		public Span span;
		public String type;
		public int start;
		public int end;

		public LabelInfo(Span span, String type, int start, int end) {
			this.span = span;
			this.type = type;
			this.start = start;
			this.end = end;
		}
	}

	// Helper method used to maintain a set of tag boundaries
	private void setBoundary(TreeMap boundaries, String beginOrEnd,
			String type, Span s) {
		Set ops = (Set) boundaries.get(s);
		if (ops == null)
			boundaries.put(s, (ops = new HashSet()));
		ops.add(new String[] { beginOrEnd, type });
	}

	/** Save extracted data in an XML format */
	public String saveTypesAsXML(TextLabels labels) {
		StringBuffer buf = new StringBuffer("<extractions>\n");
		for (Iterator i = labels.getTypes().iterator(); i.hasNext();) {
			String type = (String) i.next();
			for (Span.Looper j = labels.instanceIterator(type); j.hasNext();) {
				Span s = j.nextSpan();
				int lo = s.getTextToken(0).getLo();
				int hi = s.getTextToken(s.size() - 1).getHi();
				buf.append("  <" + type + " lo=" + lo + " hi=" + hi + ">"
						+ s.asString() + "</" + type + ">\n");
			}
		}
		buf.append("</extractions>\n");
		return buf.toString();
	}
}
