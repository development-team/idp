/* Copyright 2006, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify.relational;

import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.classify.algorithms.linear.MaxEntLearner;
import edu.cmu.minorthird.classify.*;
import edu.cmu.minorthird.util.gui.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


import edu.cmu.minorthird.util.*;
import edu.cmu.minorthird.classify.algorithms.svm.*;
import edu.cmu.minorthird.classify.algorithms.linear.*;
import edu.cmu.minorthird.classify.experiments.*;
import edu.cmu.minorthird.classify.transform.*;

import java.util.*;
import org.apache.log4j.*;

/**
 * Stacked Graphical Learning based on a BatchClassifier learner
 *
 * @author Zhenzhen Kou
 */

public class StackedGraphicalLearner extends StackedBatchClassifierLearner
{
	private static Logger log = Logger.getLogger(StackedGraphicalLearner.class);
	private static final boolean DEBUG = false;

	private ExampleSchema schema;
	private BatchClassifierLearner baseLearner;

	private StackingParams params;

	/** Bundle of parameters for the StackedGraphicalLearner. */
	public static class StackingParams {
		public int stackingDepth=1;
		public boolean useLogistic=true,useTargetPrediction=true,useConfidence=true;
		public Splitter splitter=new CrossValSplitter(5);
		int crossValSplits=5;
		
		/** If true, adjust all class confidences by passing them thru a logistic function */
		public boolean getUseLogisticOnConfidences() { return useLogistic; }
		public void setUseLogisticOnConfidences(boolean flag) { useLogistic=flag; }

		/** If true, use confidence in class predictions as weight for that feature. */
		public boolean getUseConfidences() { return useConfidence; }
		public void setUseConfidences(boolean flag) { useConfidence=flag; }

		/** If true, adjust all class confidences by passing them thru a logistic function */
		public boolean getUseTargetPrediction() { return useTargetPrediction; }
		public void setUseTargetPrediction(boolean flag) { useTargetPrediction=flag; }

		/** Number of iterations of stacking to use */
		public int getStackingDepth() { return stackingDepth; }
		public void setStackingDepth(int newStackingDepth) { this.stackingDepth = newStackingDepth; }

		/* Number of cross-validation splits to use in making predictions */
		public int getCrossValSplits() { return crossValSplits; }
		public void setCrossValSplits(int newCrossValSplits) { 
			this.splitter = new CrossValSplitter(newCrossValSplits);
			crossValSplits = newCrossValSplits;
		}
	}
	

	public StackingParams getParams() { return params; }

	public StackedGraphicalLearner()
	{
		this.baseLearner = new MaxEntLearner();
		this.params = new StackingParams();
	}

 	public StackedGraphicalLearner(BatchClassifierLearner baseLearner)
	{
		this();
		this.baseLearner = baseLearner;
		params.setStackingDepth(1);
	}


 	public StackedGraphicalLearner(BatchClassifierLearner baseLearner,int depth)
	{
		this();
		this.baseLearner = baseLearner;
		params.setStackingDepth(depth);
	}



	final public void setSchema(ExampleSchema schema)
	{
		this.schema = schema;
	}


	public Classifier batchTrain(RealRelationalDataset dataset)
	{
		Classifier[] m = new Classifier[params.stackingDepth+1];
		RealRelationalDataset stackedDataset = dataset;

		ProgressCounter pc = new ProgressCounter("training stacked learner","stacking level",params.stackingDepth+1);
		
		for (int d=0; d<=params.stackingDepth; d++) {
			m[d] = new DatasetClassifierTeacher(stackedDataset).train(baseLearner);
			if (d+1 <= params.stackingDepth) {
				stackedDataset = stackDataset(stackedDataset);
				new ViewerFrame("Dataset "+(d+1), new SmartVanillaViewer(stackedDataset));
			}
			pc.progress();
		}
		pc.finished();
		return new StackedGraphicalClassifier(m, params, dataset);
	}


	/**
	 * Create a new dataset in which each instance has been augmented
	 * with the features constructed from the *predicted* labels
	 * of neighbor examples, where the prediction is made using
	 * cross-validation.
	 */
	public RealRelationalDataset stackDataset(RealRelationalDataset dataset)
	{
		RealRelationalDataset result = new RealRelationalDataset();
				
		Dataset.Split s = dataset.split(params.splitter);
		schema = dataset.getSchema();
		ProgressCounter pc = new ProgressCounter("labeling for stacking","fold",s.getNumPartitions());

		HashMap rlt= new HashMap();
		
		for (int k=0; k<s.getNumPartitions(); k++) {

			RealRelationalDataset trainData = (RealRelationalDataset)s.getTrain(k);
			RealRelationalDataset testData = (RealRelationalDataset)s.getTest(k);
			log.info("splitting with "+params.splitter+", preparing to train on "+trainData.size()
							 +" and test on "+testData.size());
			Classifier c = new DatasetClassifierTeacher(trainData).train(baseLearner);

			for (Example.Looper i=testData.iterator(); i.hasNext(); ) {
		    SGMExample ex = (SGMExample)i.nextExample();
		    ClassLabel p = c.classification( ex );
		    rlt.put(ex.getExampleID(), p);
			}

			log.info("splitting with "+params.splitter+", stored classified dataset");
			pc.progress();
		} //get cv-like predictions for all training examples


		HashMap LinksMap = dataset.getLinksMap();
		HashMap Aggregators = dataset.getAggregators();

		for (Example.Looper i=dataset.iterator(); i.hasNext(); ) {
				SGMExample ex = (SGMExample)i.nextExample();
				SGMExample AugmentEx = AugmentExample(ex, LinksMap, Aggregators, rlt);
				
				result.add( AugmentEx );
		}

		pc.finished();
		return result;
	}

