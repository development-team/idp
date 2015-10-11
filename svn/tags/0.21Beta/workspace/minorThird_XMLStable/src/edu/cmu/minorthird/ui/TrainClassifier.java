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
 * Train a text classifier.
 *
 * @author William Cohen
 */

public class TrainClassifier extends UIMain
{
    private static Logger log = Logger.getLogger(TrainClassifier.class);

    // private data needed to train a classifier

    private CommandLineUtil.SaveParams save = new CommandLineUtil.SaveParams();
    private CommandLineUtil.ClassificationSignalParams signal = new CommandLineUtil.ClassificationSignalParams(base);
    private CommandLineUtil.TrainClassifierParams train = new CommandLineUtil.TrainClassifierParams();
    private Classifier classifier = null;

    public CommandLineUtil.SaveParams getSaveParameters() { return save; }
    public void setSaveParameters(CommandLineUtil.SaveParams p) { save=p; }
    public CommandLineUtil.ClassificationSignalParams getSignalParameters() { return signal; } 
    public void setSignalParameters(CommandLineUtil.ClassificationSignalParams p) { signal=p; } 
    public CommandLineUtil.TrainClassifierParams getAdditionalParameters() { return train; } 
    public void setAdditionalParameters(CommandLineUtil.TrainClassifierParams p) { train=p; } 

    public String getTrainClassifierHelp() {
	return "<A HREF=\"http://minorthird.sourceforge.net/tutorials/TrainClassifier%20Tutorial.htm\">TrainClassifier Tutorial</A></html>";
    }

    public CommandLineProcessor getCLP()
    {
	return new JointCommandLineProcessor(new CommandLineProcessor[]{gui,base,save,signal,train});
    }

    //
    // do the experiment
    // 

    public void doMain()
    {
	// check that inputs are valid
	if (train.learner==null) throw new IllegalArgumentException("-learner must be specified");
	if (signal.spanProp==null && signal.spanType==null) 
	    throw new IllegalArgumentException("one of -spanProp or -spanType must be specified");
	if (signal.spanProp!=null && signal.spanType!=null) 
	    throw new IllegalArgumentException("only one of -spanProp or -spanType can be specified");

	// construct the dataset
	Dataset d = 
	    CommandLineUtil.toDataset(base.labels,train.fe,signal.spanProp,signal.spanType,signal.candidateType);
	if (train.showData) {
	    System.out.println("Trying to show the Dataset");
	    new ViewerFrame("Dataset", d.toGUI());
	}

	/*Dataset seqDataset = 
	  CommandLineUtil.toSeqDataset(base.labels,train.fe,signal.spanProp,"combined");
		
	  if (train.showData) {
	  System.out.println("Trying to create Sequential Dataset");
	  new ViewerFrame("SequenceDataset", seqDataset.toGUI());
	  }*/

	// train the classifier
	classifier = new DatasetClassifierTeacher(d).train(train.learner);

	if (base.showResult) {
	    Viewer cv = new SmartVanillaViewer();
	    cv.setContent(classifier);
	    new ViewerFrame("Classifier",cv); 
	}

	String type = signal.getOutputType(train.output);
	String prop = signal.getOutputProp(train.output);
	ClassifierAnnotator ann = new ClassifierAnnotator(train.fe,classifier,type,prop,signal.candidateType);

	if (save.saveAs!=null) {
	    try {
		IOUtil.saveSerialized((Serializable)ann,save.saveAs);
	    } catch (IOException e) {
		throw new IllegalArgumentException("can't save to "+save.saveAs+": "+e);
	    }
	}
    }

    public Object getMainResult() { return classifier; }

    public static void main(String args[])
    {
	new TrainClassifier().callMain(args);
    }
}
