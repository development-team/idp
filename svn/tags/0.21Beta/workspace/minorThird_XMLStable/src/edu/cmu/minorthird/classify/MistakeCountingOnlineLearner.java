/* Copyright 2003, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify;

import edu.cmu.minorthird.classify.algorithms.linear.VotedPerceptron;
import edu.cmu.minorthird.classify.algorithms.knn.*;


/**
 * A wrapper around on OnlineClassifierLearner that counts the number
 * of mistakes if makes.
 *
 * @author William Cohen
 */

public class MistakeCountingOnlineLearner extends OnlineClassifierLearner
{
    private OnlineClassifierLearner innerLearner;
    private int numMistakes, numExamples;
    private boolean reportMistakes;

    public MistakeCountingOnlineLearner()
    {
	//this(new VotedPerceptron(),true);
	this(new KnnLearner(),true);
    }

    public MistakeCountingOnlineLearner(OnlineClassifierLearner innerLearner)
    {
	this(innerLearner,true);
    } 

    public MistakeCountingOnlineLearner(OnlineClassifierLearner innerLearner,boolean reportMistakes) 
    { 
	this.innerLearner = innerLearner; 
	this.reportMistakes = reportMistakes;
	numMistakes = numExamples = 0;
    }

    public ClassifierLearner copy() 
    { 
	return new MistakeCountingOnlineLearner((OnlineClassifierLearner)innerLearner.copy(),reportMistakes);
    }

    public void completeTraining() 
    { 
	innerLearner.completeTraining();
	if (reportMistakes) {
	    System.out.println(numMistakes+" mistakes in "+numExamples+" examples for "+innerLearner);
	}
    }

    public void addExample(Example answeredQuery)
    {
	ClassLabel predicted = innerLearner.getClassifier().classification(answeredQuery.asInstance());
	if (!answeredQuery.getLabel().isCorrect(predicted)) numMistakes++;
	numExamples++;
	innerLearner.addExample(answeredQuery);
    }

    public Classifier getClassifier() 
    {
	return innerLearner.getClassifier();
    }

    public void setSchema(ExampleSchema schema)
    {
	innerLearner.setSchema(schema);
    }

    public void reset() 
    {
	innerLearner.reset();
	numMistakes = numExamples = 0;
    }

    /** Report the number of mistakes made by the inner learner. */
    public int getNumberOfMistakes() 
    { 
	return numMistakes; 
    }

    /** Report the number of examples sent to the inner learner. */
    public int getTotalNumberOfExamples() 
    { 
	return numExamples; 
    }

    public String toString() 
    {
	return 
	    "[MistakeCountingOnlineLearner: "+
	    numMistakes+"/"+numExamples+" mistakes for "+innerLearner+"]";
    }
}
