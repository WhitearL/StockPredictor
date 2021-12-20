package com.whitearl.hip.models;

/**
 * Enum for mapping the regions to numbers so we can do linear regression
 * @author WhitearL
 *
 */
public enum Region {

	SOUTH_EAST(1, "southeast"),
	SOUTH_WEST(2, "southwest"),
	NORTH_EAST(3, "northeast"),
	NORTH_WEST(4, "northwest");
	
	private int number;
	private String nominalValue;
	
	/**
	 * Private constructor, prevent instantiation
	 * @param number Number region is mapped to.
	 * @param nominalValue The verbose value for this region
	 */
	private Region(int number, String nominalValue) {
		this.number = number;
		this.nominalValue = nominalValue;
	}

	public String getNominalValue() {
		return nominalValue;
	}

	public int getNumber() {
		return number;
	}
	
}
