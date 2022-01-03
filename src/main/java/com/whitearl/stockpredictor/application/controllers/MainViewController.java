package com.whitearl.stockpredictor.application.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.whitearl.stockpredictor.application.learning.modelcontrollers.ARIMAModelController;
import com.whitearl.stockpredictor.application.learning.modelcontrollers.LinearRegressionModelController;
import com.whitearl.stockpredictor.application.learning.modelcontrollers.ModelController;
import com.whitearl.stockpredictor.application.model.Model;
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

/**
 * JavaFX Controller for the main window. Controls UI elements.
 * @author Lewis
 *
 */
public class MainViewController implements Initializable {
	
	// Date formatter for dates on chart
	private SimpleDateFormat chartDateFormatter = new SimpleDateFormat("dd-MM-yyyy");
	
	// Stock information helper
	private StocksController stocksController;
	
	// Model controller and interface
	private ModelController modelController;

	// JavaFX controls loaded from FXML UI layout definition
	@FXML
	private ComboBox<String> cmbStock;
	@FXML
	private ComboBox<String> cmbPredictionWindow;
	@FXML
	private ComboBox<String> cmbModel;	
	@FXML
	private Label lblStockName;
	@FXML
	private Label lblPredictionWindow;
	@FXML
	private Label lblModel;
	@FXML
	private LineChart<String, Number> chtPrices;
	@FXML
	private Button btnUpdate;
	@FXML
	private Button btnEvaluate;
	@FXML
	private Button btnQuit;

	/**
	 * Initialise the UI
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		stocksController = new StocksController();

		populateComboBoxes();
		initialiseEventHandlers();
	}

	/**
	 * Populate the comboboxes with the selections they need.
	 */
	private void populateComboBoxes() {
		// Load configured stock tickers
		cmbStock.getItems().clear();
		cmbStock.getItems().addAll(
				stocksController.getStocks()
					.stream()
					.map(Stock::getTicker)
					.collect(Collectors.toList()));

		// Load configured time units for prediction windows
		cmbPredictionWindow.getItems().clear();
		cmbPredictionWindow.getItems().addAll(PredictionWindow.getVerboseNames());
		
		// Load model selections
		cmbModel.getItems().clear();
		cmbModel.getItems().addAll(Model.getVerboseNames());
	}

	/**
	 * Set up the event handlers on the comboboxes and buttons
	 */
	private void initialiseEventHandlers() {
		// When stock selected, get its details and show it on the UI
		cmbStock.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			Stock chosenStock = stocksController.getStockWithTicker(newValue);
			lblStockName.setText(chosenStock.getTicker() + System.lineSeparator() + chosenStock.getName());
		}); 

		// When prediction window selected, get its details and show it on the UI
		cmbPredictionWindow.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> 
		lblPredictionWindow.setText(PredictionWindow.getFromVerbose(newValue).getVerboseName()));

		// When model selected, get its details and show it on the UI
		cmbModel.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> 
		lblModel.setText(Model.getFromVerbose(newValue).getVerboseName()));
		
		// Connect buttons to their methods
		btnUpdate.setOnAction(e -> updatePrediction());     // Run prediction
		btnEvaluate.setOnAction(e -> showEvaluationData()); // Show model evaluation
		btnQuit.setOnAction(e -> System.exit(0));           // Quit app
	}

	/**
	 * Event handler for btnEvaluate.
	 * Displays a window showing the relevant model's evaluation metrics.
	 */
	private void showEvaluationData() {
		Map<String, String> evalData = modelController.getEvaluationData();
		if (evalData != null) {
			TableDialog td = new TableDialog(evalData);
			td.displayTableDialog();
		} else {
			// No model run yet, no evaluation data to show-- Show error message.
			new Alert(AlertType.ERROR, "Run a prediction first, then click evaluate to see the model's evaluation metrics", ButtonType.OK).showAndWait();
		}
	}
	
	/**
	 * Event handler for btnUpdate.
	 * Invokes prediction on the model selected by user and displays data on chart
	 */
	private void updatePrediction() {
		try {
			// Get user input
			Model selectedModel = Model.getFromVerbose(cmbModel.getSelectionModel().getSelectedItem());
			PredictionWindow predictionWindow = PredictionWindow.getFromVerbose(cmbPredictionWindow.getSelectionModel().getSelectedItem());	
			String stockTicker = cmbStock.getSelectionModel().getSelectedItem();
			
			// Determine the model the user chose.
			if (selectedModel.equals(Model.LINEAR_REGRESSION)) {
				// User chose WEKA Linear Regression
				this.modelController = new LinearRegressionModelController(stockTicker, predictionWindow);
			} else if (selectedModel.equals(Model.ARIMA)) {
				// User chose ARIMA
				this.modelController = new ARIMAModelController(stockTicker, predictionWindow);
			} else {
				// Error: Model not found. Show error message
				new Alert(AlertType.ERROR, "Model not found", ButtonType.OK).showAndWait();
			}
			
			// Get data and predictions. Calling getPredictions() runs the model.
			Map<Date, Double> predictions = modelController.getPredictions();
			Map<Date, Double> historicData = modelController.getCurrentData();

			// Historic data + predictions on the end. TreeMaps automatically sort by Map key
			Map<Date, Double> allData = new TreeMap<>(Collections.reverseOrder());
			allData.putAll(historicData);
			allData.putAll(predictions);

			// Get dates into a List that the JavaFX LineChart can use.
			ObservableList<String> datesX = FXCollections.observableArrayList(
					allData.keySet().stream().map(Date::toString).collect(Collectors.toList()));

			// Define chart Axes
			CategoryAxis xAxis = new CategoryAxis();
			xAxis.setCategories(datesX);
			NumberAxis yAxis = new NumberAxis();  
			yAxis.setLabel("Closing Price");					

			// Create chart series for historical and predicted, so the user can tell them apart
			XYChart.Series<String, Number> srsHistorical = new XYChart.Series<>();
			srsHistorical.setName("Historical");
			XYChart.Series<String, Number> srsPredicted = new XYChart.Series<>();
			srsPredicted.setName("Predicted");

			// Reverse the order of the historical data, sorting by Date 	
			Map<Date, Double> reversedHistoricData = new TreeMap<>();
			reversedHistoricData.putAll(historicData);
			
			// Add historical data to the chart series
			for (Map.Entry<Date, Double> entry : reversedHistoricData.entrySet()) {
				srsHistorical.getData().add(new XYChart.Data<>(chartDateFormatter.format(entry.getKey()), entry.getValue()));	
			}

			// Add predicted data to the chart series
			for (Map.Entry<Date, Double> entry : predictions.entrySet()) {
				srsPredicted.getData().add(new XYChart.Data<>(chartDateFormatter.format(entry.getKey()), entry.getValue()));
			}

			// Connect the two series visually by adding the first point of the predicted series to the historical series
			srsHistorical.getData().add(srsPredicted.getData().get(0));
			
			// Clear the chart if it has been used already
			chtPrices.getData().clear();
			
			// Dont plot symbols on each point, makes the chart look ugly
			chtPrices.setCreateSymbols(false);
			
			// Add the series to the chart. JavaFX will then automatically refresh the chart to show the data.
			chtPrices.getData().add(srsHistorical);
			chtPrices.getData().add(srsPredicted);

		} catch (NullPointerException e) { // If the user has not selected something this exception is thrown
			// Stock or prediction window not chosen. Show error message.
			new Alert(AlertType.ERROR, "Please choose a stock and a prediction window", ButtonType.OK).showAndWait();
		}

	}
	
}