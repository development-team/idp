/* Copyright 2003, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify;

import edu.cmu.minorthird.classify.experiments.Expt;
import edu.cmu.minorthird.classify.experiments.Tester;
import edu.cmu.minorthird.util.IOUtil;
import edu.cmu.minorthird.util.gui.ViewerFrame;
import edu.cmu.minorthird.util.gui.Visible;
import edu.cmu.minorthird.classify.relational.*;
import java.io.*;
import java.util.*;

/**
 * Trains a StackedClassifierLearner using the information in  a labeled relational Dataset.
 *
 * @author Zhenzhen Kou
 *
 */
public class StackedDatasetClassifierTeacher extends StackedClassifierTeacher
{
	private Dataset dataset;
	private boolean activeLearning=false;

	public StackedDatasetClassifierTeacher(Dataset dataset) 
	{
		this(dataset,false);
	}

	/**
	 * @param activeLearning if true, all learning is active - ie nothing is
	 * pushed at the learner, everything must be 'pulled' via queries.
	 * if false, all examples fron the dataset are 'pushed' at the learner
	 * via addExample.
	 */
	public StackedDatasetClassifierTeacher(Dataset dataset,boolean activeLearning) 
	{ 
		this.dataset=dataset; 
		this.activeLearning = activeLearning; 
	}

	public ExampleSchema schema()
	{
		return dataset.getSchema();
	}

	public HashMap getLinksMap()
	{
		return ((RealRelationalDataset)dataset).getLinksMap();
	}
	public HashMap getAggregators(){
		return ((RealRelationalDataset)dataset).getAggregators();
	}
	
	public Example.Looper examplePool() 
	{ 
//		System.out.println(dataset);
		return activeLearning? 
			new Example.Looper(Collections.EMPTY_SET.iterator()) : dataset.iterator();
	}

	public Instance.Looper instancePool() 
	{ 
    if (activeLearning) {
      return new Instance.Looper(dataset.iterator());
    } else if (dataset instanceof BasicDataset) {
      // (Edoardo Airoldi)  this itearator is empty whenever there are no
      // unlabeled examples available for semi-supervised learning.
      return ((BasicDataset)dataset).iteratorOverUnlabeled();
    } else {
			return new Instance.Looper(Collections.EMPTY_SET);
		}
	}

	public Example labelInstance(Instance query) 
	{ 
		// the label was hidden by just hiding the type
		if (query instanceof Example) return (Example)query;
		else return null;
	}

	public boolean hasAnswers() 
	{ 
		return activeLearning;
	}

	static public void main(String[] argv)
	{
		try {
			Dataset dataset = Expt.toDataset(argv[0]);
			ClassifierLearner learner = Expt.toLearner(argv[1]);
			Classifier c = new DatasetClassifierTeacher(dataset).train(learner);
			if (c instanceof Visible) {
				ViewerFrame f = new ViewerFrame("from "+argv[0]+" and "+argv[1],((Visible)c).toGUI());
			} else {
				System.out.println("Learnt classifier: "+c);
			}
			System.out.println("Training error:   "+Tester.errorRate(c,dataset));
			if (c instanceof BinaryClassifier) {
				System.out.println("Average log loss: "+Tester.logLoss(((BinaryClassifier)c),dataset));
			}
			if (argv.length>=3 && (c instanceof Serializable)) {
				IOUtil.saveSerialized(((Serializable) c),new File(argv[2]));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("usage: dataset learner [classifierFile]"); 
		}
	}
}
