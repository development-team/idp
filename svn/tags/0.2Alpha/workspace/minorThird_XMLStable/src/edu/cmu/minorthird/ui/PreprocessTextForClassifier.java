package edu.cmu.minorthird.ui;

import edu.cmu.minorthird.classify.*;
import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.text.*;
import edu.cmu.minorthird.text.learn.*;
import edu.cmu.minorthird.util.gui.*;
import edu.cmu.minorthird.util.*;

import org.apache.log4j.Logger;
import java.util.*;
import java.io.*;


/**
 * Preprocess text data for classification.
 *
 * @author William Cohen
 */

public class PreprocessTextForClassifier extends UIMain
{
    private static final String defaultEncoding = "UTF8";

	private static Logger log = Logger.getLogger(PreprocessTextForClassifier.class);

    protected String linkFileName = null;
    protected SpanFeatureExtractor fe = new Recommended.DocumentFE();
    protected CommandLineUtil.SaveParams save = new CommandLineUtil.SaveParams();
    protected CommandLineUtil.ClassificationSignalParams signal = new CommandLineUtil.ClassificationSignalParams(base);
    protected Dataset dataset;


    public class LinkFileParams extends BasicCommandLineProcessor 
    {
	public String linkFileHelp = "file to save mapping between examples and spans they correspond to";
	public void linkFile(String s) { linkFileName=s; }
	public CommandLineProcessor fe(String s) { 
	    fe = (SpanFeatureExtractor)CommandLineUtil.newObjectFromBSH(s,SpanFeatureExtractor.class); 
	    return (fe instanceof CommandLineProcessor.Configurable)? tryToGetCLP(fe) : null;
	}
	public CommandLineProcessor feOp() {  return tryToGetCLP(fe); }
	public void usage() {
	    System.out.println("special parameters:");
	    System.out.println(" [-linkFile FILE]           " + linkFileHelp);
	    System.out.println(" [-fe beanshell]            " + "feature extractor");
	    System.out.println(" [-feOp opt1 ...]           " + "options for feature extractor");
	    System.out.println();
	    
	}
	public String getLinkFileHelp() { return linkFileHelp; }
    } 
    public CommandLineProcessor getCLP() {
	return new JointCommandLineProcessor(new CommandLineProcessor[]{ 
	    new LinkFileParams(),gui,base,signal,save});
    }
    public String getLinkFile() { return linkFileName; }
    public void setLinkFile(String s) { linkFileName=s; }
    public SpanFeatureExtractor getFeatureExtractor() { return fe; }
    public void setFeatureExtractor(SpanFeatureExtractor fe) {  this.fe=fe; }


    public CommandLineUtil.ClassificationSignalParams getSignalParameters() { return signal; } 
    public void setSignalParameters(CommandLineUtil.ClassificationSignalParams p) { signal=p; }

    //
    // do it
    // 

    public void doMain()
    {
	// check that inputs are valid
	if (signal.spanProp==null && signal.spanType==null) {
	    throw new IllegalArgumentException("one of -spanProp or -spanType must be specified");
	}
	if (signal.spanProp!=null && signal.spanType!=null) {
	    throw new IllegalArgumentException("only one of -spanProp or -spanType can be specified");
	}
	if (save.saveAs==null) {
	    throw new IllegalArgumentException("-saveAs must be specified");
	}

	// construct the dataset and save it
	//if (tagDataFlag) {
	//	    dataset = 
	//SequenceAnnotatorLearner.prepareSequenceData(base.labels,signal.spanProp,signal.spanType,fe,historySize,reduction);

	dataset = CommandLineUtil.toDataset(base.labels,fe,signal.spanProp,signal.spanType,signal.candidateType);
	try {
	    DatasetLoader.save(dataset, save.saveAs);
	} catch (IOException ex) {
	    System.out.println("error saving dataset to '"
			       +save.saveAs+"': "+ex);
	}

	if (base.showResult) {
	    new ViewerFrame("Dataset", dataset.toGUI());
	}

	if (linkFileName!=null) {
	    try {
		saveLinkInfo(new File(linkFileName),dataset,save.getSaveAs());
	    } catch (IOException ex) {
		System.out.println("error saving link information to '"
				   +linkFileName+"': "+ex);
	    }
	}

    }

    private void saveLinkInfo(File linkFile,Dataset dataset,String datasetFileName) throws IOException
    {
	int lineNo = 0;
	// PrintStream out = new PrintStream(new FileOutputStream(linkFile));
	PrintStream out = new PrintStream(new FileOutputStream(linkFile), false, defaultEncoding);
	for (Example.Looper i=dataset.iterator(); i.hasNext(); ) {
	    Example ex = i.nextExample();
	    lineNo++;
	    if (!(ex.getSource() instanceof Span)) {
		throw new IllegalArgumentException("example not associated with a span: "+ex);
	    }
	    Span span = (Span)ex.getSource();
	    out.println( DatasetLoader.getSourceAssignedToExample(datasetFileName,lineNo) 
			 +" "+ span.getDocumentId()+" "+span.getLoChar()+" "+(span.getHiChar()-span.getLoChar()));
	}
	out.close();
    }

    public Object getMainResult() { return dataset; }

    public static void main(String args[])
    {
	new PreprocessTextForClassifier().callMain(args);
    }
}
