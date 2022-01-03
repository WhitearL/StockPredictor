package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import java.util.Date;
import java.util.Map;

public abstract class ModelController {
	
	protected ModelController() {}
	
	public abstract Map<Date, Double> getPredictions();
	
	/**
	 * Return the current data of the model.
	 */
	public abstract Map<Date, Double> getCurrentData();
	
	/**
	 * Return the evaluation metrics of the model.
	 */
	public abstract Map<String, String> getEvaluationData();
	
}
