package spaceDrawer;


import java.lang.reflect.Field;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneUtil {
	
	public static void initStage(Stage stage){
		
		stage.setTitle("SpaceDrawer ver1.0");
		stage.setWidth(650);
		stage.setHeight(500);
		
		VBox root = new VBox();
		
		VBox box = new VBox();
		box.setPadding(new Insets(30));
		box.setSpacing(30);
		//box.getChildren().addAll(genPicDataBox(), genTablePane());
		
		
		//root.getChildren().addAll(MenuUtil_MainApp.generateMenu(mainApp), box);
		Scene scene = new Scene(root);
		stage.setScene(scene);

	}
	/*
	private static Pane genPicDataBox(){
		
		Label label0= new Label("TexID");
		Label label1= new Label("PictureName (Size)");
		Label label2= new Label("GridSizeX");
		Label label3= new Label("GridSizeY");
		
		mainApp.textTextureID.setPrefWidth(50);
		mainApp.textPictureName.setMaxWidth(250);
		mainApp.textPictureName.setMinWidth(250);
		mainApp.textPictureName.setEditable(false);
		mainApp.textGridSizeX.setMaxWidth(50);
		mainApp.textGridSizeY.setMaxWidth(50);
		mainApp.storeButton.setPrefWidth(100);
		
		VBox box = new VBox();
		box.setSpacing(10);
		
		HBox box2 = new HBox();
		box2.setSpacing(30);
		box2.getChildren().addAll(label0, label1, label2, label3);
		
		HBox box3 = new HBox();
		box3.setSpacing(10);
		box3.getChildren().addAll(
				mainApp.textTextureID,
				mainApp.textPictureName, 
				mainApp.textGridSizeX, 
				mainApp.textGridSizeY,
				mainApp.storeButton
				);
		
		mainApp.textTextureID.setOnAction(event->mainApp.setTexID());
		mainApp.textGridSizeX.setOnAction(event->mainApp.setGridSize());
		mainApp.textGridSizeY.setOnAction(event->mainApp.setGridSize());
		
		mainApp.storeButton.setOnAction(event->mainApp.storeToDB());
	
		box.getChildren().addAll(box2, box3);
		
		return box;
	}
	
	public enum TexDataColumn{
		
		TexID(40,"textureID"),
		Sheet_PictureName(200,"pictureName"),
		GridX(40,"gridSizeX"),
		GridY(40,"gridSizeY");
		
		public int width;
		public String fieldName;
		
		TexDataColumn(int width, String fieldName){
			
			this.width = width;
			this.fieldName = fieldName;
		}
	}
	
	private static Pane genTablePane(){
		
		TableModule  tableModule = mainApp.getTableModule();
		TableView<TextureData> tableView = tableModule.getView();
		Pane pane = new Pane();
		
		tableView.setPrefWidth(550);
		tableView.setPrefHeight(300);
		
		TableColumn<TextureData, String>[] columns 
			= new TableColumn[TexDataColumn.values().length];
		
		for(TexDataColumn e: TexDataColumn.values()){
			
			columns[e.ordinal()] = new TableColumn<TextureData, String>(e.toString());
			columns[e.ordinal()].setPrefWidth(e.width);
		
			columns[e.ordinal()].setCellValueFactory(
					param -> {
						String colString = 
								getReflectedFieldAsString(param.getValue(),e.fieldName);
						return new SimpleStringProperty(colString);
					}
			);
		}
		
		tableView.getColumns().addAll(columns);
		tableView.setOnMouseClicked(event -> tableModule.onMouseClicked(event));
		
		pane.getChildren().add(tableView);
		
		return pane;
	}*/
	
	private static String getReflectedFieldAsString(Object object, String fieldName){
		
		Class<?> clazz = object.getClass();
		
		try{
		
			Field field = clazz.getDeclaredField(fieldName);
			
			return field.get(object).toString();
		
		}catch(ReflectiveOperationException e){
		
		}
		return "null";
	}
}
