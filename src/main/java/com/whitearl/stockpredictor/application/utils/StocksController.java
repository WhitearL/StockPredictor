package com.whitearl.stockpredictor.application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.whitearl.stockpredictor.application.model.Stock;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller responsible for controlling the names of the companies that the app supports
 * This data is loaded into the front end combobox
 * @author Lewis
 *
 */
public class StocksController {

	// File name for companies csv file
	private static final String COMPANIES_CSV_FILE = "companies.csv";
	
	// Supported stock aliases
	private List<Stock> stocks;
	
	/**
	 * Public constructor, allow instantiation
	 */
	public StocksController() {
		// Load the stocks from the csv
		this.stocks = loadStocks(COMPANIES_CSV_FILE);
	}

	/**
	 * Return the stock given by the alias 
	 * @param ticker alias to search for
	 * @return The stock if found, null otherwise
	 */
	public Stock getStockWithTicker(String ticker) {
		for (Stock stock : this.stocks) {
			if (stock.getTicker().equals(ticker)) {
				return stock;
			}
		}
		
		return null;
	}
	
	/**
	 * Check if a given stock alias is supported by the app
	 * @param ticker Alias to check
 	 * @return boolean indicating whether the stock is managed by the app
	 */
	public boolean isStockValid(String ticker) {
		for (Stock stock : this.stocks) {
			if (stock.getTicker().equals(ticker)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Load the stocks from the company definition file
	 * @param fileName File to read from
	 * @return List of stocks
	 */
	private List<Stock> loadStocks(String fileName) {
        List<Stock> stocksList = new ArrayList<>();
		
        // Open the company definition file
        try (BufferedReader reader = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {   
			
        	// Stream the files lines, skipping the header
        	Stream<String> lines = reader.lines().skip(1); // Skip the header line
	
        	// For each line, create a new stock object with its data
			lines.forEachOrdered(line -> {
	            String[] splitCSV = line.split(",");
	            stocksList.add(new Stock(splitCSV[0], splitCSV[1]));
			});
		} catch (IOException e) {
			// Problem reading company file-- show error message
			new Alert(AlertType.ERROR, "There was a problem loading the companies definitions file: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}
		
		return stocksList;
	}

	/**
	 * @return The stocks managed by this app
	 */
	public List<Stock> getStocks() {
		return stocks;
	}
	
}
