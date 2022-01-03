package com.whitearl.stockpredictor.application.controllers;

import static com.whitearl.stockpredictor.application.constants.StockPredictorConstants.MAIN_VIEW_ICON_FILE_PATH;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
 
/**
 * Custom JavaFX dialog to show values in tabulated form.
 * Accepts a Map, puts keys in left column, and values in right column
 * @author Lewis
 *
 */
public class TableDialog {
	
	// UI Constants for padding, spacing, titles and fonts
	private static final Insets PADDING = new Insets(10, 0, 0, 10);
	private static final int EDGE_SPACING = 5;
	private static final int FONT_SIZE = 30;	
	private static final String TITLE = "Model Evaluation";
	private static final String FONT = "Arial";

	// Data to show on the table, given by constructor*/
	private Map<String, String> data;
	
	/**
	 * Public constructor, allow instantiation
	 * @param data Data to show on the dialog
	 */
 	public TableDialog(Map<String, String> data) {
		this.data = data;
	}
	
 	/**
 	 * Render and show the table dialog.
 	 */
 	public void displayTableDialog() {
 		// JavaFX window setup
    	Stage stage = new Stage();
        Scene scene = new Scene(new Group());
        stage.setTitle(TITLE);
        stage.setWidth(400);
        stage.setHeight(300);
 
        // Set the window's icon
	    stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream(MAIN_VIEW_ICON_FILE_PATH)));
        
	    // Set the dialog title and its font
        Label lblTitle = new Label(TITLE);
        lblTitle.setFont(new Font(FONT, FONT_SIZE));
 
        // Pane with two columns
        GridPane gridPane = new GridPane();
        gridPane.addColumn(0);
        gridPane.addColumn(1);     
        
        int rowNumber = 0;
        // Iterate over the data Map
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
        	// Render the data key value pairs as JavaFX labels
        	Label key = new Label(entry.getKey());
        	key.setPadding(PADDING);
        	Label value = new Label(entry.getValue());
        	value.setPadding(PADDING);
        	
        	// Add those labels to a row with row number 'rowNumber'
        	gridPane.addRow(rowNumber, key, value);
        	rowNumber++;
        }
        
        // Container box for title and grid pane. Set spacing and padding. Add grid pane and title
        VBox vbox = new VBox();
        vbox.setSpacing(EDGE_SPACING);
        vbox.setPadding(PADDING);
        vbox.getChildren().addAll(lblTitle, gridPane);
 
        // Add all JavaFX controls to the Scene 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        // Set the scene and show the dialog
        stage.setScene(scene);
        stage.show();
    }
}