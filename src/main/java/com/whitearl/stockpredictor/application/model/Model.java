package com.whitearl.stockpredictor.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Model {

	LINEAR_REGRESSION("Linear Regression"),
	ARIMA("ARIMA");
	
	private String verboseName;
	private Model(String verboseName) {
		this.verboseName = verboseName;
	}
	
	public static List<String> getVerboseNames() {
		return Arrays.asList(Model.values()).stream().map(Model::getVerboseName).collect(Collectors.toList());
	}

	public static Model getFromVerbose(String verboseName) {
		for (Model model : Model.values()) {
			if (model.getVerboseName().equals(verboseName)) {
				return model;
			}
		}
		return null;
	}

	public String getVerboseName() {
		return verboseName;
	}

}
