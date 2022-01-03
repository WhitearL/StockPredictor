package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;

public abstract class WEKAModelController extends ModelController{
	
	public abstract double predict(Instance instance);
	protected abstract AbstractClassifier buildModel();
	
}
