package com.whitearl.hip.preprocessing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whitearl.hip.models.Region;
import com.whitearl.hip.models.Sex;
import com.whitearl.hip.models.SmokerStatus;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 * Preprocess CSV data, and convert to ARFF for Weka
 * @author WhitearL
 *
 */
public class DataPreProcessor {
	
	private Logger logger = LoggerFactory.getLogger(DataPreProcessor.class);
	
	private static final String PROCESSED_DATA_FILE_NAME = "health-insurance-data-processed.csv";
	
	private List<String> unprocessedData;
	
	private List<String> processedData;
	
	/**
	 * Public constructor, allow instantiation
	 * @param csvFile File to read data from.
	 * @throws IOException 
	 */
	public DataPreProcessor(File csvFile, File outFile) throws IOException {
		this.unprocessedData = loadData(csvFile);
		this.processedData = convertNominalToNumeric(unprocessedData);
		
		File processedDataFile = new File(
				Paths.get(csvFile.getParentFile().getAbsolutePath(), PROCESSED_DATA_FILE_NAME).toString());
		// Write preprocessed data to a new file
		writeData(this.processedData, processedDataFile);
		// Convert the new file to arff for weka
		convertCSVtoARFF(processedDataFile, outFile);
	}
	
	/**
	 * Convert a csv file to arff
	 * @param csvFile CSV file to convert
	 * @param outFile Output file as arff
	 */
    public void convertCSVtoARFF(File csvFile, File outFile) {

        try {
	        CSVLoader loader = new CSVLoader();
	        
	        String [] options = new String[1];
	        options[0]="-H";
	        loader.setOptions(options);
	        loader.setSource(csvFile);

	        Instances data = loader.getDataSet();
	
	        ArffSaver saver = new ArffSaver();
	        saver.setInstances(data);
	        saver.setFile(outFile);
	        saver.writeBatch();
	        
        } catch (IOException e) {
        	logger.error("IO error converting file {} to ARFF", csvFile.getName());
        } catch (Exception e) {
        	logger.error("Error converting file {} to ARFF", csvFile.getName());
        }
    }
	
	/**
	 * Convert the nominal fields in the data to numeric.
	 * These fields are: 
	 * {@link Region} {@link Sex}, {@link SmokerStatus}
	 * @return Processed list of lines
	 */
	private List<String> convertNominalToNumeric(List<String> unprocessedData) {
		
		List<String> processedLines = new ArrayList<>();
		
		unprocessedData.forEach(csvLine -> {

			// Convert regions to numbers
			for (Region region : Region.values()) {
				csvLine = csvLine.replace(region.getNominalValue(), String.valueOf(region.getNumber()));
			}
			
			// Convert sexes to numbers
			for (Sex sex : Sex.values()) {
				/**
				 * Regular expression note
				 * Since the method searches for feMALE, it replaces the substring male in female with the number for male.
				 * To get around this I have denoted the start and end of the strings in the csv with commas
				 * I.E ',male,' gets changed to ',1,' and ',female,' gets changed to ',2,'
				 */
				csvLine = csvLine.replace(("," + sex.getNominalValue() + ","), ("," + String.valueOf(sex.getNumber()) + ","));
			}
			
			// Convert binary smoker status to numbers
			for (SmokerStatus sStatus : SmokerStatus.values()) {
				csvLine = csvLine.replace(sStatus.getNominalValue(), String.valueOf(sStatus.getNumber()));
			}
			
			processedLines.add(csvLine);
		});
		
		return processedLines;
	}
	
	/**
	 * Read the data from csv
	 * @return List of lines from the file.
	 */
	private List<String> loadData(File csvFile) {
		try {
			List<String> data = Files.readAllLines(csvFile.toPath(), StandardCharsets.UTF_8);
			
			// Remove the CSV header
			data.remove(0);
			return data;
		} catch (IOException e) {
			logger.error("Error while reading file {}", csvFile.getName());
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	/**
	 * Write list of strings to a file one per line.
	 * @param data Data to write
	 * @param outFile File to write to
	 */
	private void writeData(List<String> data, File outFile) {	
		try {
			if (!outFile.exists() && !outFile.createNewFile()) {
				throw new IOException();
			}
			
			Files.write(outFile.toPath(), data, Charset.defaultCharset());
		} catch (IOException e) {
			logger.error("Error writing data to file {}", outFile.getName());
			e.printStackTrace();
		}
	}
}
