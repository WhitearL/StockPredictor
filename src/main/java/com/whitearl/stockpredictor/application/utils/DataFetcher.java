package com.whitearl.stockpredictor.application.utils;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.DAYS_IN_YEAR;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.FINNHUB_API_KEY;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.FINNHUB_CANDLE_RESOLUTION_DAILY;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.UNIX_CONVERSION_LONG;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.oscerd.finnhub.client.FinnhubClient;
import com.github.oscerd.finnhub.model.Candle;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Class for grabbing data from the Finnhub API
 * @author Lewis
 *
 */
public class DataFetcher {

	private FinnhubClient dataClient;

	/**
	 * Public constructor, allow instantiation
	 */
	public DataFetcher() {		
		// Initialise the API client with my personal API key
		this.dataClient = new FinnhubClient(FINNHUB_API_KEY);		
	}

	/**
	 * Get the stock prices for a given stock between the given dates
	 * @param ticker Stock alias e.g AAPL for Apple
	 * @param startDate Start date of the data to get
	 * @param endDate End date of the data to get
	 * @return A map with all the closing prices and timestamps
	 * @throws IOException Finnhub api exception intended to be caught by {@link getHistoricPrices}
	 */
	public Map<Date, Double> getPricesInTimeframe(String ticker, Date startDate, Date endDate) throws IOException {
		// TreeMap-- Automatic sorting of entries based on date.
		Map<Date, Double> historicPrices = new TreeMap<>();
		// Convert the dates to unix timestamps-- this is how dates are accepted by the API
		long unixStartTime = startDate.getTime() / UNIX_CONVERSION_LONG;
		long unixEndTime = endDate.getTime() / UNIX_CONVERSION_LONG;

		// Use the client to query finnhub for the data
		Candle candle = dataClient.getCandle(ticker, FINNHUB_CANDLE_RESOLUTION_DAILY, unixStartTime, unixEndTime);

		// Get response data
		List<Double> closingPrices = Arrays.asList(candle.getC());
		List<Long> days = Arrays.asList(candle.getT());
		
		for (int index = 0; index < closingPrices.size(); index++) {
			// Take the timestamp and the price for that timestamp
			historicPrices.put(new Date(days.get(index) * UNIX_CONVERSION_LONG), closingPrices.get(index));
		}		

		return historicPrices;
	}
	
	/**
	 * Get the historic prices for a stock going back a given number of years
	 * @param ticker Stock alias, eg AAPL for Apple
	 * @param yearsBack Number of years to get backdated data for
	 * @return A map of historic data with their timestamps and prices
	 */
	public Map<Date, Double> getHistoricPrices(String ticker, int yearsBack) {		

		Map<Date, Double> data = new TreeMap<>();

		Date startDate = new Date();
		Date endDate = new Date();

		// For each year given
		for (int i = 0; i < yearsBack; i++) {

			// Move the start date back one year
			startDate = modifyDateByDays(startDate, -DAYS_IN_YEAR);

			try {
				// Get the data and add to the map
				data.putAll(getPricesInTimeframe(ticker, startDate, endDate));
			} catch (IOException e) {
				// Error downloading data, show error message, break from loop and return empty map.
				new Alert(AlertType.ERROR, "There was a problem downloading the data from Finnhub: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
				return new TreeMap<>();
			}
			
			// Set the end date to the start date, and move them both back one day to prevent overlap
			endDate = startDate;
			endDate = modifyDateByDays(endDate, -1);
			startDate = modifyDateByDays(startDate, -1);
		}

		return data;
	}

	/**
	 * Modify a given date by a number of days
	 * @param date Date to modify
	 * @param days Days to modify by, use negative numbers to go back days
	 * @return The modified date
	 */
	public Date modifyDateByDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
}
