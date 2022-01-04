package com.whitearl.stockpredictor.application;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_FILE_NAME;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_ICON_FILE_PATH;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_TITLE;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Application entry class
 * @author Lewis
 *
 */
public class StockPredictor extends Application {
	
	@Override
	public void start(Stage stage) {
		try {

			// Load the FXML GUI definition file to lay out the GUI
			ClassLoader classLoader = getClass().getClassLoader();
		    FXMLLoader loader = new FXMLLoader(classLoader.getResource(MAIN_VIEW_FILE_NAME));
		    Parent root = loader.load();
    
		    // Set the window title and icon
		    stage.setTitle(MAIN_VIEW_TITLE);
		    stage.getIcons().add(new Image(classLoader.getResourceAsStream(MAIN_VIEW_ICON_FILE_PATH)));

		    // Set the scene using the FXML UI data
		    Scene scene = new Scene(root);
		    
		    // Show the main window
		    stage.setScene(scene);
		    stage.show();
		   
		} catch (IOException e) {
			// Problem loading GUI assets-- Show error message
			new Alert(AlertType.ERROR, "There was a problem initialising the GUI: " + System.lineSeparator() + e, ButtonType.OK).showAndWait();
		}
	}

	/**
	 * Application entry point
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		// Launch the JavaFX app
		launch(args);
	}
}