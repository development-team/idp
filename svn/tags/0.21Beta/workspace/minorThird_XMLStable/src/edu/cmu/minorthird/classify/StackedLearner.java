/* Copyright 2003, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify;

import edu.cmu.minorthird.classify.experiments.CrossValSplitter;
import edu.cmu.minorthird.classify.algorithms.trees.AdaBoost;
import edu.cmu.minorthird.classify.algorithms.linear.MaxEntLearner;
import edu.cmu.minorthird.util.gui.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Stacked generalization.  This implementation is based on Wolpert,
 * D.H. (1992), Stacked Generalization, Neural Networks, Vol. 5,
 * pp. 241-259, Pergamon Press. http://citeseer.nj.nec.com/wolpert92stacked.html
 *
 * @author William Cohen
 */

public class StackedLearner extends BatchClassifierLearner
{
	private static Logger log = Logger.getLogger(StackedLearner.class);
	private static final boolean DEBUG = false;

	private ExampleSchema schema;
	private BatchClassifierLearner[] innerLearners;
	private BatchClassifierLearner finalLearner;
	private Splitter splitter;

	/** Use stacked learning to calibrate the predictions of the inner learner
	 * using logistic regression.
	 */
	public StackedLearner(BatchClassifierLearner innerLearner,Splitter splitter)
	{
		this(
			new BatchClassifierLearner[]{innerLearner},	 
			new MaxEntLearner(),
			splitter);
	}
	/** Use stacked learning to calibrate the predictions of the inner learner
	 * using logistic regression, using 3-CV to split.
	 */
	public StackedLearner(BatchClassifierLearner innerLearner)
	{
		this(
			new BatchClassifierLearner[]{innerLearner},	 
			new MaxEntLearner(),
			new CrossValSplitter(3));
	}
	/** Use stacked learning to calibrate the predictions of AdaBoost
	 * using logistic regression, using 3-CV to split.
	 */
	public StackedLearner()
	{
		this(
			new BatchClassifierLearner[]{new AdaBoost()},	 
			new MaxEntLearner(),
			new CrossValSplitter(3));
	}
	/** Create a stacked learner.
	 */
	public StackedLearner(
		BatchClassifierLearner[] innerLearners,
		BatchClassifierLearner finalLearner,
		Splitter splitter)
	{
		this.innerLearners = innerLearners;
		this.finalLearner = finalLearner;
		this.splitter = splitter;
	}

	public Splitter getSplitter() { return splitter; }
	public void setSplitter(Splitter splitter) {  this.splitter=splitter; }
	public void setInnerLearner(BatchClassifierLearner learner) 
	{ 
		this.innerLearners = new BatchClassifierLearner[]{learner};
	}
	public BatchClassifierLearner getInnerLearner()
	{
		if (innerLearners.length!=1) throw new IllegalStateException("multiple inner learners");
		return innerLearners[0];
	}

	final public void setSchema(ExampleSchema schema)
	{
		this.schema = schema;
		for (int i=0; i<innerLearners.length; i++) {
			innerLearners[i].setSchema(schema);
		}
		finalLearner.setSchema(schema);
	}

	public Classifier batchTrain(Dataset dataset)
	{
		BasicDataset stackedData = new BasicDataset();
		Classifier[] innerClassifiers = new Classifier[innerLearners.length];

		// build transformed dataset of examples where features
		// are predictions of inner learners on test data, and
		// classes are the real classes

		Dataset.Split split = dataset.split(splitter);
		for (int k=0; k<split.getNumPartitions(); k++) {
			Dataset trainData = split.getTrain(k);
			for (int i=0; i<innerLearners.length; i++) {
				innerLearners[i].reset();
				log.info("training inner learner "+(i+1)+"/"+innerLearners.length
								 +" on fold "+(k+1)+"/"+split.getNumPartitions());
				innerClassifiers[i] = innerLearners[i].batchTrain(trainData);
			}
			Dataset testData = split.getTest(k);
			log.info("transforming test examples of fold "+(k+1)+"/"+split.getNumPartitions());
			for (Example.Looper j=testData.iterator(); j.hasNext(); ) {
				Example e = j.nextExample();
				stackedData.add(new Example(transformInstance(schema,e,innerClassifiers), e.getLabel()));
			}
		}
		
		// train final learner on transformed data, and innerLearners on the real data
		log.info("training level-1 learner");
		Classifier finalClassifier = finalLearner.batchTrain(stackedData);
		log.info("result is "+finalClassifier);
		for (int i=0; i<innerLearners.length; i++) {
			log.info("training inner learner "+(i+1)+"/"+innerLearners.length+" on full dataset");
			innerClassifiers[i] = innerLearners[i].batchTrain(dataset);
		}
		classifier = new StackedClassifier(schema,innerClassifiers,finalClassifier);
		return classifier;
	}
	
