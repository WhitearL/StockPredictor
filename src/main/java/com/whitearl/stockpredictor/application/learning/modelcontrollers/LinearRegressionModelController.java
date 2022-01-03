package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.DECIMAL_FORMAT;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_FOLDS;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_SEED;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import com.whitearl.stockpredictor.application.model.PredictionWindow;
import com.whitearl.stockpredictor.application.utils.DataFetcher;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class LinearRegressionModelController extends WEKAModelController {
	
	// Temp data file names 
	private static final String CSV_TEMP_FILE_NAME = "tempData.csv";
	private static final String ARFF_TEMP_FILE_NAME = "tempData.arff";
	
	// Temp data file objects for data conversion
	private static final File CSV_TEMP_FILE = new File(CSV_TEMP_FILE_NAME);
	private static final File ARFF_TEMP_FILE = new File(ARFF_TEMP_FILE_NAME);
	
	// Dataset for WEKA
    private Instances dataSet;
    
    // WEKA LR Model
	private LinearRegression model;
	
	// Current data from API
    private Map<Date, Double> currentData;
    
    // Prediction window chosen by user
    private PredictionWindow predictionWindow;
    
    /**
     * Public constructor, allow instantiation
	 * @param stockTicker Stock alias, e.g. AAPL for Apple Inc.
	 * @param predictionWindow Prediction window selected by user
     */
    public LinearRegressionModelController(String stockTicker, PredictionWindow predictionWindow) {
		this.predictionWindow = predictionWindow;
		
    	try {
			cleanTempFiles();
			
			DataFetcher df = new DataFetcher();
		
			currentData = df.getHistoricPrices(stockTicker, 5);
				
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
	protected LinearRegression buildModel() {
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

	@Override
	public Map<Date, Double> getCurrentData() {
		return currentData;
	}

	public LinearRegression getModel() {
		return model;
	}

	
	@Override
	public Map<Date, Double> getPredictions() {
		Map<Date, Double> predictedValues = new TreeMap<>();

		Long predictionWindowMillis = this.predictionWindow.getInSeconds() * 1000;

		Date currentDate = new Date();
		Long currentDateMillis = currentDate.getTime();

		Long predictionEndDateMillis = currentDateMillis + predictionWindowMillis;
		Date predictionEndDate = new Date(predictionEndDateMillis);

		List<Date> daysToPredict = getWorkingDaysBetweenTwoDates(currentDate, predictionEndDate);
		Collections.sort(daysToPredict);

		List<Instance> instancesToClassify = new ArrayList<>();
		for (Date dayToPredict : daysToPredict) {
			Instance instance = new DenseInstance(2);
			instance.setDataset(getDataSet());
			instance.setValue(0, dayToPredict.getTime() / 1000);

			instancesToClassify.add(instance);
		}

		for (int i = 0; i < instancesToClassify.size(); i++) {
			Date date = daysToPredict.get(i);
			predictedValues.put(
				date,
				predict(instancesToClassify.get(i)) 
			);		
		}

		return predictedValues;
	}
	

	@Override
	public Map<String, String> getEvaluationData() {		
		Map<String, String> evalData = new HashMap<>();
		
		try {
			Instances dataset = getDataSet();
			Evaluation evaluator = new Evaluation(dataset);

			LinearRegression evalModel = getModel();

			evaluator.crossValidateModel(evalModel, dataset, ML_FOLDS, new Random(ML_SEED));

			evalData.put("Root Mean Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanSquaredError()));
			evalData.put("Root Relative Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootRelativeSquaredError()));
			evalData.put("Root Mean Prior Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanPriorSquaredError()));
			evalData.put("Mean Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanAbsoluteError()));
			evalData.put("Mean Prior Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanPriorAbsoluteError()));
			evalData.put("Correlation Coefficient", String.format(DECIMAL_FORMAT, evaluator.correlationCoefficient()));
			evalData.put("Error Rate", String.format(DECIMAL_FORMAT, evaluator.errorRate()));

		} catch (NullPointerException e) {
			// Model not generated yet
			new Alert(AlertType.ERROR, 
					"Please choose a stock and a prediction window and generate a model first by clicking 'Update Prediction'.", 
					ButtonType.OK).showAndWait();
		} catch (Exception e) {
			// Weka exceptions
			e.printStackTrace();
		}
		return evalData;
	}
	
	public List<Date> getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);

		List<Date> workDays = new ArrayList<>();

		// Return empty list if start and end are the same
		if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
			return workDays;
		}

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}

		do { // Exclude start and end dates
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				workDays.add(startCal.getTime());
			}
		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis());

		return workDays;
	}
}
