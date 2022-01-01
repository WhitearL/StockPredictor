package com.whitearl.stockpredictor.application.learning;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import com.whitearl.stockpredictor.application.utils.DataFetcher;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class MultiRegressor {
			
	private static final String CSV_TEMP_FILE_NAME = "tempData.csv";
	private static final String ARFF_TEMP_FILE_NAME = "tempData.arff";
	
	private static final File CSV_TEMP_FILE = new File(CSV_TEMP_FILE_NAME);
	private static final File ARFF_TEMP_FILE = new File(ARFF_TEMP_FILE_NAME);
	
    private Instances dataSet;
	private LinearRegression model;
    private Map<Date, Double> currentData;

    public MultiRegressor(String stockTicker, Date trainingBackDate, Date predictionStartDate) {
		try {

			cleanTempFiles();
			
			DataFetcher df = new DataFetcher();
		
			currentData = df.getHistoricPrices(stockTicker, trainingBackDate, predictionStartDate);
				
			convertMaptoARFF(currentData);
			
			DataSource dataSrc = new DataSource(ARFF_TEMP_FILE_NAME);
			dataSet = dataSrc.getDataSet();
			
			LinearRegression builtModel = buildModel();
			if (builtModel == null) {
				throw new NullPointerException();
			} else {
				this.model = builtModel;
			}
			
			cleanTempFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}	
    }
    
	/**
	 * Create a linear regression model from the given data source.
	 * @return The built model
	 */
	private LinearRegression buildModel() {
		try {
			dataSet.setClassIndex(dataSet.numAttributes() -1);
			LinearRegression builtModel = new LinearRegression();
			builtModel.buildClassifier(dataSet);
			return builtModel;
		} catch (Exception e) {
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
			e.printStackTrace();
		}
		return -1;
	}

    public void convertMaptoARFF(Map<Date, Double> data) {
   	
    	try {
			if (CSV_TEMP_FILE.createNewFile() 
					&& ARFF_TEMP_FILE.createNewFile()) {	
				
				writeMapDataToCSV(data);
				
				convertCSVtoARFF(CSV_TEMP_FILE, ARFF_TEMP_FILE);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    private void writeMapDataToCSV(Map<Date, Double> data) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_TEMP_FILE, true))) {
			
			for (Map.Entry<Date, Double> entry : data.entrySet()) {
				StringBuilder sb = new StringBuilder();
				
				sb.append(entry.getKey().getTime() / 1000); // Unix timestamp of date
				sb.append(",");
				sb.append(entry.getValue());
				
				bw.write(sb.toString() + System.lineSeparator());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void convertCSVtoARFF(File csvFile, File outFile) {

        try {
	        CSVLoader loader = new CSVLoader();
	        
	        String [] options = new String[1];
	        options[0]="-H";
	        loader.setOptions(options);
	        loader.setSource(csvFile);

	        Instances data = loader.getDataSet();
	
	        ArffSaver saver = new ArffSaver();
	        saver.setInstances(data);
	        saver.setFile(outFile);
	        saver.writeBatch();
	               
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    private void cleanTempFiles() {
    	if (CSV_TEMP_FILE.exists()) {CSV_TEMP_FILE.delete();} 
    	if (ARFF_TEMP_FILE.exists()) {ARFF_TEMP_FILE.delete();}
    }
    
	/**
	 * @return The dataset for this model
	 */
	public Instances getDataSet() {
		return dataSet;
	}

	public Map<Date, Double> getCurrentData() {
		return currentData;
	}

	public LinearRegression getModel() {
		return model;
	}
}
