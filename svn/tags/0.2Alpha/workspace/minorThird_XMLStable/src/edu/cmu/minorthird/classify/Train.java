/* Copyright 2003, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify;

import edu.cmu.minorthird.classify.multi.*;
import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.classify.algorithms.linear.*;
import edu.cmu.minorthird.classify.algorithms.trees.*;
import edu.cmu.minorthird.classify.algorithms.svm.*;
import edu.cmu.minorthird.classify.algorithms.knn.*;
import edu.cmu.minorthird.classify.multi.*;
import edu.cmu.minorthird.classify.transform.*;
import edu.cmu.minorthird.classify.sequential.*;
import edu.cmu.minorthird.util.*;
import edu.cmu.minorthird.util.gui.*;

import java.util.*;

// Don't use: import java.io.*;  Because this causes java.io.Console to be included, 
// which conflicts with edu.cmu.minorthird.util.gui.Console
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import org.apache.log4j.*;

/**
 * Main UI program for the 'classify' package. 
 *
 * @author William Cohen
 */

public class Train
{
    private static Logger log = Logger.getLogger(UI.class);

    private static final Class[] SELECTABLE_TYPES = new Class[]{
        DataClassificationTask.class,
        ClassifyCommandLineUtil.SimpleTrainParams.class, ClassifyCommandLineUtil.MultiTrainParams.class, ClassifyCommandLineUtil.SeqTrainParams.class,
        ClassifyCommandLineUtil.Learner.SequentialLnr.class, ClassifyCommandLineUtil.Learner.ClassifierLnr.class,
        KnnLearner.class, NaiveBayes.class,
        VotedPerceptron.class,	SVMLearner.class,
        DecisionTreeLearner.class, AdaBoost.class,
        BatchVersion.class, TransformingBatchLearner.class,
        MaxEntLearner.class,
        // transformations
        FrequencyBasedTransformLearner.class, InfoGainTransformLearner2.class,
        T1InstanceTransformLearner.class, TFIDFTransformLearner.class,
        // sequential learner
        CollinsPerceptronLearner.class, GenericCollinsLearner.class,
        // splitters
        CrossValSplitter.class, RandomSplitter.class, StratifiedCrossValSplitter.class,
    };

    private static final Set LEGAL_OPS = new HashSet(Arrays.asList(new String[]{"train","test","trainTest"}));

    public static class DataClassificationTask implements CommandLineProcessor.Configurable,/*Saveable,*/ Console.Task
    {
        private ClassifyCommandLineUtil.TrainParams trainParams = new ClassifyCommandLineUtil.TrainParams();

        public Object resultToShow;
        public boolean useGUI;
        public Console.Task main;

        // for gui
	public ClassifyCommandLineUtil.TrainParams getTrainParams() {return trainParams;}
	public void setTrainParams(ClassifyCommandLineUtil.TrainParams train) {trainParams = train;}
	public String getTrainParamsHelp() { return "Define what type of experiment you would like to run: <br>" +
		"Simple - Standard classify experiment <br> " +
		"Multi  - Classify Experiment with Multiple labels per example <br>" +
		"Seq    - Classify experiment with a Sequential Dataset, where each example has a history, <br> " +
		"          and uses a Sequential Learner"; }
        protected class GUIParams extends BasicCommandLineProcessor {
            public void gui() { 
		useGUI=true; 
		if(trainParams.type != null)
		    trainParams = trainParams.type;
		else trainParams = new ClassifyCommandLineUtil.SimpleTrainParams();
	    }
            public void usage() {
                System.out.println("presentation parameters:");
                System.out.println(" -gui                     use graphic interface to set parameters");
                System.out.println();
            }
        }
        public String getDatasetFilename() { return trainParams.trainDataFilename; }

        public CommandLineProcessor getCLP() {
            JointCommandLineProcessor jlpTrain = new JointCommandLineProcessor(new CommandLineProcessor[]
            {new GUIParams(),trainParams,trainParams});
                return jlpTrain;
        }


        /** Returns whether base.labels exits */
        public boolean getLabels(){
            return (getDatasetFilename() != null);
        }

