package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.DECIMAL_FORMAT;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_FOLDS;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_SEED;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.UNIX_CONVERSION_LONG;

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

/**
 * Controller for WEKA Linear Regression Model
 * @author Lewis
 *
 */
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
    		// Clean temp data files
			cleanTempFiles();
			
			// Use the data fetcher to get the data for the last five years of the specific stock from the API
			DataFetcher df = new DataFetcher();
			currentData = df.getHistoricPrices(stockTicker, 5);
				
			// Convert the data to an ARFF file that Weka can use
			convertMaptoARFF(currentData);
			
			// Read the created ARFF file into a weka dataset
			DataSource dataSrc = new DataSource(ARFF_TEMP_FILE_NAME);
			dataSet = dataSrc.getDataSet();
			
			// Build the model. If model invalid throw an error
			LinearRegression builtModel = buildModel();
			if (builtModel == null) {
				throw new NullPointerException();
			} else {
				this.model = builtModel;
			}
			
			// Clean temp data files
			cleanTempFiles();
		} catch (Exception e) {
			// Catch-All for Weka and app exceptions. Show error message
			new Alert(AlertType.ERROR, "There was a problem building the LR model: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}	
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Date, Double> getCurrentData() {
		return currentData;
	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Date, Double> getPredictions() {
		// Automatic sorting via treemap
		Map<Date, Double> predictedValues = new TreeMap<>();

		// Get the prediction window in milliseconds
		Long predictionWindowMillis = this.predictionWindow.getInSeconds() * UNIX_CONVERSION_LONG;

		// Get the current date in millis
		Date currentDate = new Date();
		Long currentDateMillis = currentDate.getTime();

		// Get the end date of the prediction window
		Long predictionEndDateMillis = currentDateMillis + predictionWindowMillis;
		Date predictionEndDate = new Date(predictionEndDateMillis);

		// Get the working days in the prediction window and sort the result by date
		List<Date> daysToPredict = getWorkingDaysBetweenTwoDates(currentDate, predictionEndDate);
		Collections.sort(daysToPredict);

		// Create the instances to predict, putting the date for each in the first column.
		// Leave the second column empty so it can be filled by the model.
		List<Instance> instancesToClassify = new ArrayList<>();
		for (Date dayToPredict : daysToPredict) {
			// New instance
			Instance instance = new DenseInstance(2);
			
			// Set its dataset
			instance.setDataset(getDataSet());
			
			// Set the date in the first column
			instance.setValue(0, dayToPredict.getTime() / UNIX_CONVERSION_LONG);

			// Add to list
			instancesToClassify.add(instance);
		}

		// Predict all the instances and match them with their dates
		for (int i = 0; i < instancesToClassify.size(); i++) {
			Date date = daysToPredict.get(i);
			predictedValues.put(
				date,
				predict(instancesToClassify.get(i)) 
			);		
		}

		// Return the predicted data
		return predictedValues;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getEvaluationData() {		
		// Evaluation data map
		Map<String, String> evalData = new HashMap<>();
		
		try {
			// Use the Weka model evaluator to get the metrics
			Instances dataset = getDataSet();
			Evaluation evaluator = new Evaluation(dataset);

			LinearRegression evalModel = getModel();

			// Run a 3-Fold cross validation on the model with set seed.
			evaluator.crossValidateModel(evalModel, dataset, ML_FOLDS, new Random(ML_SEED));

			// Get the evaluation metrics from the evaluator and add to the map
			evalData.put("Root Mean Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanSquaredError()));
			evalData.put("Root Relative Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootRelativeSquaredError()));
			evalData.put("Root Mean Prior Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanPriorSquaredError()));
			evalData.put("Mean Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanAbsoluteError()));
			evalData.put("Mean Prior Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanPriorAbsoluteError()));
			evalData.put("Correlation Coefficient", String.format(DECIMAL_FORMAT, evaluator.correlationCoefficient()));
			evalData.put("Error Rate", String.format(DECIMAL_FORMAT, evaluator.errorRate()));

		} catch (NullPointerException e) {
			// Model not generated yet- Show error message
			new Alert(AlertType.ERROR, 
					"Please choose a stock and a prediction window and generate a model first by clicking 'Update Prediction'.", 
					ButtonType.OK).showAndWait();
		} catch (Exception e) {
			// Weka exceptions- Show error message
			new Alert(AlertType.ERROR, 
					"There was a problem generating the evaluation data:", 
					ButtonType.OK).showAndWait();
		}
		
		// Return the evaluation data
		return evalData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected LinearRegression buildModel() {
		try {
			// Set the class to predict to the last one (closing price)
			dataSet.setClassIndex(dataSet.numAttributes() -1);
			
			// Build the classifier for the model
			LinearRegression builtModel = new LinearRegression();
			builtModel.buildClassifier(dataSet);
			
			return builtModel;
		} catch (Exception e) {
			// Weka's Java library, unhelpfully, does not throw specific exceptions.
			new Alert(AlertType.ERROR, "There was a problem building the LR model: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public double predict(Instance instance) {
		try {
			// Attempt to classify the instance
			return model.classifyInstance(instance);
		} catch (Exception e) {
			// This method is called repeatedly. Dont spam the user with error messages, just print the stack trace
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Convert a map with data into an ARFF file
	 * @param data Data to convert
	 */
    public void convertMaptoARFF(Map<Date, Double> data) {	
    	try {
    		// Check files exist
			if (CSV_TEMP_FILE.createNewFile() 
					&& ARFF_TEMP_FILE.createNewFile()) {	
				
				// Write the map data to a CSV file. CSV files can be easily converted to ARFF by Weka.
				writeMapDataToCSV(data);
				
				// Convert the CSV file to ARFF
				convertCSVtoARFF(CSV_TEMP_FILE, ARFF_TEMP_FILE);

			}
		} catch (IOException e) {
			// Show file error message
			new Alert(AlertType.ERROR, "Could not convert the dataset to ARFF: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}
    }
	
    /**
     * Write the map data to a CSV file
     * @param data The data to write
     */
    private void writeMapDataToCSV(Map<Date, Double> data) {
    	// Open the csv file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_TEMP_FILE, true))) {
			
			// For each data entry write it to the file using a string builder
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<Date, Double> entry : data.entrySet()) {
				// Clear the string builder
				sb.setLength(0);
				
				// Append the data points separated by a comma
				sb.append(entry.getKey().getTime() / UNIX_CONVERSION_LONG); // Unix timestamp of date
				sb.append(",");
				sb.append(entry.getValue());
				
				// Write the string builders output to the file, followed by a line terminator
				bw.write(sb.toString() + System.lineSeparator());
			}
			
		} catch (IOException e) {
			// Problem with the files, show error message
			new Alert(AlertType.ERROR, 
					"Could not write the dataset to CSV: " 
							+ System.lineSeparator() + e, ButtonType.OK)
								.showAndWait();
		}
    }
    
    /**
     * Convert the created csv file to ARFF, which Weka can use
     * @param csvFile CSV file created earlier
     * @param outFile Output ARFF file
     */
    public void convertCSVtoARFF(File csvFile, File outFile) {
        try {
        	// Use Weka CSV library to load CSV
	        CSVLoader loader = new CSVLoader();
	        
	        // Configure csv loader
	        String [] options = new String[1];
	        // No header present in the file, use -H to indicate this
	        options[0]="-H"; 
	        loader.setOptions(options);
	        loader.setSource(csvFile);

	        // Load the data into a dataset
	        Instances data = loader.getDataSet();
	
	        // Save the file as ARFF
	        ArffSaver saver = new ArffSaver();
	        saver.setInstances(data);
	        saver.setFile(outFile);
	        saver.writeBatch();
	               
        } catch (Exception e) {
        	// Show error message
        	new Alert(AlertType.ERROR, 
        			"Could not convert the dataset to ARFF: " 
        					+ System.lineSeparator() + e, ButtonType.OK)
        						.showAndWait();
        }
    }

    /**
     * Utility method- Get a list of all the working days between two dates
     * @param startDate Date to start at (exclusive)
     * @param endDate Date to end at (exclusive)
     * @return A List of all the working days between the two dates.
     */
	public List<Date> getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
		
		// Get start and end dates as Calendar instances
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);

		// List to store ther results
		List<Date> workDays = new ArrayList<>();

		// Return empty list if start and end are the same
		if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
			return workDays;
		}

		// Ensure the start and end dates are the correct way round.
		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}

		do { // Exclude start and end dates
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			
			// If the date is not a saturday or a sunday add it to the list
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				workDays.add(startCal.getTime());
			}
		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); // While the start date is before the end date

		// Return the dates
		return workDays;
	}
    
    /**
     * Delete temporary files
     */
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

	/**
	 * @return The current model
	 */
	public LinearRegression getModel() {
		return model;
	}
	
}
