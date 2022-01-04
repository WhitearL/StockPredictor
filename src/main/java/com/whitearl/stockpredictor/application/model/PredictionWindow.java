package com.whitearl.stockpredictor.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum representing prediction windows 
 * @author Lewis
 *
 */
public enum PredictionWindow {

	// Time units with its length in days and seconds
	ONE_DAY("1 Day", 1, 86400L), 
	ONE_WEEK("7 Days", 7, 604800L), 
	ONE_MONTH("30 Days", 30, 2592000L), 
	ONE_YEAR("365 Days", 365, 31536000L);

	private String verboseName;
	private Long inSeconds;
	private int inDays;
	
	/**
	 * Private constructor, prevent instantiation
	 * @param verboseName The verbose representation of this enum instance
	 * @param inDays The prediction window's length in days
	 * @param inSeconds the prediction window's length in seconds
	 */
	private PredictionWindow(String verboseName, int inDays, Long inSeconds) {
		this.verboseName = verboseName;
		this.inSeconds = inSeconds;
		this.inDays = inDays;
	}
	
	/**
	 * @return The verbose strings associated with each type
	 */
	public static List<String> getVerboseNames() {
		return Arrays.asList(PredictionWindow.values()).stream().map(PredictionWindow::getVerboseName).collect(Collectors.toList());
	}

	/**
	 * Get the instance of the enum corresponding to the string value for 'verboseName'
	 * @param verboseName Input string to parse
	 * @return The enum instance represented by the string
	 */
	public static PredictionWindow getFromVerbose(String verboseName) {
		for (PredictionWindow pw : PredictionWindow.values()) {
			if (pw.getVerboseName().equals(verboseName)) {
				return pw;
			}
		}
		return null;
	}

	/**
	 * @return The verbose representation of this enum instance
	 */
	public String getVerboseName() {
		return verboseName;
	}

	/**
	 * @return The prediction window's length in seconds
	 */
	public Long getInSeconds() {
		return inSeconds;
	}

	/**
	 * @return The prediction window's length in days
	 */
	public int getInDays() {
		return inDays;
	}

}
