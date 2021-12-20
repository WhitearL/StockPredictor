package com.whitearl.hip.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class MultiRegressor {
	
	private Logger logger = LoggerFactory.getLogger(MultiRegressor.class);
		
    private DataSource dataSrc;
    private Instances dataSet;
	private LinearRegression model;
    
    /**
     * Public constructor, allow instantiation.
     * @param arffPath Path to arff data file to train from
     */
    public MultiRegressor(String arffPath) {
		try {
			dataSrc = new DataSource(arffPath);
			dataSet = dataSrc.getDataSet();
			
			LinearRegression model = buildModel();
			if (model.equals(null)) {
				throw new NullPointerException();
			} else {
				this.model = model;
				logger.info("Model built!");
			}
		} catch (Exception e) {
			logger.error("Error building model");
			e.printStackTrace();
		}	
    }
    
	/**
	 * Create a linear regression model from the given data source.
	 * @return The built model
	 */
	private LinearRegression buildModel() {
		try {
			dataSet.setClassIndex(dataSet.numAttributes()-1);
			LinearRegression model = new LinearRegression();
			model.buildClassifier(dataSet);
			return model;
		} catch (Exception e) {
			logger.error("Error while creating model");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Predict using data from an instance
	 * @param instance Instance to predict from
	 * @return Predicted value
	 */
	public double predict(Instance instance) {
		try {
			return model.classifyInstance(instance);
		} catch (Exception e) {
			logger.error("Could not classify instance");
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * @return The dataset for this model
	 */
	public Instances getDataSet() {
		return dataSet;
	}
}
