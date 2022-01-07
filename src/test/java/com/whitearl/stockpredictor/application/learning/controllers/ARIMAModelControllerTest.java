package com.whitearl.stockpredictor.application.learning.controllers;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.whitearl.stockpredictor.application.learning.modelcontrollers.ARIMAModelController;
import com.whitearl.stockpredictor.application.model.PredictionWindow;

/**
 * Test for {@link ARIMAModelController}
 * Also tests {@link DataFetcher}
 * @author Lewis
 *
 */
public class ARIMAModelControllerTest {

	private static final String TEST_STOCK = "AAPL";

	/**
	 * Full run through of arima model controller
	 */
	@Test
	public void testARIMAController() {
		// Start a new model controller
		ARIMAModelController amc = new ARIMAModelController(TEST_STOCK, PredictionWindow.ONE_WEEK);
		
		// Check the model returns data for all the outputs
		assertNotNull(amc.getCurrentData());
		assertNotNull(amc.getEvaluationData());
		assertNotNull(amc.getPredictions());
		
		// Check the outputs are not empty
		assertNotEquals(amc.getCurrentData().size(), 0);
		assertNotEquals(amc.getEvaluationData().size(), 0);
		assertNotEquals(amc.getPredictions().size(), 0);
	}

}
