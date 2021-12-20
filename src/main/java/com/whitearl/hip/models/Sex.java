package com.whitearl.hip.models;

/**
 * Enum for mapping biological sex to numbers for linear regression
 * @author WhitearL
 *
 */
public enum Sex {

	MALE(1, "male"),
	FEMALE(2, "female");
	
	private int number;
	private String nominalValue;
	
	/**
	 * Private constructor, prevent instantiation
	 * @param number Number the given sex is mapped to.
	 */
	private Sex(int number, String nominalValue) {
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
