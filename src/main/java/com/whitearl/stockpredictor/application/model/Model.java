package com.whitearl.stockpredictor.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum representing ML Model types supported by this app
 * @author Lewis
 *
 */
public enum Model {

	LINEAR_REGRESSION("Linear Regression"),
	ARIMA("ARIMA");
	
	private String verboseName;
	
	/**
	 * Private constructor, prevent instantiation
	 * @param verboseName The verbose representation of this enum instance
	 */
	private Model(String verboseName) {
		this.verboseName = verboseName;
	}
	
	/**
	 * @return The verbose strings associated with each type
	 */
	public static List<String> getVerboseNames() {
		return Arrays.asList(Model.values()).stream().map(Model::getVerboseName).collect(Collectors.toList());
	}

	/**
	 * Get the instance of the enum corresponding to the string value for 'verboseName'
	 * @param verboseName Input string to parse
	 * @return The enum instance represented by the string
	 */
	public static Model getFromVerbose(String verboseName) {
		for (Model model : Model.values()) {
			if (model.getVerboseName().equals(verboseName)) {
				return model;
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

}
