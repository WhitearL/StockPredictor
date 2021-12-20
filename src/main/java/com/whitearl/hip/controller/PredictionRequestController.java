package com.whitearl.hip.controller;

import static com.whitearl.hip.constants.HIPConstants.PROCESSED_ARFF_FILE_NAME;

import java.io.File;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whitearl.hip.learning.MultiRegressor;

import weka.core.DenseInstance;
import weka.core.Instance;

@Controller
@RequestMapping(value = "/prediction")
public class PredictionRequestController {

	private Logger logger = LoggerFactory.getLogger(PredictionRequestController.class);
	
	private MultiRegressor model;
	
    @GetMapping(path = "/predict", produces = "application/json")
    @ResponseBody
    public String predictPrice(
    		@RequestParam(required = true) int age,
    		@RequestParam(required = true) int sex,
    		@RequestParam(required = true) int bmi,
    		@RequestParam(required = true) int children,
    		@RequestParam(required = true) int smokerStatus,
    		@RequestParam(required = true) int region
    	) {
    	
    	if (ensureModelExists()) {
    		Instance inst = new DenseInstance(7);
    		inst.setDataset(this.model.getDataSet());
    		inst.setValue(0, age);
    		inst.setValue(1, sex);
    		inst.setValue(2, bmi);
    		inst.setValue(3, children);
    		inst.setValue(4, smokerStatus);  
    		inst.setValue(5, region);
    		
    		JsonObject jsObj = new JsonObject();
    		jsObj.addProperty("price", model.predict(inst));

    		return jsObj.toString();
    	} else {
    		return "";
    	}
    }
	
    /**
     * Ensure that the model exists. Attempt to create it if missing.
     * If the data file is missing the model will not be created and all requests will fail.
     * @return A boolean indicating model 
     */
    private boolean ensureModelExists() {
    	String appDir = System.getProperty("user.dir");
    	File arffDataFile = new File(Paths.get(appDir, PROCESSED_ARFF_FILE_NAME).toString());
    	
    	if (!arffDataFile.exists()) {
    		logger.error("Missing data file. Cannot fulfil request");	
    	} else {
    		// Create model if it doesnt exist
    		if (this.model == null) {
    			this.model = new MultiRegressor(arffDataFile.toString());
    		}
    		return true;
    	}
    	return false;
    }
}
