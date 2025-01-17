/* Copyright 2003, Carnegie Mellon, All Rights Reserved */

package edu.cmu.minorthird.classify;

import edu.cmu.minorthird.classify.algorithms.linear.*;



/**
 * Abstract class which implements the 'getBinaryClassifier' method of BinaryClassifierLearner's.
 *
 * @author William Cohen
 */

public abstract class OnlineBinaryClassifierLearner extends OnlineClassifierLearner implements BinaryClassifierLearner
{
	final public void setSchema(ExampleSchema schema)
	{
		if (!ExampleSchema.BINARY_EXAMPLE_SCHEMA.equals(schema)) {
			throw new IllegalStateException("can only learn binary example data: requested schema is "+schema);
		}
	}
	final public BinaryClassifier getBinaryClassifier()
	{
		return (BinaryClassifier)getClassifier();
	}
}
