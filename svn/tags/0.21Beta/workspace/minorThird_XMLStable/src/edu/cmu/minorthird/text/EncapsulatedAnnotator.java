package edu.cmu.minorthird.text;

import edu.cmu.minorthird.util.*;
import edu.cmu.minorthird.text.*;
import edu.cmu.minorthird.text.mixup.MixupProgram;

import java.io.*;

/**
 * An annotator that 'requires' some type of annotation, but exports
 * only a selected set of spanTypes (maybe all of them) from the
 * annotated documents.  The 'require' call is done in the context of
 * a particular set of files (e.g. mixup files). This is a wrapper
 * around EncapsulatingAnnotatorLoader.
 *
 * @author William Cohen
 */

public class EncapsulatedAnnotator extends AbstractAnnotator implements Serializable
{
    private String requiredAnnotation;
    private String[] exportedTypes = null;
    private AnnotatorLoader annotatorLoader = null;

    public EncapsulatedAnnotator(String requiredAnnotation,String path)	{ this(requiredAnnotation,path,(String[])null);	}

    public EncapsulatedAnnotator(String requiredAnnotation,String path,String type)	{ this(requiredAnnotation,path,new String[]{type});	}

    /**
     * @param exportedTypes - only span types from this list will actually
     * by exported by the annotator
     */
    public EncapsulatedAnnotator(String requiredAnnotation,String path,String[] exportedTypes)	
    { 
	this.requiredAnnotation = requiredAnnotation; 
	this.exportedTypes = exportedTypes;
	this.annotatorLoader = new EncapsulatingAnnotatorLoader(path);
	System.out.println("will export these types: " + (exportedTypes==null ? "all" : StringUtil.toString(exportedTypes)) );;
    }

    protected void doAnnotate(MonotonicTextLabels labels)
    { 
		
	if (exportedTypes==null) {
	    //System.out.println("exporting all types");
	    labels.require(requiredAnnotation,null,annotatorLoader);
	} else {
	    // just export a few span types
	    //System.out.println("export these types: "+StringUtil.toString(exportedTypes));
	    //System.out.println("labels: "+labels);
	    NestedTextLabels tempLabels = new NestedTextLabels(labels);
	    tempLabels.require(requiredAnnotation,null,annotatorLoader);
	    for (int i=0; i<exportedTypes.length; i++) {
		//System.out.println("exporting type"+exportedTypes[i]);
		for (Span.Looper j=tempLabels.instanceIterator(exportedTypes[i]); j.hasNext(); ) {
		    Span span = j.nextSpan();
		    labels.addToType(span,exportedTypes[i]);
		}
	    }
	    //System.out.println("tempLabels: "+tempLabels);
	}
    }

    public String explainAnnotation(TextLabels labels, Span documentSpan) { return "annotated with '"+requiredAnnotation+"'"; }

    /** Create a serialized annotator that 'requires' a particular
     * type of annotation in the context of a particular set of files.
     */

    public static void main(String[] args) throws IOException
    {
	if (args.length<3) {
	    System.out.println("usage: save-file requiredAnnotation path [exportedSpan1 ... ]");
	}
	EncapsulatedAnnotator ann = null;
	if (args.length==3) {
	    ann = new EncapsulatedAnnotator(args[1],args[2]);
	} else if (args.length>3) {
	    String[] exportedTypes = new String[args.length-3];
	    for (int i=0; i<exportedTypes.length; i++) exportedTypes[i] = args[i+3];
	    ann = new EncapsulatedAnnotator(args[1],args[2],exportedTypes);
	}
	IOUtil.saveSerialized(ann,new File(args[0]));
    }
}
