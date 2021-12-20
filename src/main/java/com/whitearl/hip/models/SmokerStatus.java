package com.whitearl.hip.models;

/**
 * Enum for mapping smoker status to numbers for linear regression
 * @author WhitearL
 *
 */
public enum SmokerStatus {

	SMOKER(1, "yes"),
	NON_SMOKER(0, "no");
	
	private int number;
	private String nominalValue;
	
	/**
	 * Private constructor, prevent instantiation
	 * @param number Number the given smoker status is mapped to.
	 * @param nominalValue The verbose value for this smoker status (yes for SMOKER, no for NON_SMOKER)
	 */
	private SmokerStatus(int number, String nominalValue) {
		this.number = number;
		this.nominalValue = nominalValue;
	}

	public int getNumber() {
		return number;
	}

	public String getNominalValue() {
		return nominalValue;
	}
	
}
