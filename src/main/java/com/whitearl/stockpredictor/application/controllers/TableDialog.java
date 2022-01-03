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
 
 
public class TableDialog {
	
	private static final Insets PADDING = new Insets(10, 0, 0, 10);
	private static final int EDGE_SPACING = 5;
	private static final int FONT_SIZE = 30;
	
	private static final String TITLE = "Model Evaluation";
	private static final String FONT = "Arial";

	
	private Map<String, String> data;
	
 	public TableDialog(Map<String, String> data) {
		this.data = data;
	}
	
    public void displayTableDialog() {
    	Stage stage = new Stage();
        Scene scene = new Scene(new Group());
        stage.setTitle(TITLE);
        stage.setWidth(400);
        stage.setHeight(300);
 
	    stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream(MAIN_VIEW_ICON_FILE_PATH)));
        
        Label label = new Label(TITLE);
        label.setFont(new Font(FONT, FONT_SIZE));
 
        GridPane pane = new GridPane();
        pane.addColumn(0);
        pane.addColumn(1);     
        
        int index = 0;
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
        	Label key = new Label(entry.getKey());
        	key.setPadding(PADDING);
        	
        	Label value = new Label(entry.getValue());
        	value.setPadding(PADDING);
        	
        	pane.addRow(index, key, value);
        	index++;
        }
        
        
        VBox vbox = new VBox();
        vbox.setSpacing(EDGE_SPACING);
        vbox.setPadding(PADDING);
        vbox.getChildren().addAll(label, pane);
 
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        stage.setScene(scene);
        stage.show();
    }
}