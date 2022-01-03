package com.whitearl.stockpredictor.application.constants;

/**
 * Constants for Stock Predictor Application
 * @author Lewis
 *
 */
public final class StockPredictorConstants {

	/**
	 * Private constructor, prevent instantiation
	 */
	private StockPredictorConstants() {}

	// 2DP format for numbers
	public static final String DECIMAL_FORMAT = "%.2f";
	
	// Paths for GUI assets
	public static final String MAIN_VIEW_FILE_NAME = "main-view.fxml";
	public static final String MAIN_VIEW_ICON_FILE_PATH = "img/icon.png";

	// Window title
	public static final String MAIN_VIEW_TITLE = "Stock Predictor";

	// API Key for accessing FinnHub for stock data
	public static final String FINNHUB_API_KEY = "c75p7p2ad3i9kvgaqe30";
	
	// Parameter for API requests to get daily information
	public static final String FINNHUB_CANDLE_RESOLUTION_DAILY = "D";
	
	// Factor for converting back and forth between seconds and millis for Unix Timestamps
	public static final long UNIX_CONVERSION_LONG = 1000L;
	
	// Days in the year, for frequencies and seasonalities
	public static final int DAYS_IN_YEAR = 365;
	
	// Seed and number of folds to use in ML models
	public static final int ML_SEED = 141;
	public static final int ML_FOLDS = 3;

}
