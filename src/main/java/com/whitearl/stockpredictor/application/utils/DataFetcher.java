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

public class DataFetcher {

	private FinnhubClient dataClient;

	public DataFetcher() {		
		this.dataClient = new FinnhubClient(FINNHUB_API_KEY);		
	}

	public Map<Date, Double> getPricesInTimeframe(String ticker, Date startDate, Date endDate) {
		// TreeMap-- Automatic sorting of entries based on date.
		Map<Date, Double> historicPrices = new TreeMap<>();
		try {
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
		} catch (IOException e) {
			e.printStackTrace();
		}		

		return historicPrices;
	}
	
	public Map<Date, Double> getHistoricPrices(String ticker, int yearsBack) {		

		Map<Date, Double> data = new TreeMap<>();

		Date startDate = new Date();
		Date endDate = new Date();
		for (int i = 0; i < yearsBack; i++) {

			startDate = modifyDateByDays(startDate, -DAYS_IN_YEAR);

			data.putAll(getPricesInTimeframe(ticker, startDate, endDate));
			
			endDate = startDate;
			endDate = modifyDateByDays(endDate, -1);
			startDate = modifyDateByDays(startDate, -1);
		}

		return data;
	}

	public Date modifyDateByDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
}
