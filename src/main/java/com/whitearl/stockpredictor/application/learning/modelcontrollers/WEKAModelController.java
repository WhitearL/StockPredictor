package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;

/**
 * Abstract class representing a generic WEKA ML Model
 * @author Lewis
 *
 */
public abstract class WEKAModelController extends ModelController{
	
	/**
	 * Predict an instance
	 * @param instance Instance to predict
	 * @return The result of the prediction as a double
	 */
	public abstract double predict(Instance instance);
	
	/**
	 * Build the model with the current data
	 * @return The build model as a WEKA generic classifier
	 */
	protected abstract AbstractClassifier buildModel();
	
}