	private SGMExample AugmentExample(SGMExample ex, HashMap LinksMap, HashMap Aggregators, HashMap PredictedRlt){
		
		int numNewFeatures = 0;

		for (Iterator iter = Aggregators.keySet().iterator(); iter.hasNext();){
			numNewFeatures = numNewFeatures+ ((HashSet)Aggregators.get(iter.next())).size()*schema.getNumberOfClasses();
		}

		
		String[] features = new String[numNewFeatures];
		double[] values = new double[numNewFeatures];
		int index = 0;
		String egID = ex.getExampleID();
		if( LinksMap.containsKey(egID) ){ //this obj has ngbs
					//LinksMap.get(from) is a hashMap, keys are type, final val is to
				  // Aggregators is HashMap, key: type, val: operation--a hashset
					for (Iterator iter = Aggregators.keySet().iterator(); iter.hasNext();) { //for all types
								String type = iter.next().toString();
								if( ((HashMap)LinksMap.get(egID)).containsKey(type) ){	
									HashSet oper = (HashSet)Aggregators.get(type);
											for(Iterator operIter = oper.iterator(); operIter.hasNext();){ //every operations
												String Agr = (String)operIter.next();

															int[] temval = new int[schema.getNumberOfClasses()];
															HashSet ngb = (HashSet)((HashMap)LinksMap.get(egID)).get(type);
															for(Iterator ngbiter=ngb.iterator(); ngbiter.hasNext();){
																String ngbID= (String)ngbiter.next();
																if( PredictedRlt.get(ngbID) != null){
																	String pre = ((ClassLabel)PredictedRlt.get(ngbID)).bestClassName();
																	int idx = schema.getClassIndex(pre);
																	temval[idx]++;
																}
															}

															for( int i=0; i<schema.getNumberOfClasses(); i++ ){
																features[index] = stackFeatureName(type, Agr, schema.getClassName(i) );
																if(Agr.equals("COUNT"))
																	values[index] = temval[i];
																if(Agr.equals("EXISTS") && temval[i]>0)
																	values[index] = 1;
																	
																index++;
															}
											
											}
								}
				 }//end Aggregators.keySet
		
//		System.out.println(" index is "+index+" features are "+features.length+" and values are "+values.length);
		String[] truefeatures = new String[index];
		double[] truevalues = new double[index];
		for ( int i=0;i<index;i++ ){
			truefeatures[i]=	features[i];
			truevalues[i] = values[i];
		}
				Instance stackedInstance = new AugmentedInstance(ex.asInstance(),truefeatures,truevalues);
				return new SGMExample( stackedInstance, ex.getLabel(), ex.getExampleID());	
		
		}else{
			return ex;
		}

	}
	
	
	
	
	private static String stackFeatureName(String agr,String type, String predictedClassName)
	{
		 return "pred."+agr+"."+type+"."+predictedClassName;
	}


	public class StackedGraphicalClassifier implements Classifier,Visible
	{
	
		private Classifier[] m; 
		private RealRelationalDataset dataset;
		private StackingParams params;

		public StackedGraphicalClassifier(Classifier[] m, StackingParams params,RealRelationalDataset ds) 
		{ 
			this.m = m; 
			this.params = params;
			this.dataset = ds;
		}

		
		public ClassLabel classification(Instance instance)
		{
			return m[0].classification(instance);
		}
		
		
		
		public HashMap classification(RealRelationalDataset dataset)
		{
				HashMap rlt= new HashMap();

				RealRelationalDataset testData = dataset;

				for (int d=0; d<=params.stackingDepth; d++) {
					for (Example.Looper i=testData.iterator(); i.hasNext(); ) {
				    SGMExample ex = (SGMExample)i.nextExample();
				    ClassLabel p = m[d].classification( ex );
				    rlt.put(ex.getExampleID(), p);
					}

					if (d+1 <= params.stackingDepth) {
						testData = stackTestDataset(testData, rlt);
					}
				}
			return rlt;
		}		
		
		


		public RealRelationalDataset stackTestDataset(RealRelationalDataset dataset, HashMap predictions)
		{
			RealRelationalDataset result = new RealRelationalDataset();

			HashMap LinksMap = dataset.getLinksMap();
			HashMap Aggregators = dataset.getAggregators();
	
			for (Example.Looper i=dataset.iterator(); i.hasNext(); ) {
					SGMExample ex = (SGMExample)i.nextExample();
					SGMExample AugmentEx = AugmentExample(ex, LinksMap, Aggregators, predictions);
					result.addSGM( AugmentEx );
			}
	
			return result;
		}		
		
		
		
		public double score(Instance instance,String classLabelName) {
			return classification(instance).getWeight(classLabelName);
		}
		

		public String explain(Instance instance)
		{
			return "sorry, not implemented yet";
		}

	    public Explanation getExplanation(Instance instance) {
		Explanation ex = new Explanation(explain(instance));
		return ex;
	    }

		public Viewer toGUI()
		{
			ParallelViewer v = new ParallelViewer();
			for (int i=0; i<m.length; i++) {
				final int k = i;
				v.addSubView( 
					"Level "+k+" classifier",
					new TransformedViewer( new SmartVanillaViewer(m[k]) ) {
						public Object transform(Object o) {
							StackedGraphicalClassifier s = (StackedGraphicalClassifier)o;
							return s.m[k];
						}});
			}
			v.setContent(this);
			return v;
		}
	}

	

}