	private static Instance 
	transformInstance(ExampleSchema schema,Instance oldInstance,Classifier[] innerClassifiers)
	{
		MutableInstance newInstance = new MutableInstance();
		for (int i=0; i<innerClassifiers.length; i++) {
			ClassLabel ithPrediction = innerClassifiers[i].classification(oldInstance);
			String learner = "learner_"+i;
			for (int h=0; h<schema.getNumberOfClasses(); h++) {
				String className = schema.getClassName(h);
				double w = ithPrediction.getWeight(className);
				newInstance.addNumeric( new Feature(new String[]{learner,"class_"+className}), w); 
			}
		}
		if (DEBUG) log.debug("Transformed "+newInstance+" <= "+oldInstance);
		return newInstance;
	}

	private static String
	explainTransformedInstance(ExampleSchema schema,Instance oldInstance,Classifier[] innerClassifiers)
	{
		StringBuffer buf = new StringBuffer("");
		MutableInstance newInstance = new MutableInstance();
		for (int i=0; i<innerClassifiers.length; i++) {
			ClassLabel ithPrediction = innerClassifiers[i].classification(oldInstance);
			String learner = "learner_"+i;
			for (int h=0; h<schema.getNumberOfClasses(); h++) {
				String className = schema.getClassName(h);
				double w = ithPrediction.getWeight(className);
				newInstance.addNumeric( new Feature(new String[]{learner,"class_"+className}), w); 
				buf.append("learner#"+(i+1)+" predicts "+className+":\n"+
									 innerClassifiers[i].explain(oldInstance)+"\n");
			}
		}
		if (DEBUG) log.debug("Transformed "+newInstance+" <= "+oldInstance);
		return buf.toString();
	}

	static private class StackedClassifier implements Classifier,Visible
	{
		private ExampleSchema schema;
		private Classifier[] innerClassifiers;
		private Classifier finalClassifier;
		public StackedClassifier(ExampleSchema schema,Classifier[] innerClassifiers,Classifier finalClassifier)
		{
			this.schema = schema;
			this.innerClassifiers = innerClassifiers;
			this.finalClassifier = finalClassifier;
		}
		public ClassLabel classification(Instance instance)
		{
			Instance newInstance = transformInstance(schema,instance,innerClassifiers);
			return finalClassifier.classification(newInstance);
		}
		public double score(Instance instance,String classLabelName) {
			return classification(instance).getWeight(classLabelName);
		}
		public String explain(Instance instance)
		{
			StringBuffer buf = new StringBuffer("");
			buf.append(explainTransformedInstance(schema,instance,innerClassifiers));
			Instance newInstance = transformInstance(schema,instance,innerClassifiers);			
			buf.append("final classifier:\n");
			buf.append(finalClassifier.explain(newInstance));
			return buf.toString();
		}

	    public Explanation getExplanation(Instance instance) {
		Explanation ex = new Explanation(explain(instance));
		return ex;
	    }
		public Viewer toGUI()
		{
			Viewer v = new ComponentViewer() {
					public JComponent componentFor(Object o) {
						StackedClassifier sc = (StackedClassifier)o;
						JPanel mainPanel = new JPanel();
						mainPanel.setLayout(new BorderLayout());
						mainPanel.setBorder(new TitledBorder("Stacked Classifier"));
						JPanel finalPanel = new JPanel();
						finalPanel.setBorder(new TitledBorder("Final classifier"));
						Viewer w = new SmartVanillaViewer(sc.finalClassifier);
						finalPanel.add(w);
						w.setSuperView(this);
						mainPanel.add(finalPanel,BorderLayout.NORTH);
						JPanel innerPanel = new JPanel();
						innerPanel.setBorder(new TitledBorder("Inner classifier(s)"));
						for (int i=0; i<innerClassifiers.length; i++) {
							Viewer u = new SmartVanillaViewer(innerClassifiers[i]);
							innerPanel.add(u);
							u.setSuperView(this);
						}
						mainPanel.add(innerPanel,BorderLayout.SOUTH);
						return new JScrollPane(mainPanel);
					}
				};
			return v;
		}
	}
}
