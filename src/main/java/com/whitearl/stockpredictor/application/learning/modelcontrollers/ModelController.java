package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import java.util.Date;
import java.util.Map;

public abstract class ModelController {
	
	protected ModelController() {}
	
	/**
	 * Get the model's predictions
	 * @return A map with the prediction data
	 */
	public abstract Map<Date, Double> getPredictions();
	
	/**
	 * Return the current data of the model.
	 * @return A map with the current data
	 */
	public abstract Map<Date, Double> getCurrentData();
	
	/**
	 * Return the evaluation metrics of the model.
	 * @return A map with the evaluation data
	 */
	public abstract Map<String, String> getEvaluationData();
	
}
