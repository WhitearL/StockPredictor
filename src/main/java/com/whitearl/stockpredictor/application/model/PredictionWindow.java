package com.whitearl.stockpredictor.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PredictionWindow {

	// Time units with its length in seconds
	ONE_DAY("1 Day", 86400L),
	ONE_WEEK("7 Days", 604800L),
	ONE_MONTH("30 Days", 2592000L),
	ONE_YEAR("365 Days", 31536000L);
	
	private String verboseName;
	private Long inSeconds;
	
	private PredictionWindow(String verboseName, Long inSeconds) {
		this.verboseName = verboseName;
		this.inSeconds = inSeconds;
	}

	public static List<String> getVerboseNames() {
		return Arrays.asList(
				PredictionWindow.values())
					.stream()
					.map(PredictionWindow::getVerboseName)
					.collect(Collectors.toList());
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
	
}
