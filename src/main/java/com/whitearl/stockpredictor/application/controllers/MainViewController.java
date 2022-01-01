package com.whitearl.stockpredictor.application.controllers;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_FOLDS;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.ML_SEED;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.UNIX_CONVERSION_LONG;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.whitearl.stockpredictor.application.learning.MultiRegressor;
import com.whitearl.stockpredictor.application.model.PredictionWindow;
import com.whitearl.stockpredictor.application.model.Stock;
import com.whitearl.stockpredictor.application.utils.StocksController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MainViewController implements Initializable {

	private static final String DECIMAL_FORMAT = "%.2f";
	private StocksController stocksController;
	private MultiRegressor modelController;

	@FXML
	private ComboBox<String> cmbStock;

	@FXML
	private ComboBox<String> cmbPredictionWindow;

	@FXML
	private Label lblStockName;

	@FXML
	private Label lblPredictionWindow;

	@FXML
	private LineChart<String, Number> chtPrices;

	@FXML
	private Button btnUpdate;

	@FXML
	private Button btnEvaluate;

	@FXML
	private Button btnQuit;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		stocksController = new StocksController();

		populateComboBoxes();
		initialiseEventHandlers();
	}

	private void populateComboBoxes() {
		// Load configured stock tickers
		cmbStock.getItems().clear();
		cmbStock.getItems().addAll(
				stocksController.getStocks().stream().map(Stock::getTicker).collect(Collectors.toList()));

		// Load configured time units for prediction windows
		cmbPredictionWindow.getItems().clear();
		cmbPredictionWindow.getItems().addAll(PredictionWindow.getVerboseNames());
	}

	private void updatePrediction() {

		try {

			Map<Date, Double> predictions = getPredictions();
			Map<Date, Double> historicData = modelController.getCurrentData();

			// Historic data + predictions on the end
			Map<Date, Double> allData = new TreeMap<>(historicData);
			allData.putAll(predictions);

			ObservableList<String> datesX = FXCollections.observableArrayList(allData.keySet().stream().map(Date::toString).collect(Collectors.toList()));

			CategoryAxis xAxis = new CategoryAxis();
			xAxis.setCategories(datesX);

			NumberAxis yAxis = new NumberAxis();  
			yAxis.setLabel("Closing Price");					

			XYChart.Series<String, Number> srsHistorical = new XYChart.Series<>();
			srsHistorical.setName("Historical");

			XYChart.Series<String, Number> srsPredicted = new XYChart.Series<>();
			srsPredicted.setName("Predicted");

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			for (Map.Entry<Date, Double> entry : historicData.entrySet()) {
				srsHistorical.getData().add(new XYChart.Data<>(formatter.format(entry.getKey()), entry.getValue()));
			}

			for (Map.Entry<Date, Double> entry : predictions.entrySet()) {
				srsPredicted.getData().add(new XYChart.Data<>(formatter.format(entry.getKey()), entry.getValue()));
			}

			chtPrices.getData().clear();
			chtPrices.setCreateSymbols(false);
			chtPrices.getData().add(srsHistorical);
			chtPrices.getData().add(srsPredicted);

		} catch (NullPointerException e) {
			// Stock or prediction window not chosen.
			new Alert(AlertType.ERROR, "Please choose a stock and a prediction window", ButtonType.OK).showAndWait();
		}

	}

	private Map<Date, Double> getPredictions() {
		Map<Date, Double> predictedValues = new TreeMap<>();

		String ticker = cmbStock.getSelectionModel().getSelectedItem();
		Long predictionWindowMillis = PredictionWindow.getFromVerbose(cmbPredictionWindow.getSelectionModel().getSelectedItem()).getInSeconds() * 1000;

		Date currentDate = new Date();
		Long currentDateMillis = currentDate.getTime();

		Long trainingBackDateMillis = currentDateMillis - PredictionWindow.ONE_YEAR.getInSeconds() * 1000;		
		Date trainingBackDate = new Date(trainingBackDateMillis); 

		Long predictionEndDateMillis = currentDateMillis + predictionWindowMillis;		
		Date predictionEndDate = new Date(predictionEndDateMillis); 

		if (stocksController.isStockValid(ticker)) {
			modelController = new MultiRegressor(ticker, trainingBackDate, currentDate);

			List<Date> daysToPredict = getWorkingDaysBetweenTwoDates(currentDate, predictionEndDate);
			Collections.sort(daysToPredict);

			List<Instance> instancesToClassify = new ArrayList<>();
			for (Date dayToPredict : daysToPredict) {
				Instance instance = new DenseInstance(2);
				instance.setDataset(this.modelController.getDataSet());
				instance.setValue(0, dayToPredict.getTime() / UNIX_CONVERSION_LONG);

				instancesToClassify.add(instance);
			}


			for (int i = 0; i < instancesToClassify.size(); i++) {
				predictedValues.put(daysToPredict.get(i), modelController.predict(instancesToClassify.get(i)));
			}

		}
		return predictedValues;
	}

	private void initialiseEventHandlers() {
		// Show selected stock on the label
		cmbStock.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			Stock chosenStock = stocksController.getStockWithTicker(newValue);
			lblStockName.setText(chosenStock.getTicker() + System.lineSeparator() + chosenStock.getName());
		}); 

		// Show selected prediction window on the label
		cmbPredictionWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> 
		lblPredictionWindow.setText(PredictionWindow.getFromVerbose(newValue).getVerboseName())
				);

		btnUpdate.setOnAction(e -> updatePrediction());
		btnQuit.setOnAction(e -> System.exit(0));
		btnEvaluate.setOnAction(e -> showEvaluationData());
	}

	private void showEvaluationData() {
		try {
			Instances dataset = modelController.getDataSet();
			Evaluation evaluator = new Evaluation(dataset);

			LinearRegression model = modelController.getModel();
	
			evaluator.crossValidateModel(model, dataset, ML_FOLDS, new Random(ML_SEED));

			Map<String, String> evalData = new HashMap<>();
	
			evalData.put("Root Mean Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanSquaredError()));
			evalData.put("Root Relative Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootRelativeSquaredError()));
			evalData.put("Root Mean Prior Squared Error", String.format(DECIMAL_FORMAT, evaluator.rootMeanPriorSquaredError()));
			evalData.put("Mean Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanAbsoluteError()));
			evalData.put("Mean Prior Absolute Error", String.format(DECIMAL_FORMAT, evaluator.meanPriorAbsoluteError()));
			evalData.put("Correlation Coefficient", String.format(DECIMAL_FORMAT, evaluator.correlationCoefficient()));
			evalData.put("Error Rate", String.format(DECIMAL_FORMAT, evaluator.errorRate()));		
			
			TableDialog td = new TableDialog(evalData);
			td.displayTableDialog();
			
		} catch (NullPointerException e) {
			// Model not generated yet
			new Alert(AlertType.ERROR, 
					"Please choose a stock and a prediction window and generate a model first by clicking 'Update Prediction'.", 
					ButtonType.OK).showAndWait();
			
		} catch (Exception e) {
			// Weka exceptions
			e.printStackTrace();
		}

	}

	public List<Date> getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDate);        

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);

		List<Date> workDays = new ArrayList<>();

		//Return empty list if start and end are the same
		if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
			return workDays;
		}

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(endDate);
			endCal.setTime(startDate);
		}

		do {
			//excluding start date
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				workDays.add(startCal.getTime());
			}
		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis()); //excluding end date

		return workDays;
	}

}