	public MultiDataset annotateData(MultiDataset md) {
	    MultiDataset annotatedDataset = new MultiDataset();
	    Splitter splitter = new CrossValSplitter(9);
	    MultiDataset.MultiSplit s = md.MultiSplit(splitter);
	    for (int x=0; x<9; x++) {
		MultiClassifierTeacher teacher = new MultiDatasetClassifierTeacher(s.getTrain(x));
		MultiClassifier c = teacher.train(trainParams.clsLnr.clsLearner);
		for(MultiExample.Looper i = s.getTest(x).multiIterator(); i.hasNext(); ) {
		    MultiExample ex = i.nextMultiExample();		
		    Instance instance = ex.asInstance();
		    MultiClassLabel predicted = c.multiLabelClassification(instance);
		    Instance annotatedInstance = new InstanceFromPrediction(instance, predicted.bestClassName());
		    MultiExample newEx = new MultiExample(annotatedInstance, ex.getMultiLabel(), ex.getWeight());
		    annotatedDataset.addMulti(newEx);
		}
	    }
	    return annotatedDataset;
	}

        // main action
        public void doMain()
        {
            if (trainParams.trainData==null) {
                System.out.println("The training data needs to be specified with the -data option.");
                return;
            }
            if ((trainParams.typeString.equals("seq")) && (!(trainParams.trainData instanceof SequenceDataset))) {
                System.out.println("The training data should be a sequence dataset");
                return;
            }
            if (trainParams.showData) new ViewerFrame("Training data",trainParams.trainData.toGUI());
          
	    if (trainParams.typeString.equals("seq")) {
                    DatasetSequenceClassifierTeacher teacher = new DatasetSequenceClassifierTeacher((SequenceDataset)trainParams.trainData);
                    SequenceClassifier c = teacher.train(trainParams.seqLnr.seqLearner);
                    trainParams.resultToShow = trainParams.resultToSave = c;
                } else if (trainParams.typeString.equals("multi")) {
		    MultiDataset multiData;
		    if(trainParams.crossDim)
			multiData = annotateData((MultiDataset)trainParams.trainData);
		    else multiData = (MultiDataset)trainParams.trainData;
		    MultiClassifierTeacher teacher = new MultiDatasetClassifierTeacher(multiData);
		    MultiClassifier c = teacher.train(trainParams.clsLnr.clsLearner);
		    trainParams.resultToShow = trainParams.resultToSave = c;
		} else {
                    ClassifierTeacher teacher = new DatasetClassifierTeacher(trainParams.trainData);
                    Classifier c = teacher.train(trainParams.clsLnr.clsLearner);
                    trainParams.resultToShow = trainParams.resultToSave = c;
                }
                resultToShow=trainParams.resultToShow;
		if (trainParams.saveAs!=null) {
		    if (IOUtil.saveSomehow(trainParams.resultToSave,trainParams.saveAs)) {
			log.info("Result saved in "+trainParams.saveAs);
		    } else {
			log.error("Can't save "+trainParams.resultToSave.getClass()+" to "+trainParams.saveAs);
		    }
		}		
            
            if (trainParams.showResult) new ViewerFrame("Result", new SmartVanillaViewer(trainParams.resultToShow));
            if (trainParams.saveAs!=null) {
                if (IOUtil.saveSomehow(trainParams.resultToSave,trainParams.saveAs)) {
                    log.info("Result saved in "+trainParams.saveAs);
                } else {
                    log.error("Can't save "+trainParams.resultToSave.getClass()+" to "+trainParams.saveAs);
                }
            }
        }
        public Object getMainResult()
        {
            return resultToShow;
        }

        //
        // implements Saveable
        //
        /*public String[] getFormatNames() { return clp.getFormatNames(); }
        public String getExtensionFor(String format) { return clp.getExtensionFor(format); }
        public void saveAs(File file, String format) throws IOException { clp.saveAs(file,format); }
        public Object restore(File file) throws IOException
        {
        DataClassificationTask task = new DataClassificationTask();
        task.clp.config(file.getAbsolutePath());
        return task;
        }*/

