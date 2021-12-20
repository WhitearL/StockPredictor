package com.whitearl.hip;

import static com.whitearl.hip.constants.HIPConstants.PROCESSED_ARFF_FILE_NAME;
import static com.whitearl.hip.constants.HIPConstants.UNPROCESSED_CSV_FILE_NAME;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.whitearl.hip.learning.MultiRegressor;
import com.whitearl.hip.preprocessing.DataPreProcessor;

@SpringBootApplication
public class HealthInsurancePredictorApplication {


	
	public static void main(String[] args) throws IOException {
		String appDir = System.getProperty("user.dir");
		
		String arffFilePath = Paths.get(appDir, PROCESSED_ARFF_FILE_NAME).toString();
		DataPreProcessor dpp = 
				new DataPreProcessor(
						new File(Paths.get(appDir, UNPROCESSED_CSV_FILE_NAME).toString()),
						new File(arffFilePath));
		
		MultiRegressor model = new MultiRegressor(arffFilePath);
		
		SpringApplication.run(HealthInsurancePredictorApplication.class, args);
		
		
		
	}

}
