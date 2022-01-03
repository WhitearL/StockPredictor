package com.whitearl.stockpredictor.application.learning.modelcontrollers;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.github.rcaller.rstuff.RCaller;
import com.github.rcaller.rstuff.RCode;
import com.whitearl.stockpredictor.application.utils.DataFetcher;

public class ARIMAModelController extends ModelController {
			
	private static final String CSV_TEMP_FILE_NAME = "tempData.csv";
	private static final String CSV_HEADER = "close";
	private static final File CSV_TEMP_FILE = new File(CSV_TEMP_FILE_NAME);
	
	private Map<Date, Double> predictions;
	private Map<Date, Double> currentData;
	
    public ARIMAModelController(String stockTicker) {
		try {

			cleanTempFiles();
			
			DataFetcher df = new DataFetcher();
		
			this.currentData = df.getHistoricPrices(stockTicker, 2);
			this.predictions = new TreeMap<>();	
			
			writeMapDataToCSV(this.currentData);
			
			double[] predictionsArray = generatePredictions(this.currentData.values().stream().mapToDouble(Double::doubleValue).toArray());
			
			System.out.println(predictionsArray.length);
			
			Date priceDate = new Date();
			int i = 0;
			for (double cPrice : predictionsArray) {
				this.predictions.put(priceDate, cPrice);
				priceDate = modifyDateByDays(priceDate, 1);
				i++;
			}
			System.out.println(i);
			System.out.println(predictions.size());
			
			cleanTempFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}	
    }
	
    public double[] generatePredictions(double[] stockClosePrices) {
        try {

          RCaller caller = RCaller.create();

          RCode code = RCode.create();

          code.addDoubleArray("x", stockClosePrices);
          code.R_require("forecast");
          code.addRCode("ww <- auto.arima(ts(x, frequency=100),D=1)");
          code.addRCode("tt <- forecast(ww,h=100)");

          code.addRCode("myResult <- list(mean = as.numeric(tt$mean), lower=tt$lower, fitted = as.double(tt$fitted))");

          caller.setRCode(code);
          caller.runAndReturnResult("myResult");

          /**
           * It is good to have a look at the XML file
           * for having info about which variables are passed to result
           */
          System.out.println(caller.getParser().getXMLFileAsString());

          double[] upValues = caller.getParser().getAsDoubleArray("mean");
          double[] loValues = caller.getParser().getAsDoubleArray("lower");
          double[] fitted = caller.getParser().getAsDoubleArray("fitted");
          
          return upValues;

        } catch (Exception e) {
        	e.printStackTrace();
        }

        return new double[0];
      }
    
    private void writeMapDataToCSV(Map<Date, Double> data) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_TEMP_FILE, true))) {
			StringBuilder sb = new StringBuilder();
			bw.write(CSV_HEADER + System.lineSeparator());
			for (Map.Entry<Date, Double> entry : data.entrySet()) {
				sb.append(entry.getValue() + System.lineSeparator());	
			}
			bw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
       
    private void cleanTempFiles() {
    	if (CSV_TEMP_FILE.exists()) {CSV_TEMP_FILE.delete();} 
    }

	public Date modifyDateByDays(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

    
    private String loadRScript(String scriptFile) {    
    	StringBuilder sb = new StringBuilder();
    	InputStream is = getClass().getClassLoader().getResourceAsStream(scriptFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + System.lineSeparator());
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
        return sb.toString();
    }

	public Map<Date, Double> getPredictions() {
		return predictions;
	}

	public Map<Date, Double> getCurrentData() {
		return currentData;
	}
}
