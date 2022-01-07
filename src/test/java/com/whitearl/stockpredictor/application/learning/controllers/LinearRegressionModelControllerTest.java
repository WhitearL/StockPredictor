package com.whitearl.stockpredictor.application.learning.controllers;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.whitearl.stockpredictor.application.learning.modelcontrollers.LinearRegressionModelController;
import com.whitearl.stockpredictor.application.model.PredictionWindow;
import com.whitearl.stockpredictor.application.utils.DataFetcher;

/**
 * Test for {@link LinearRegressionModelController}
 * Also tests {@link DataFetcher}
 * @author Lewis
 *
 */
public class LinearRegressionModelControllerTest {

	private static final String TEST_STOCK = "AAPL";

	/**
	 * Full run through of linear regression model controller
	 */
	@Test
	public void testARIMAController() {
		// Start a new model controller
		LinearRegressionModelController lrc = new LinearRegressionModelController(TEST_STOCK, PredictionWindow.ONE_WEEK);
		
		// Check the model returns data for all the outputs
		assertNotNull(lrc.getCurrentData());
		assertNotNull(lrc.getEvaluationData());
		assertNotNull(lrc.getPredictions());
		
		// Check the outputs are not empty
		assertNotEquals(lrc.getCurrentData().size(), 0);
		assertNotEquals(lrc.getEvaluationData().size(), 0);
		assertNotEquals(lrc.getPredictions().size(), 0);
	}

}
