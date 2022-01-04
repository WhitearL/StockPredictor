package com.whitearl.stockpredictor.application.model;

/**
 * Class representing a stock from the company definition CSV file
 * @author Lewis
 *
 */
public class Stock {

	private String ticker;
	private String name;
	
	/**
	 * Public constructor, allow instantiation
	 * @param ticker Stock alias, e.g. AAPL for Apple
	 * @param name Full name of the company
	 */
	public Stock(String ticker, String name) {
		this.ticker = ticker;
		this.name = name;
	}
	
	/**
	 * @return The stock alias
	 */
	public String getTicker() {
		return ticker;
	}

	/**
	 * Set the stock alias
	 * @param ticker Stock alias to set to
	 */
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	/**
	 * @return The company name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the company name
	 * @param name Company name to set to
	 */
	public void setName(String name) {
		this.name = name;
	}

}
