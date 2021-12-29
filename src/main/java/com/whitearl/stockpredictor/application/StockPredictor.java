package com.whitearl.stockpredictor.application;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_FILE_NAME;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_ICON_FILE_PATH;
import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_TITLE;

import java.io.IOException;

import com.whitearl.stockpredictor.application.controllers.MainViewController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class StockPredictor extends Application {
	
	@Override
	public void start(Stage stage) {
		try {

			ClassLoader classLoader = getClass().getClassLoader();
		    FXMLLoader loader = new FXMLLoader(classLoader.getResource(MAIN_VIEW_FILE_NAME));
		    Parent root = loader.load();

		    MainViewController myController = loader.getController();

		    stage.setTitle(MAIN_VIEW_TITLE);
		    stage.getIcons().add(new Image(classLoader.getResourceAsStream(MAIN_VIEW_ICON_FILE_PATH)));
		    
		    Scene scene = new Scene(root);
		    stage.setScene(scene);
		    stage.show();

		    //Set Data to FXML through controller
		    myController.setData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}