package com.whitearl.stockpredictor.application.learning.modelcontrollers;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.DECIMAL_FORMAT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.whitearl.stockpredictor.application.model.PredictionWindow;
import com.whitearl.stockpredictor.application.utils.DataFetcher;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Model controller for ARIMA.
 * Interfaces with the R language to make use of R's auto.arima() function
 * @author Lewis
 *
 */
public class ARIMAModelController extends ModelController {

	// Accuracy metrics provided by the R Arima Library
	private static final String[] ORDERED_ACC_METRICS = {
			"Mean Error",
			"Root Mean Squared Error",
			"Mean Absolute Error",
			"Mean Percentage Error",
			"Mean Absolute Percentage Error",
			"Mean Absolute Scaled Error",
			"Autocorrelation of errors at lag 1"
	};

	// CSV Constants
	private static final String CSV_TEMP_FILE_NAME = "tempData.csv";
	private static final File CSV_TEMP_FILE = new File(CSV_TEMP_FILE_NAME);
	private static final String CSV_HEADER = "close";

	// Data matrices for predictions, current data, and accuracy metrics
	private Map<Date, Double> predictions;
	private Map<Date, Double> currentData;
	private Map<String, String> accMatrix;

	// Prediction window detailing how much data to forecast
	private PredictionWindow predictionWindow;

	/**
	 * Public constructor, allow instantiation
	 * @param stockTicker Stock alias, e.g. AAPL for Apple Inc.
	 * @param predictionWindow Prediction window selected by user
	 */
	public ARIMAModelController(String stockTicker, PredictionWindow predictionWindow) {
		this.predictionWindow = predictionWindow;

		// TreeMap for automatic sorting by date
		this.predictions = new TreeMap<>();	

		// Clean temp data files
		cleanTempFiles();

		// Use the data fetcher to get the data for the last two years of the specific stock from the API
		DataFetcher df = new DataFetcher();
		this.currentData = df.getHistoricPrices(stockTicker, 2);

		// Write the API data to a file which the R script can read
		writeMapDataToCSV(this.currentData);

		// Get the predictions from the R script that runs the ARIMA model
		double[] predictionsArray = generatePredictions(this.currentData.values().stream().mapToDouble(Double::doubleValue).toArray());

		// For each prediction, put it into the predictions matrix with its relevant date
		Date priceDate = new Date();
		for (double cPrice : predictionsArray) {
			this.predictions.put(priceDate, cPrice);
			// Move date forward by 1 day for next day
			priceDate = modifyDateByDays(priceDate, 1);
		}

		// Clean temp data files
		cleanTempFiles();
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
	public Map<String, String> getEvaluationData() {
		if (accMatrix != null) {
			return accMatrix;
		} else {
			return new HashMap<>();
		}	
	}
	
	/**
	 * Use the R interface library to run an R script to make a forecast from the gathered data
	 * @param stockClosePrices Double array time series of stock closing prices
	 * @return
	 */
	public double[] generatePredictions(double[] stockClosePrices) {
		double[] meanValues;

		// Initialise the R interface library 
		RCaller caller = RCaller.create();
		RCode code = RCode.create();

		// Define the R script used to get the predictions
		code.addDoubleArray("stockClosePrices", stockClosePrices);
		// Add the R Library 'forecast' for time series forecasting from an ARIMA model
		code.R_require("forecast");
		// Build the model
		code.addRCode("model <- auto.arima(ts(stockClosePrices, frequency=100),D=1)"); 
		// Forecast the next x days, x being the prediction window the user chose
		code.addRCode("forecastedData <- forecast(model ,h="+ this.predictionWindow.getInDays() + ")");
		// Get the mean forecasted values and the accuracy matrics from the forecasted results
		code.addRCode("resultData <- list(mean = as.numeric(forecastedData$mean), accMtx=accuracy(forecastedData))");
		caller.setRCode(code);

		// Run the code and grab the 'resultData' variable to return
		caller.runAndReturnResult("resultData");

		// Get the accuracy matrix and forecasted data from the R interface
		double[][] accuracyMatrix = caller.getParser().getAsDoubleMatrix("accMtx");
		meanValues = caller.getParser().getAsDoubleArray("mean");

		// Record the accuracy matrix data for later use
		recordAccuracyMatrix(accuracyMatrix[0]);

		// Return the data.
		return meanValues;

	}

	/**
	 * Save the accuracy matrix from the model
	 * @param accuracyMatrix Accuracy matrix generated from model
	 */
	private void recordAccuracyMatrix(double[] accuracyMatrix) {
		if (accMatrix != null) {
			accMatrix.clear();
		} else {
			accMatrix = new HashMap<>();
		}

		for (int i = 0; i < ORDERED_ACC_METRICS.length; i++) {
			// Put the metric's name in a map with its value as string
			accMatrix.put(ORDERED_ACC_METRICS[i], String.format(DECIMAL_FORMAT, accuracyMatrix[i]));
		}
	}

	/**
	 * Write data from map to a CSV file
	 * @param data Data to write
	 */
	private void writeMapDataToCSV(Map<Date, Double> data) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_TEMP_FILE, true))) {
			// String builder to build file contents
			StringBuilder sb = new StringBuilder();
			
			// Write the CSV header first
			bw.write(CSV_HEADER + System.lineSeparator());
			
			// For each data entry, add it to the string builder, followed by a line terminator
			for (Map.Entry<Date, Double> entry : data.entrySet()) {
				sb.append(entry.getValue() + System.lineSeparator());	
			}
			
			// Write the string contents
			bw.write(sb.toString());
		} catch (IOException e) {
			// There was a problem while writing to the csv file. Show an error message.
			new Alert(AlertType.ERROR, "There was a problem writing data to CSV: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}
	}

	/**
	 * Delete the temporary CSV file
	 */
	private void cleanTempFiles() {
		if (CSV_TEMP_FILE.exists()) {CSV_TEMP_FILE.delete();} 
	}

	/**
	 * Add or subtract days from a date
	 * @param date Date to modify
	 * @param days Dates to add or subtract. e.g. Use -5 to go back 5 days
	 * @return The modified date
	 */
	public Date modifyDateByDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * Return the predictions
	 */
	public Map<Date, Double> getPredictions() {
		return predictions;
	}

}
