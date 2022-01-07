package com.whitearl.stockpredictor.application.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test for {@link StocksController}
 *
 * @author Lewis
 *
 */
public class StocksControllerTest {

	private static final String TEST_STOCK = "AAPL";

	/**
	 * Full run through of stock controller
	 */
	@Test
	public void testARIMAController() {
		// Start a new stock controller
		StocksController sc = new StocksController();
		
		// If stocks are not loaded in then these will fail
		// Check stocks are loaded
		assertNotNull(sc.getStocks());
		assertNotEquals(sc.getStocks().size(), 0);
		
		// Check the correct stock is returned on request
		assertNotNull(sc.getStockWithTicker(TEST_STOCK));
		assertEquals(TEST_STOCK, sc.getStockWithTicker(TEST_STOCK).getTicker());

	}

}
