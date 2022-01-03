package com.whitearl.stockpredictor.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PredictionWindow {

	// Time units with its length in days and seconds
	ONE_DAY("1 Day", 1, 86400L), 
	ONE_WEEK("7 Days", 7, 604800L), 
	ONE_MONTH("30 Days", 30, 2592000L), 
	ONE_YEAR("365 Days", 365, 31536000L);

	private String verboseName;
	private Long inSeconds;
	private int inDays;
	
	private PredictionWindow(String verboseName, int inDays, Long inSeconds) {
		this.verboseName = verboseName;
		this.inSeconds = inSeconds;
		this.inDays = inDays;
	}

	public static List<String> getVerboseNames() {
		return Arrays.asList(PredictionWindow.values()).stream().map(PredictionWindow::getVerboseName).collect(Collectors.toList());
	}

	public static PredictionWindow getFromVerbose(String verboseName) {
		for (PredictionWindow pw : PredictionWindow.values()) {
			if (pw.getVerboseName().equals(verboseName)) {
				return pw;
			}
		}
		return null;
	}

	public String getVerboseName() {
		return verboseName;
	}

	public Long getInSeconds() {
		return inSeconds;
	}

	public int getInDays() {
		return inDays;
	}

}
