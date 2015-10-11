/**
 *
 */
package idp.sandBox.client.gui;

import idp.sandBox.client.constants.Constants;
import idp.sandBox.client.dialogs.IDPMessageDlg;
import idp.sandBox.client.gui.components.IDPGlassPane;
import idp.sandBox.client.gui.components.tabs.IDPTabContext;
import idp.sandBox.client.gui.components.tabs.IDPTabbedPane;
import idp.sandBox.client.gui.components.tabs.TabbedPaneScroller;
import idp.sandBox.client.gui.components.trees.IDPContentTree;
import idp.sandBox.client.managers.CheckTreeManager;
import idp.sandBox.client.managers.IDPClientSingleton;
import idp.sandBox.client.managers.IDPConnection;
import idp.sandBox.client.managers.IDPSettingsManager;
import idp.sandBox.client.managers.IDPWindowManager;
import idp.sandBox.models.TextFileInfo;
import idp.sandBox.server.MessagingInterface;

import java.awt.Color;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author max talanov, airat natfullin
 * 
 */
public class SandBoxClient extends JFrame implements ComponentListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = true;
	static final int MAXIMIZED = 1;

	//private MessagingInterface connection = null;
	
	private IDPGlassPane jGlassPane = null;

	/////////////////////////////////////////////////////////////////////////////////////
	// Tabs: are turned off in current version
	private HashMap<JScrollPane, IDPTabContext> tabsDNDContext = new HashMap<JScrollPane, IDPTabContext>();
	/////////////////////////////////////////////////////////////////////////////////////
						
	private DefaultMutableTreeNode xmlTreeRoot = new DefaultMutableTreeNode("XML tree");
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Drag and Drop
	public IDPTabContext curTabContext;
	/////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////
	// Save XML
	//private JButton jButtonSaveXML = null;
	private String current_filename = null;
	private String globalTagList = "";	
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Getter for String currentXML 
	 */
	public String getCurrentXML() {
		if (curTabContext == null) return null;
		return curTabContext.getCurrentXML();
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	private int screen_width;
	private int screen_height;
	
	
	/**
	 * Update the current pane. 
	 */
	public void updatePane()
	{
		/////////////////////////////////////////////////////////////////////////////////
		// determine the current pane and selected tag
		DefaultMutableTreeNode xmlnode = (DefaultMutableTreeNode) IDPWindowManager.getJXMLTree().getLastSelectedPathComponent();
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		if (xmlnode == null) return;
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////
		// get the selected tags in XML Tree (jXMLTree) 
		String selected_tags = "";
		CheckTreeManager treeManager = IDPWindowManager.getCheckTreeManager();
		TreePath checkedPaths[] = treeManager.getSelectionModel().getSelectionPaths();
		if (checkedPaths != null)
		{
			for (TreePath path: checkedPaths)
			{
				if (selected_tags.length() > 0) selected_tags += ";"; 
				selected_tags += path.getLastPathComponent().toString();
			}
		}
			
		/////////////////////////////////////////////////////////////////////////////
		// re-color the text
		if (curTabContext != null)
			updateView(curTabContext.getCurTextFileInfo(), selected_tags);
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * This method parses XML and add nodes to JTree component 
	 * 
	 */
	public void parseXMLforTree(String sXML) {
		
		xmlTreeRoot.removeAllChildren();
		
		/////////////////////////////////////////////////////////////////////////////////
		// parse XML document
		try
		{
			/////////////////////////////////////////////////////////////////////////////
			// check
			if (sXML == null || sXML.isEmpty() == Boolean.TRUE) return;
			/////////////////////////////////////////////////////////////////////////////

			/////////////////////////////////////////////////////////////////////////////
			// load xml
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new InputSource(new StringReader(sXML)));
			//doc = docBuilder.parse(filename);
			doc.getDocumentElement().normalize();
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// process the root element separately
			Node root = doc.getFirstChild();
			globalTagList = "<"+root.getNodeName()+">";
			MutableTreeNode tree_root = new DefaultMutableTreeNode("<"+root.getNodeName()+">");
			xmlTreeRoot.add(tree_root);
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// call the recursive procedure
			buildTree(root, tree_root);
			/////////////////////////////////////////////////////////////////////////////
		}
		catch(ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch(IOException err)
		{
			err.printStackTrace();
		}
		catch(SAXException ex)
		{
			ex.printStackTrace();
		}
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Recursive method for parsing XML and adding nodes to the XML tree 
	 * 
	 */	
	private void buildTree(Node node, MutableTreeNode tree_node)
	{
		NodeList node_list = node.getChildNodes();
		int len = node_list.getLength();
		/////////////////////////////////////////////////////////////////////////////////
		// look through all children
		for (int i = 0; i < len; i++)
		{
			Node fsnode = node_list.item(i);
			
			if (fsnode.getNodeType() == Node.ELEMENT_NODE)
			{
				MutableTreeNode newChild = new DefaultMutableTreeNode("<"+fsnode.getNodeName()+">");
				
				/////////////////////////////////////////////////////////////////////////
				// add to JTree component
				tree_node.insert(newChild,0);

				/////////////////////////////////////////////////////////////////////////
				// add to global list
				globalTagList += ";<" + fsnode.getNodeName() + ">";
				
				/////////////////////////////////////////////////////////////////////////
				// recursive call
				buildTree(fsnode,newChild);
			}
		}
		/////////////////////////////////////////////////////////////////////////////////

	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * get XML namespace prefix
	 */
	String getXMLnamespacePrefix(String currentXML)
	{
		String prefix = "";
		
		int index = currentXML.indexOf("xmlns:");
		
		if (index != -1)
		{
			index = index + "xmlns:".length();
			int index2 = currentXML.indexOf("=", index);
			prefix = currentXML.substring(index, index2);
			prefix = prefix.trim() + ":";
		}
		else return null;
		
		return prefix;
	}
	/////////////////////////////////////////////////////////////////////////////////////	

	/**
	 * get XML namespace url
	 */
	String getXMLnamespaceURL(String currentXML)
	{
		String url = "";
		
		int index = currentXML.indexOf("xmlns:");
		
		if (index != -1)
		{
			index = index + "xmlns:".length();
			int index2 = currentXML.indexOf("\"", index);
			int index3 = currentXML.indexOf("\"", index2+1);
			url = currentXML.substring(index2+1, index3);
		}
		else return null;
		
		return url;
	}
	/////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Strip all html tags from the source 
	 * Has simpliest algorithm: just replace all tag occurrences by space
	 * There is a list of tags that should be replaced.
	 */
	/*
	public String stripHTMLtags(String source)
	{
		/////////////////////////////////////////////////////////////////////////////////
		String tag_str = "<html>;<title>;<head>;<body>;<table>;<td>;<tr>;<br>;";
		tag_str += "<a>;<b>;<i>;<u>;<form>;<h1>;<h2>;<h3>;<h4>;";
		tag_str += "<h5>;<hr>;<img>;<li>;<frame>;<frameset>;<div>;";
		tag_str += "<th>;<ul>;<string>;<p>;<span>;<ol>;";
		tag_str += "select;<option>;<textarea>;<input>;";
		//tag_str += "<root>";
		String[] tag_list = tag_str.split(";");
		// lower case source
		String lc_source = source.toLowerCase();
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		String opening_tag = "";
		String closing_tag = "";
		String alone_tag = "";
		String param_tag = "";
		int index = 0;
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		for (String tag : tag_list)
		{
			/////////////////////////////////////////////////////////////////////////////
			// check for safety (otherwise infinite loop may appear)
			if (tag.length() == 0) {
				System.out.println("warning: empty tag");
				continue;
			}
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// usual tags - like <table>
			opening_tag = tag;
			// like </table> 
			closing_tag = tag.replace("<", "</");
			// like <br/>
			alone_tag = tag.replace(">", "/>");
			// like <table border=1>
			param_tag = tag.replace(">", "");
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// delete them all!
			// commented cause this algorithm is case sensitive
			//source = source.replaceAll(opening_tag, " ");
			//source = source.replaceAll(closing_tag, " ");
			//source = source.replaceAll(alone_tag, " ");
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// opening tag
			index = lc_source.indexOf(opening_tag);
			// if the next char is ":", then it was namespace - just skip it
			if (index != -1 && lc_source.charAt(index+1) == ':') index = -1;
			while (index != -1)
			{
				int index2 = lc_source.indexOf(">", index);
				source = source.substring(0, index) + source.substring(index2+1);
				lc_source = lc_source.substring(0, index) + lc_source.substring(index2+1);
				index = lc_source.indexOf(opening_tag);
			}
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// closing tag
			index = lc_source.indexOf(closing_tag);
			// if the next char is ":", then it was namespace - just skip it
			if (index != -1 && lc_source.charAt(index+1) == ':') index = -1;
			while (index != -1)
			{
				int index2 = lc_source.indexOf(">", index);
				source = source.substring(0, index) + source.substring(index2+1);
				lc_source = lc_source.substring(0, index) + lc_source.substring(index2+1);
				index = lc_source.indexOf(closing_tag);
			}
			/////////////////////////////////////////////////////////////////////////////

			/////////////////////////////////////////////////////////////////////////////
			// alone tag
			index = lc_source.indexOf(alone_tag);
			// if the next char is ":", then it was namespace - just skip it
			if (index != -1 && lc_source.charAt(index+1) == ':') index = -1;
			while (index != -1)
			{
				int index2 = lc_source.indexOf(">", index);
				source = source.substring(0, index) + source.substring(index2+1);
				lc_source = lc_source.substring(0, index) + lc_source.substring(index2+1);
				index = lc_source.indexOf(alone_tag);
			}
			/////////////////////////////////////////////////////////////////////////////

			/////////////////////////////////////////////////////////////////////////////
			// tag with parameters
			index = lc_source.indexOf(param_tag);
			// if the next char is ":", then it was namespace - just skip it
			if (index != -1 && lc_source.charAt(index+param_tag.length()) == ':') index = -1;
			while (index != -1)
			{
				// find the end of the tag 
				int index2 = lc_source.indexOf(">", index);
				source = source.substring(0, index) + source.substring(index2+1);
				lc_source = lc_source.substring(0, index) + lc_source.substring(index2+1);
				index = lc_source.indexOf(param_tag);
			}
			/////////////////////////////////////////////////////////////////////////////
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		return source;
		/////////////////////////////////////////////////////////////////////////////////
	}
	*/
	/////////////////////////////////////////////////////////////////////////////////////	

	
	/**
	 * Create text pane and fill it with the data from server, then remove all tags that
	 * are included into right JTree component, then color the selected tag
	 * 
	 * @param aFileInfo - the data that identifies the JScrollPane in the tabs
	 */
	public boolean openView(TextFileInfo aFileInfo) {
		
		/////////////////////////////////////////////////////////////////////////////////
		// try to find out if such pane already exists
		IDPTabbedPane jTabbedPane = IDPWindowManager.getJTabbedPane();
		JScrollPane currentPane = jTabbedPane.getTabInfo(aFileInfo.getRelativePath());
		
		/////////////////////////////////////////////////////////////////////////////////
		// if pane exists then just focus on it
		if (currentPane != null) {
			jTabbedPane.setSelectedComponent(currentPane);
			curTabContext = this.tabsDNDContext.get(currentPane);
			if (curTabContext != null)
				IDPWindowManager.getJButtonSaveChanges().setEnabled(curTabContext.isChanged());
			else IDPWindowManager.getJButtonSaveChanges().setEnabled(false);
		} 
		else 
		{   // create new text pane
			// add textPane to tab pane
			
			/////////////////////////////////////////////////////////////////////////////
			// check if the extension is appropriate
			if (aFileInfo.getName().indexOf(Constants.annotatorsExtension) < 0
					&& aFileInfo.getName().indexOf(Constants.seqExtension) < 0
					&& aFileInfo.getName()
							.indexOf(Constants.namespaceExtension) < 0) {
				try {
					
					
					/////////////////////////////////////////////////////////////////////
					// get data from the server
					MessagingInterface connection = IDPConnection.getConnection();
					String buf = connection.getFileContentByAbsolutePath(aFileInfo.getAbsolutePath());
					if (buf.isEmpty() == true) return false;
					/////////////////////////////////////////////////////////////////////

					/////////////////////////////////////////////////////////////////////
					// create text pane to represent data in it
					JTextPane textPane = new JTextPane();
					textPane.setEditable(false);
					//textPane.setContentType("text/html");
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////				
					// add to JTabbedPane 
					JScrollPane textView = new JScrollPane(textPane);
				    jTabbedPane.addTab(aFileInfo.getName(), textView);
					/////////////////////////////////////////////////////////////////////
				
				    /////////////////////////////////////////////////////////////////////
					// put to Hash Map
					jTabbedPane.putTabInfo(aFileInfo, textView);
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					curTabContext = new IDPTabContext();
					curTabContext.setCurTextFileInfo(aFileInfo);
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					current_filename = aFileInfo.getRelativePath();
					int last_index = current_filename.lastIndexOf("\\");
					if (last_index != -1)
					{
						current_filename = current_filename.substring(last_index+1);
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// trim
					buf = buf.trim();
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// keep in memory current XML and textPane
					curTabContext.setCurrentXML(buf);
					IDPWindowManager.getJXMLTree().setCurrentPane(textPane);
					curTabContext.setCurTextPane(textPane);
					IDPWindowManager.getJXMLTree().setCurrentXML(buf);
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// get xml namespace prefix and url
					curTabContext.setXmlns_prefix(getXMLnamespacePrefix(buf));
					curTabContext.setXmlns_url(getXMLnamespaceURL(buf));
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					//buf = stripHTMLtags(buf);
					buf = buf.trim();
					/////////////////////////////////////////////////////////////////////

					/////////////////////////////////////////////////////////////////////
					// Initialize
					if (curTabContext.getTag_scheme() == null)
					{
						curTabContext.setTag_scheme(new HashMap<String,Offset>());
					}
					else
					{
						curTabContext.clearTag_scheme();
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// get all selected elements in xml tree
					String selected_tags = "";
					CheckTreeManager treeManager = IDPWindowManager.getCheckTreeManager();
					TreePath checkedPaths[] = treeManager.getSelectionModel().getSelectionPaths();
					if (checkedPaths != null)
					{
						for (TreePath path: checkedPaths)
						{
							if (selected_tags.length() > 0) selected_tags += ";"; 
							selected_tags += path.getLastPathComponent().toString();
						}
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// get selected tag from right JTree component
					String tag_to_modify = "";
					TreePath[] paths = IDPWindowManager.getJXMLTree().getSelectionPaths();
										
					if (paths != null)
					{
						tag_to_modify = paths[0].getLastPathComponent().toString();
					}
					/////////////////////////////////////////////////////////////////////
					
					//globalTagList = "<i:post_date>";
					String[] taglist = globalTagList.split(";");

					/////////////////////////////////////////////////////////////////////
					// set text with tags and move cursor to the beginning of the text
					// (scroll up)
					textPane.setText(buf);
					textPane.setCaretPosition(0);
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// look through all tags - remove from the text and save tag positions in tag_scheme
					for (String tag : taglist) {
						
						/////////////////////////////////////////////////////////////////
						if (tag.length() == 0)
							continue;
						/////////////////////////////////////////////////////////////////
						
						/////////////////////////////////////////////////////////////////
						String closing_tag = tag.replaceFirst("<", "</");
						/////////////////////////////////////////////////////////////////
						
						/////////////////////////////////////////////////////////////////
						buf = textPane.getText();
 						/////////////////////////////////////////////////////////////////
	
 						/////////////////////////////////////////////////////////////////
 						int len = tag.length();
						int len2 = closing_tag.length();
						/////////////////////////////////////////////////////////////////

						/////////////////////////////////////////////////////////////////
						// find data (by finding the opening and closing tag)
						int index1 = buf.indexOf(tag.substring(0,tag.length()-1));
						int index2 = buf.indexOf(closing_tag);
						int index3 = buf.indexOf(">",index1);
						len = index3 - index1 +1;
						/////////////////////////////////////////////////////////////////
	
						/////////////////////////////////////////////////////////////////
						// remove all current tag occurrences
						//   and color (highlight data)
						while (index1 != -1 && index2 != -1)
						{
							/////////////////////////////////////////////////////////////
							// remove opening and closing tags
							buf = buf.substring(0, index2) + buf.substring(index2 + len2, buf.length()); 
							correctTagPositions(index2, len2);
							buf = buf.substring(0, index1) + buf.substring(index1 + len, buf.length());
							correctTagPositions(index1, len);
							/////////////////////////////////////////////////////////////
	
							/////////////////////////////////////////////////////////////
							// save tag to hash map
							saveTagPosition(tag,index1,index2-len);
							/////////////////////////////////////////////////////////////
	
							/////////////////////////////////////////////////////////////
							// try to find the next tag occurrence
							index1 = buf.indexOf(tag.substring(0,tag.length()-1));
							index2 = buf.indexOf(closing_tag);
							
							index1 = -1;
							index2 = -1;
							/////////////////////////////////////////////////////////////
						}
						/////////////////////////////////////////////////////////////////
						
						/////////////////////////////////////////////////////////////////
						// correct text
						textPane.setText(buf);
						/////////////////////////////////////////////////////////////////
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// move scroll to the beginning
					textPane.setCaretPosition(0);
					
					/////////////////////////////////////////////////////////////////////
					// create drag and drop object and initialize it
					IDPDragAndDrop dnd = new IDPDragAndDrop();
					dnd.setCurTextPane(curTabContext.getCurTextPane());
					curTabContext.setDrag_and_drop(dnd);
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// highlight tags according to tag positions (tag_scheme), 
					//  color scheme (from properties) and selection
					for (String tag : taglist) {
						if (selected_tags.contains(tag) == true) tagNeedsColoring(tag, false, textView);
						if (tag_to_modify.compareTo(tag) == 0) tagNeedsColoring(tag, true, textView);
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// insert drag and drop components
					Offset left_right_offset = curTabContext.getTag_scheme().get(tag_to_modify);
					if (left_right_offset != null) 
					{
						int left_offset = left_right_offset.left; 
						int right_offset = left_right_offset.right;
						if (left_offset != 0 && right_offset != 0) {
							curTabContext.getDrag_and_drop().insertDragAndDropComponents(tag_to_modify,left_offset,right_offset);
						}
					}
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					treeManager.setCurrentScheme(curTabContext.getTag_scheme());
					IDPWindowManager.getJXMLTree().updateUI();
					/////////////////////////////////////////////////////////////////////
					
					/////////////////////////////////////////////////////////////////////
					// save DND context
					this.tabsDNDContext.put(textView, curTabContext);
					/////////////////////////////////////////////////////////////////////
				    
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Couldn't receive file from the server.\n"+
							"Try again or press disconnect\\connect.", "Error", JOptionPane.WARNING_MESSAGE);
				}
						
			} else {
				JOptionPane.showMessageDialog(null,
						"Can not display binary file", "Error",
						JOptionPane.ERROR_MESSAGE);
				
				IDPWindowManager.getJStatusLabel().setText(" Can not display binary file ");
			}
			/////////////////////////////////////////////////////////////////////////////
		}
		
		return true;
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Update the text in the current JTextPane - change the highlighting
	 * 
	 * @param aFileInfo - the data that identifies the JScrollPane in the tabs
	 * @param selected_tags - the list of selected tags from right JTree component
	 */
	public void updateView(TextFileInfo aFileInfo, String selected_tags) {
		
		/////////////////////////////////////////////////////////////////////////////////
		// get currentPane and JTextPane from it 
		JScrollPane currentPane = IDPWindowManager.getJTabbedPane().getTabInfo(aFileInfo);
		if (currentPane == null) return;
		JViewport viewport = (JViewport)currentPane.getComponent(0);
		if (viewport == null) return;
		JTextPane textPane = (JTextPane)viewport.getComponent(0);
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// getDNDContext
		//curDNDContext = this.tabsDNDContext.get(currentPane);
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		try {
			/////////////////////////////////////////////////////////////////////////////
			// get text from the current Text Pane
			String buf = textPane.getText();
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// delete d'n'd tags
			if (curTabContext.getDrag_and_drop() != null)
			{
				StringBuffer text = new StringBuffer();
				text.append(buf);
				text = curTabContext.getDrag_and_drop().DeleteDragAndDropTags(text);
				this.tabsDNDContext.put(currentPane,curTabContext);
				buf = text.toString();
			}
			/////////////////////////////////////////////////////////////////////////////			
			
			/////////////////////////////////////////////////////////////////////////////
			// get selected tag from right JTree component
			String tag_to_modify = "";
			TreePath[] paths = IDPWindowManager.getJXMLTree().getSelectionPaths();
								
			if (paths != null)
			{
				tag_to_modify = paths[0].getLastPathComponent().toString();
			}
			/////////////////////////////////////////////////////////////////////////////
			
			//globalTagList = "<i:post_date>";
			String[] taglist = globalTagList.split(";");
			
			/////////////////////////////////////////////////////////////////////////////
			// set text with tags and move cursor to the beginning of the text
			// (scroll up)
			textPane.setText(buf);
			textPane.setCaretPosition(0);
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// remove all previous highlighting
			StyledDocument doc = textPane.getStyledDocument();
			SimpleAttributeSet bgcolor = new SimpleAttributeSet();
			StyleConstants.setBackground(bgcolor, new Color(255,255,255));
			doc.setCharacterAttributes(0, buf.length(), bgcolor, false);
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////////////
			// highlight tags according to tag positions (tag_scheme), 
			//  color scheme (from properties) and selection
			for (String tag : taglist) {
				if (selected_tags.contains(tag) == true) tagNeedsColoring(tag, false, currentPane);
				if (tag_to_modify.compareTo(tag) == 0) tagNeedsColoring(tag, true, currentPane);
			}
			/////////////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////
			// insert drag and drop components
			Offset left_right_offset = curTabContext.getTag_scheme().get(tag_to_modify);
			if (left_right_offset != null) 
			{
				int left_offset = left_right_offset.left; 
				int right_offset = left_right_offset.right;
				if (left_offset != 0 && right_offset != 0) {
					curTabContext.getDrag_and_drop().insertDragAndDropComponents(tag_to_modify, left_offset, right_offset);
					this.tabsDNDContext.put(currentPane,curTabContext);
				}
			}
			/////////////////////////////////////////////////////////////////////
			
			/////////////////////////////////////////////////////////////////////
			IDPWindowManager.getCheckTreeManager().setCurrentScheme(curTabContext.getTag_scheme());
			//IDPWindowManager.getJXMLTree().updateUI();
			/////////////////////////////////////////////////////////////////////

		}
		finally
		{
		}
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Colors tag in the document according to tag position (got from tag_scheme), 
	 *  color scheme (got from properties) and XML tree node selection
	 *  
	 *   @param boolean b_save_color - true when tag = tag_to_modify so we need to keep in memory
	 *   a "bgcolor" value for drag and drop classes
	 */
	public void tagNeedsColoring(String tag, boolean b_save_color,JScrollPane currentPane)
	{
		/////////////////////////////////////////////////////////////////////////////////
		if (tag.length() == 0) return;
		String tag_name = tag.replace(":", "_");
		
		CheckTreeManager checkTreeManager = IDPWindowManager.getCheckTreeManager();
		Color tagColor = checkTreeManager.getTagColor(tag_name);
		
		////////////////////////////////////////////////////////////////////////////////
		// find tag position in tag scheme
		Offset offset = curTabContext.getTag_scheme().get(tag);
		if (offset == null) return;
		int index1 = offset.left; 
		int index2 = offset.right;
		////////////////////////////////////////////////////////////////////////////////
	
		////////////////////////////////////////////////////////////////////////////////
		// color (highlight) data
		SimpleAttributeSet bgcolor = new SimpleAttributeSet();
			
		/////////////////////////////////////////////////////////////////////////////////
		// color (highlight) text range
		StyleConstants.setBackground(bgcolor, tagColor);
		highlightRange(curTabContext.getCurTextPane(), index1 , index2 , bgcolor, true);
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// save the color
		if (b_save_color == true && curTabContext.getDrag_and_drop() != null) {
			curTabContext.getDrag_and_drop().setCurBgColor(bgcolor);
			this.tabsDNDContext.put(currentPane,curTabContext);
		}
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Color the text from position index1 to position index according to bgcolor
	 */
	public static void highlightRange(JTextPane text_pane, int index1, int index2, SimpleAttributeSet bgcolor, boolean match_modifiers)
	{
		/////////////////////////////////////////////////////////////////////////////////
		// get the interface for a generic styled document
		StyledDocument doc = text_pane.getStyledDocument();

		/////////////////////////////////////////////////////////////////////////////////
		// get the text for coloring 
		char[] str = text_pane.getText().toCharArray();
		
		/////////////////////////////////////////////////////////////////////////////////
		// StyledDocument ignores new line characters so 
		//  we need to find offset modifiers for both indexes
		int offset_md1 = 0;
		int offset_md2 = 0;
		if (match_modifiers == true)
		{
			for (int i = 0; i < index1; i++)
			{
				if (str[i] == '\r')	offset_md1++;
			}
			for (int i = index1; i < index2; i++)
			{
				if (str[i] == '\r')	offset_md2++;
			}
		}
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		// color the content
		doc.setCharacterAttributes(index1 - offset_md1, index2 - index1 - offset_md2, bgcolor, false);
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Just put left and right value of tag position to hash map with tag name as a key 
	 */
	public void saveTagPosition(String tag_name, int left, int right)
	{
		/////////////////////////////////////////////////////////////////////////////////
		// if tag exists then just modify else add it into hash map
		Offset offset = new Offset(left,right);
		curTabContext.getTag_scheme().put(tag_name, offset);
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Get tag scheme
	 */
	public HashMap<String, Offset> getTagScheme()
	{
		return curTabContext.getTag_scheme();
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Update key value in tag scheme.
	 */
	public void updateTagScheme(String key, Offset value)
	{
		curTabContext.getTag_scheme().put(key, value);
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *  Correct hash map.
	 *  If we delete some text then we should change tag positions of those
	 *  tags that are below
	 */
	private void correctTagPositions(int index, int len)
	{
		/////////////////////////////////////////////////////////////////////////////////
		// Get keys from hash map
		Set<String> keys = curTabContext.getTag_scheme().keySet();
		
		/////////////////////////////////////////////////////////////////////////////////
		// correct
		for (Object key : keys)
		{
			Offset offset = curTabContext.getTag_scheme().get(key);
			Offset new_offset = offset;
			if (offset.left > index)
			{
				new_offset.left -= len;
				curTabContext.getTag_scheme().put(key.toString(), new_offset);
			}
			if (offset.right > index)
			{
				new_offset.right -= len;
				curTabContext.getTag_scheme().put(key.toString(), new_offset);
			}
			
		}
		/////////////////////////////////////////////////////////////////////////////////
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Send corrected XML to the server.
	 */
	public void saveCurrentXML()
	{
		TextFileInfo aFileInfo = curTabContext.getCurTextFileInfo();
		
		////////////////////////////////////////////////////////////////////////////////
		// get currentPane and JTextPane from it 
		JScrollPane currentPane = IDPWindowManager.getJTabbedPane().getTabInfo(aFileInfo);
		if (currentPane == null) return;
		JViewport viewport = (JViewport)currentPane.getComponent(0);
		if (viewport == null) return;
		JTextPane textPane = (JTextPane)viewport.getComponent(0);
		////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////
		StringBuffer text = new StringBuffer();
		text.append(textPane.getText());
		////////////////////////////////////////////////////////////////////////////////
		
		////////////////////////////////////////////////////////////////////////////////
		try
		{
			////////////////////////////////////////////////////////////////////////////
			// Get keys from hash map
			Set<String> keys = curTabContext.getTag_scheme().keySet();
			int num = keys.size();
			Integer[] pos = new Integer[2*num];
			String[] tag_name = new String[2*num];
			
			////////////////////////////////////////////////////////////////////////////
			// put into separate array to sort it in future
			int i = 0;
			String tagName = "";
			for (Object key : keys)
			{
				Offset offset = curTabContext.getTag_scheme().get(key);
				pos[i] = offset.left;
				tagName = key.toString();
				//tagName = tagName.replace("i:", "");
				tag_name[i] = tagName; 
				i++;
				pos[i] = offset.right;
				tag_name[i] = tagName.replace("<","</");
				i++;
			}

			////////////////////////////////////////////////////////////////////////////
			// Sort tags positions
			
			// local variable
			//String l_currentXML = currentXML.replace("i:", "");
			String l_currentXML = curTabContext.getCurrentXML();
			
			int temp = 0;
			String stemp = "";
			for (i = 0; i < 2*num - 1; i++)
				for (int j = i + 1; j < 2*num; j++)
				{
					if (pos[i].compareTo(pos[j]) > 0)
					{
						temp = pos[i];
						pos[i] = pos[j];
						pos[j] = temp;
						stemp = tag_name[i];
						tag_name[i] = tag_name[j];
						tag_name[j] = stemp;
					}
					if (pos[i].compareTo(pos[j]) == 0)
					{
						String tag1 = tag_name[i];
						String tag2 = tag_name[j];
						int i1 = l_currentXML.indexOf(tag1);
						int i2 = l_currentXML.indexOf(tag2); 
						if (i1 > i2)
						{
							temp = pos[i];
							pos[i] = pos[j];
							pos[j] = temp;
							stemp = tag_name[i];
							tag_name[i] = tag_name[j];
							tag_name[j] = stemp;
						}
					}
				}
			
			////////////////////////////////////////////////////////////////////////////
			// *** Save
			
			String content = "";
			
			////////////////////////////////////////////////////////////////////////////
			// remove drag and drop tags first
			if (curTabContext.getDrag_and_drop() != null)
				text = curTabContext.getDrag_and_drop().ClearFromDragAndDropTags(text);
			////////////////////////////////////////////////////////////////////////////
			
			////////////////////////////////////////////////////////////////////////////
			// *** Get content
			
			////////////////////////////////////////////////////////////////////////////
			// 
			int beginIndex = pos[0];
			if (beginIndex > 0)
			{
				content = text.substring(0, beginIndex); 
			}

			////////////////////////////////////////////////////////////////////////////
			// 
			int endIndex = 0;
			tagName = "";
			for (i = 0; i < 2*num - 1; i++)
			{
				endIndex = pos[i+1];
				tagName = tag_name[i];
				
				// if it's root (first) tag then add xml namespace if exists
				if (i == 0 && curTabContext.getXmlns_prefix() != null & curTabContext.getXmlns_url() != null)
				{
					tagName = tagName.substring(0,tagName.length() - 1);
					tagName += " xmlns:" + curTabContext.getXmlns_prefix().substring(0,curTabContext.getXmlns_prefix().length() - 1) 
							+ "=\"" + curTabContext.getXmlns_url() + "\">";
				}
				
				content += tagName + text.substring(beginIndex,endIndex); 
				beginIndex = endIndex;
			};

			////////////////////////////////////////////////////////////////////////////
			// add last tag name 
			content += tag_name[2*num-1]; 
					
			////////////////////////////////////////////////////////////////////////////
			// concat the tail 
			if (endIndex < text.length())
			{
				content += text.substring(endIndex); 	
			}

			// *** Get content
			////////////////////////////////////////////////////////////////////////////
			
			////////////////////////////////////////////////////////////////////////////
			// Send content to the server
			MessagingInterface connection = IDPConnection.getConnection();
			connection.sendCorrected(current_filename, content);
			////////////////////////////////////////////////////////////////////////////
			
			// *** Save
			////////////////////////////////////////////////////////////////////////////
			
			refreashTree();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Couldn't get respond from the server.\n"+
					"Try again or press disconnect\\connect.", "Error", JOptionPane.WARNING_MESSAGE);
		}
		//////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////
		curTabContext.setChanged(false);
		IDPWindowManager.getJButtonSaveChanges().setEnabled(false);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// trace
		System.out.print("Saving...Done.");
		//////////////////////////////////////////////////////////////////
	}
	
	/**
	 * Refresh Tree
	 */
	public void refreashTree()
	{
		////////////////////////////////////////////////////////////////////////////
		// *** Refresh tree
		
		IDPContentTree jTree = IDPWindowManager.getJTree();
		TreePath selected = jTree.getSelectionPath();
		String[] str_path;
		String _str_path = selected.getLastPathComponent().toString();

		while (true)
		{
			selected = selected.getParentPath();
			if (selected == null) break;
			_str_path += ";" + selected.getLastPathComponent().toString();
		}
		
		str_path = _str_path.split(";");
		
		// tapped pane
		int index = IDPWindowManager.getJTabbedPane().getSelectedIndex();
		
		jTree.initTree();
		jTree.selectPath(str_path);
		
		IDPWindowManager.getJTabbedPane().setSelectedIndex(index);
		
		// *** Refresh tree
		////////////////////////////////////////////////////////////////////////////
		
	}
	
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		// Create and set up the window.
		SandBoxClient ex = IDPClientSingleton.getIDPClient();
		ex.setVisible(true);
		
	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Overridden so we can exit when window is closed */
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0);
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This is the default constructor
	 */
	public SandBoxClient() {
		super();
		initialize();
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(700, 700);
		this.setContentPane(IDPWindowManager.getJContentPane());
		this.setTitle("SandBoxClient");
		
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
		screen_width = scrnRect.width;
		screen_height = scrnRect.height;
		
		//////////////////////////////////////////////////////////////////
		// glass pane
		jGlassPane = new IDPGlassPane(IDPGlassPane.defaultWatermarkAlpha);
		this.setGlassPane(jGlassPane);
		jGlassPane.setVisible(true);
		jGlassPane.setScreenWidth(screen_width);
		jGlassPane.setScreenHeight(screen_height);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		this.setJMenuBar(IDPWindowManager.getJMenuBar());
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		setupHotKeys();
		//////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		if (MAXIMIZED == 1) setExtendedState(JFrame.MAXIMIZED_BOTH);
		/////////////////////////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////////////////////////////////
		addComponentListener(this);
		/////////////////////////////////////////////////////////////////////////////////
		
	}
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Quit the application. 
	 */
	public void quit() {
		System.exit(0);
	}

	/**
	 * Install application hot keys.
	 */
	private void setupHotKeys()
	{
		//////////////////////////////////////////////////////////////////
		// About
		Action showAbout = new AbstractAction() {
		    
			/**
			 * version UID
			 */
			private static final long serialVersionUID = 4423309510641771509L;
			
		    public void actionPerformed(ActionEvent e) {
		    	glassPaneFadeIn(IDPGlassPane.IDPDialogs.AboutDialog);
		    }
		};
		IDPWindowManager.getJMenuBar().getInputMap().put(KeyStroke.getKeyStroke("F1"),
        "showAbout");
		IDPWindowManager.getJMenuBar().getActionMap().put("showAbout",
				showAbout);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// Settings
		Action showSettings = new AbstractAction() {
		    /**
			 * version UID
			 */
			private static final long serialVersionUID = 4423309510641771509L;

			public void actionPerformed(ActionEvent e) {
		    	glassPaneFadeIn(IDPGlassPane.IDPDialogs.SettingsDialog);
		    }
		};
		IDPWindowManager.getJMenuBar().getInputMap().put(KeyStroke.getKeyStroke("F10"),
        "showSettings");
		IDPWindowManager.getJMenuBar().getActionMap().put("showSettings",
				showSettings);
		//////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////
		// we add this manager to prevent focusing on menu when ALT key is pressed
		DefaultKeyboardFocusManager keyManager = new DefaultKeyboardFocusManager();
		KeyboardFocusManager.setCurrentKeyboardFocusManager(keyManager);
		keyManager.addKeyEventDispatcher(new KeyEventDispatcher(){
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				return false;
			}
		});
		//////////////////////////////////////////////////////////////////

	}
	
	/**
	 * Update all necessary stuff. 
	 * (current tabbed pane context, XML source and reference for JXMLTree, textview)
	 */
	public void updateContext(JScrollPane jScrollPane)
	{
		curTabContext = tabsDNDContext.get(jScrollPane);
		//////////////////////////////////////////////////////////////////
		// can be null when we open tab for the first time (by clicking on left IDPContentTree)
		// is not null if we select tab in tabbed pane
		if (curTabContext != null)  
		{
			IDPWindowManager.getJXMLTree().setCurrentXML(curTabContext.getCurrentXML());
			IDPWindowManager.getJXMLTree().setCurrentPane(curTabContext.getCurTextPane());
			IDPWindowManager.getJButtonSaveChanges().setEnabled(curTabContext.isChanged() == true);
			
			/////////////////////////////////////////////////////////////////////
			TextFileInfo aFileInfo = curTabContext.getCurTextFileInfo();
			current_filename = aFileInfo.getRelativePath();
			int last_index = current_filename.lastIndexOf("\\");
			if (last_index != -1)
			{
				current_filename = current_filename.substring(last_index+1);
			}
			/////////////////////////////////////////////////////////////////////

		}
		//////////////////////////////////////////////////////////////////
		updatePane();
	}
	
	/**
	 * Change alpha for glasspane. Used to change translucent parameter for water mark (IDP).
	 */
	public void setGlassPaneAlpha(double alpha)
	{
		jGlassPane.setAlpha(alpha);
		if (alpha == 0.0)
		{
			jGlassPane.setVisible(false);
		}
		else jGlassPane.setVisible(true);
	}
	
	public void turnOffWaterMark()
	{
		jGlassPane.setWithLoadmark(false);
		jGlassPane.repaint();
	}
	
	public void turnOnWaterMark()
	{
		jGlassPane.setWithLoadmark(true);
		jGlassPane.repaint();
	}

	/**
	 * Show dialog
	 * @param dialog - dialog to show
	 */
	public void glassPaneFadeIn(IDPGlassPane.IDPDialogs dialog)
	{
		jGlassPane.fadeIn(dialog);
	}
	
	/**
	 * Show dialog with two params
	 * @param dialog - dialog to show
	 * @param title - first param
	 * @param text - second param
	 */
	public void glassPaneFadeIn(IDPGlassPane.IDPDialogs dialog, String title, String message, IDPMessageDlg.messageType messageType)
	{
		jGlassPane.fadeIn(dialog, title, message, messageType);
	}
	
	/**
	 * Get xmlTreeRoot
	 * @return
	 */
	public DefaultMutableTreeNode getXmlTreeRoot() {
		return xmlTreeRoot;
	}
	
	/**
	 * Set tab info button bounds.
	 * @param hiddenTabsCount 
	 */
	public void updateTabInfoBounds(int hiddenTabsCount)
	{
		Rectangle r;
		
		// get container width
		IDPTabbedPane jTabbedPane = IDPWindowManager.getJTabbedPane();
		r = jTabbedPane.getBounds();
		int w = r.width;
		
		// set width
		JSplitPane jMiddleSplitPane = IDPWindowManager.getJMiddleSplitPane();
		r = jMiddleSplitPane.getBounds();
		r.width = w;
		
		// add menu height
		JMenuBar menuBar = IDPWindowManager.getJMenuBar();
		Rectangle rect = menuBar.getBounds();
		r.y += rect.height;
		
		// get container x 
		JSplitPane jInnerSplitPan = IDPWindowManager.getJInnerSplitPane();
		rect = jInnerSplitPan.getBounds();
		r.x = rect.x;
		
		//jGlassPane.setRect(r);
		jGlassPane.setTabScrollerInfo(hiddenTabsCount);
		jGlassPane.setTabButtonLocation(r.x + r.width, r.y);
	}
	
	/**
	 * Update the number of unhidden tabs in TabbedPaneScroller.
	 * @param hiddenTabsCount - number of unhidden tabs
	 * @see TabbedPaneScroller
	 */
	public void updateTabInfo(int hiddenTabsCount)
	{
		jGlassPane.setTabScrollerInfo(hiddenTabsCount);
	}
	
	/**
	 * Change TabbedPaneScroller component visibility
	 */
	public void setTabButtonVisibility(boolean bVisible)
	{
		if (bVisible == false)
		{
			jGlassPane.setTabScrollerInfo(0);
		}
	}
	
	/**
	 * Get screen width. 
	 * (Also updates inner variables)
	 * @return
	 */
	public int getScreenWidth()
	{
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
		screen_width = scrnRect.width;
		screen_height = scrnRect.height;
		
		return screen_width;
	}
	
	public void discardCurrentDialog()
	{
		jGlassPane.discardCurrentDialog();
	}
	
	/**
	 * Get screen height. 
	 * (Also updates inner variables)
	 * @return
	 */
	public int getScreenHeight()
	{
		Rectangle scrnRect = getGraphicsConfiguration().getBounds();
		screen_width = scrnRect.width;
		screen_height = scrnRect.height;
		
		return screen_height;
	}


	@Override
	public void keyPressed(KeyEvent arg0) {
		// do nothing
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// do nothing
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		// do nothing
	}


	@Override
	public void componentHidden(ComponentEvent arg0) {
		// do nothing
	}


	@Override
	public void componentMoved(ComponentEvent arg0) {
		// do nothing
	}


	@Override
	public void componentResized(ComponentEvent arg0) {
		jGlassPane.repaintAll();
	}


	@Override
	public void componentShown(ComponentEvent arg0) {
		// do nothing
	}
	
	/**
	 * Set the flag to show that some changes were made so we need to save current xml on the server
	 * This flag is used to activate\deactivate "Save changes" button according to tab context and
	 * to ask user to save changes when tab with updated context is closed
	 * 
	 * @param isChanged
	 *  
	 * @see IDPWindowsManager.getJButtonSaveChanges
	 */
	public void setCurContextIsChanged(boolean isChanged)
	{
		curTabContext.setChanged(isChanged);
	}
	
	/**
	 * Get current context state
	 * 
	 * @return
	 */
	public boolean isCurContextChanged() 
	{
		if (curTabContext == null) return false;
		
		return curTabContext.isChanged();
	}
	
	/**
	 * main
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	/////////////////////////////////////////////////////////////////////////////////
	
} 
