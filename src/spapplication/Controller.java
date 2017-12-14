package spapplication;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

public class Controller {
	@FXML private ColorPicker colorPicker;
	@FXML private BorderPane body;
	@FXML private GridPane canvasGrid;
	final FileChooser fileChooser = new FileChooser();
	private int imageSize = 16;
	@FXML void changeColour(ActionEvent e) {
		Button currentButton = (Button)e.getSource();
		currentButton.setStyle("-fx-background-color: rgb(" + 
				(int)(colorPicker.getValue().getRed() * 256) + ", " +
				(int)(colorPicker.getValue().getGreen() * 256) +  ", " +
				(int)(colorPicker.getValue().getBlue() * 256) + 
				")" + 
				"; -fx-text-fill: white;");
	}
	
	@FXML void saveFile() {
	    BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
	    GridPane gridPane = (GridPane)body.getCenter();
	    for(Node node : gridPane.getChildren()) {
	    	if(node.getStyle() != "") {
	    		System.out.println(node.getStyle());
	    		System.out.println(node.getStyle().matches("background"));
	    		 List<String> allMatches = new ArrayList<String>();
	    		 CharSequence yourStringHere = node.getStyle();
				Matcher m = Pattern.compile("\\d+")
	    		     .matcher(yourStringHere);
	    		 while (m.find()) {
	    		   allMatches.add(m.group());
	    		   System.out.println(m.group());
	    		 }
	    		 int rgbValues[] = new int[3];
	    		 for(int i = 0; i < 3; i++) {
	    			 rgbValues[i] = Integer.parseInt(allMatches.get(i)) - 1;
	    			 if(rgbValues[i] == -1) 
	    				 rgbValues[i] += 1;
	    		 }
	    		 System.out.println((node.getStyle()));
	    		 System.out.println(rgbValues[0] + ", " + rgbValues[1] + ", " + rgbValues[2]);
			    image.setRGB(GridPane.getColumnIndex(node), GridPane.getRowIndex(node), new Color(rgbValues[0], rgbValues[1], rgbValues[2]).getRGB());
	    	}
	    }

	    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
	    fileChooser.getExtensionFilters().add(extFilter);
	    File ImageFile = fileChooser.showSaveDialog(body.getScene().getWindow());
	    try {
	        ImageIO.write(image, "png", ImageFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML void newFile(ActionEvent e) {
		MenuItem currentItem = (MenuItem)e.getSource();
		int text = (int)currentItem.getText().charAt(0) - 48;
		switch (text) {
			case(1):
				destroyGrid();
				createGrid(16);
				break;
			case(3): 
				destroyGrid();
				createGrid(32);	
				break;
			case(6): 
				destroyGrid();
				createGrid(64);
				break;
		}
	}
	@FXML
	void openFile(ActionEvent e) throws IOException {
		File file = fileChooser.showOpenDialog(body.getScene().getWindow());
		if (file != null) {
			BufferedImage in = ImageIO.read(file);
			if(in.getWidth() == 16 && in.getHeight() == 16) {
				createGrid(16);
				create2dArray(in);
			}else if(in.getWidth() == 32 && in.getHeight() == 32) {
				createGrid(32);
				create2dArray(in);
			} else if(in.getWidth() == 64 && in.getHeight() == 64) {
				createGrid(64);
				create2dArray(in);
			} else {
				System.out.println("Can't open file, has to be 16x16, 32x32 or 64x64");
			}
        }
	}
	
	void create2dArray(BufferedImage in) {
		for(Node node : canvasGrid.getChildren()) {
			if(GridPane.getColumnIndex(node) == null || GridPane.getRowIndex(node) == null) {
			} else {
				int rgbValue = in.getRGB(GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
				node.setStyle("-fx-background-color: rgb(" +
				         ((rgbValue >> 32) & 0xFF)+ ", " +
						 ((rgbValue >> 16) & 0xFF)+ ", " +
						 ((rgbValue >> 8) & 0xFF) +  ", " +
						 ((rgbValue) & 0xFF) + 
						")" + 
						"; -fx-text-fill: white;");;
				System.out.println(node.getStyle());
			}
			
		}
	}
	
	private void createButton(GridPane gridpane, int i, int j, int size) {
		Button button = new Button();
		button.setPrefHeight(25 / ((float)size / 25));
		button.setPrefWidth(25 / ((float)size / 25));
		button.setMaxHeight(25 / ((float)size / 25));
		button.setMaxWidth(25 / ((float)size / 25));
		button.setMinHeight(25 / ((float)size / 25));
		button.setMinWidth(25 / ((float)size / 25));
		button.setOnAction(e -> changeColour(e));
		gridpane.add(button, i, j);
	}
	
	private void createGrid(int size) {
		imageSize = size;
		GridPane gridpane = new GridPane();
		gridpane.setGridLinesVisible(true);
		gridpane.setAlignment(Pos.CENTER);
		for(int i = 0; i < size; i++) {
			gridpane.getColumnConstraints().add(new ColumnConstraints(25 / ((float)size / 25)));
		}
		for(int j = 0; j < size; j++) {
			gridpane.getRowConstraints().add(new RowConstraints(25 / ((float)size / 25)));
			
		}	
		for(int i = 0; i < (size); i++) {
			for(int j = 0; j < (size); j++) {
				createButton(gridpane, i, j, size);
			}
		}
		canvasGrid = gridpane;
		body.setCenter(gridpane);
	    
	}
	
	private void destroyGrid() {
		body.getChildren().remove(canvasGrid);
	}

}