        // gui around main action
        public void callMain(final String[] args)
        {
            try {
                getCLP().processArguments(args);
                if (!useGUI) {
                    doMain();
                }
                else {
                    main = this;
                    final Viewer v = new ComponentViewer() {
                        public JComponent componentFor(Object o)
                        {
                            Viewer ts = new TypeSelector(SELECTABLE_TYPES, "selectableTypes.txt", DataClassificationTask.class);
                            ts.setContent(o);

                            // we'll put the type selector in a nice panel
                            JPanel panel = new JPanel();
                            panel.setBorder(new TitledBorder(StringUtil.toString(args,"Command line: ",""," ")));
                            panel.setLayout(new GridBagLayout());
                            GridBagConstraints gbc;

                            // another panel to allow parameter modifications
                            JPanel subpanel1 = new JPanel();
                            subpanel1.setBorder(new TitledBorder("Parameter modification"));
                            //subpanel1.add(new JLabel("Use the edit button to change the parameters given in the command line"));
                            subpanel1.add( ts );
                            gbc = Viewer.fillerGBC(); gbc.weighty=0;
                            panel.add( subpanel1, gbc  );

                            // a control panel for controls
                            JPanel subpanel2 = new JPanel();
                            subpanel2.setBorder(new TitledBorder("Execution controls"));
                            // a button to show the results
                            final JButton viewButton = new JButton(new AbstractAction("View results") {
                                public void actionPerformed(ActionEvent event) {
                                    Viewer rv = new SmartVanillaViewer();
                                    rv.setContent( getMainResult() );
                                    ViewerFrame f = new ViewerFrame("Result", rv);
                                }
                            });
                            viewButton.setEnabled(false);

                            // another panel for error messages and other outputs
                            JPanel errorPanel = new JPanel();
                            errorPanel.setBorder(new TitledBorder("Error messages and output"));
                            final Console console = new Console(main, getDatasetFilename() != null, viewButton);
                            errorPanel.add(console.getMainComponent());

                            // a button to start this thread
                            JButton goButton = new JButton(new AbstractAction("Start task") {
                                public void actionPerformed(ActionEvent event) {
                                    console.start();
                                }
                            });
                            // and a button to show the current labels
                            JButton showLabelsButton = new JButton(new AbstractAction("Show train data") {
                                public void actionPerformed(ActionEvent ev) {
                                    new ViewerFrame("Labeled TextBase", new SmartVanillaViewer(trainParams.trainData));
                                }
                            });
                            // and a button to clear the errorArea
                            JButton clearButton = new JButton(new AbstractAction("Clear window") {
                                public void actionPerformed(ActionEvent ev) {
                                    console.clear();
                                }
                            });
                            // and a button for help
                            JButton helpParamsButton = new JButton(new AbstractAction("Parameters") {
                                public void actionPerformed(ActionEvent ev) {
                                    PrintStream oldSystemOut = System.out;
                                    ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
                                    System.setOut(new PrintStream(outBuffer));
                                    //clp.usage();
                                    console.append(outBuffer.toString());
                                    System.setOut(oldSystemOut);
                                }
                            });
                            subpanel2.add( goButton );
                            subpanel2.add( viewButton );
                            subpanel2.add( showLabelsButton );
                            subpanel2.add( clearButton );
                            subpanel2.add( new JLabel("Help:") );
                            subpanel2.add( helpParamsButton );
                            gbc = Viewer.fillerGBC();	gbc.weighty=0; gbc.gridy=1;
                            panel.add(subpanel2, gbc );

                            gbc = Viewer.fillerGBC(); gbc.weighty=1; gbc.gridy=2;
                            panel.add(errorPanel, gbc);

                            // now some progress bars
                            JProgressBar progressBar1 = new JProgressBar();
                            JProgressBar progressBar2 = new JProgressBar();
                            JProgressBar progressBar3 = new JProgressBar();
                            ProgressCounter.setGraphicContext(new JProgressBar[]{progressBar1, progressBar2,progressBar3});
                            gbc = Viewer.fillerGBC();	gbc.weighty=0; gbc.gridy=3;
                            panel.add(progressBar1, gbc);
                            gbc = Viewer.fillerGBC(); gbc.weighty=0; gbc.gridy=4;
                            panel.add(progressBar2, gbc);
                            gbc = Viewer.fillerGBC(); gbc.weighty=0; gbc.gridy=5;
                            panel.add(progressBar3, gbc);

                            return panel;
                        }
                    };
                    v.setContent(this);
                    String className = this.getClass().toString().substring("class ".length());
                    ViewerFrame f = new ViewerFrame(className + ": " + Version.getVersion() ,v);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Use option -help for help");
            }
        }
    }

    public static void main(String[] args) {
        new DataClassificationTask().callMain(args);
    }
}


