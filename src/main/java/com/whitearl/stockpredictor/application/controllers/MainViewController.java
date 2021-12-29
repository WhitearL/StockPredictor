package com.whitearl.stockpredictor.application.controllers;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class MainViewController implements Initializable
{

	@FXML
	private ComboBox<String> cmbStock;
	
	@FXML
	private ComboBox<String> cmbPredictionWindow;
	
	@FXML
	private Label lblStock;
	
	@FXML
	private Label lblPredictionWindow;
	
	@FXML
	private LineChart<Date, Double> chtPrices;
	
	@FXML
	private Button btnUpdate;
	
	@FXML
	private Button btnQuit;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	public void setData(){

		cmbStock.getItems().clear();

		cmbStock.getItems().addAll(
				"jacob.smith@example.com",
				"isabella.johnson@example.com",
				"ethan.williams@example.com",
				"emma.jones@example.com",
				"michael.brown@example.com");
	}
}