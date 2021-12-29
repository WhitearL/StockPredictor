package com.whitearl.stockpredictor.application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.whitearl.stockpredictor.application.model.Stock;

public class StocksController {

	private static final String COMPANIES_CSV_FILE = "companies.csv";
	
	private List<Stock> stocks;
	
	public StocksController() {
		this.stocks = loadStocks(COMPANIES_CSV_FILE);
	}

	public Stock getStockWithTicker(String ticker) {
		for (Stock stock : this.stocks) {
			if (stock.getTicker().equals(ticker)) {
				return stock;
			}
		}
		
		return null;
	}
	
	public boolean isStockValid(String ticker) {
		for (Stock stock : this.stocks) {
			if (stock.getTicker().equals(ticker)) {
				return true;
			}
		}
		
		return false;
	}
	
	private List<Stock> loadStocks(String fileName) {
        List<Stock> stocksList = new ArrayList<>();
		
        try (BufferedReader reader = new BufferedReader(
        		new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {   
			Stream<String> lines = reader.lines().skip(1); // Skip the header line
	
			lines.forEachOrdered(line -> {
	            String[] splitCSV = line.split(",");
	            stocksList.add(new Stock(splitCSV[0], splitCSV[1]));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stocksList;
	}

	public List<Stock> getStocks() {
		return stocks;
	}
	
}
