package spaceDrawer;


import java.lang.reflect.Field;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import spaceDrawer.MenuUtil.BackGroundColor;
import spaceDrawer.MenuUtil.MenuCallback;
import spaceDrawer.MenuUtil.OnOrOff;

public class SceneUtil{
	
	public static MenuCallback menuCallback;
	
	public static void initStage(Stage stage){
		
		stage.setTitle("SpaceDrawer ver1.0");
		stage.setWidth(650);
		stage.setHeight(500);
		
		VBox root = new VBox();
		
		VBox box = new VBox();
		box.setPadding(new Insets(30));
		box.setSpacing(30);
		box.getChildren().addAll(genConsoleBox(), genTablePane());
		
		root.getChildren().addAll(MenuUtil.generateMenu(menuCallback), box);
		Scene scene = new Scene(root);
		stage.setScene(scene);

	}
	
	private void setMenuCallback() {
		
		menuCallback = new MenuCallback() {

			@Override
			public void saveImageFile() {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	public static TextField textPictureName = new TextField("No_title.png");
	public static Button makeButton = new Button("Make Space");
	
	private static Pane genConsoleBox(){
		
		Label label1= new Label("PictureName");
		
		textPictureName.setMaxWidth(250);
		textPictureName.setMinWidth(250);
		textPictureName.setEditable(false);
		makeButton.setPrefWidth(100);
		
		VBox box = new VBox();
		box.setSpacing(10);
		
		HBox box2 = new HBox();
		box2.setSpacing(30);
		box2.getChildren().addAll(label1);
		
		HBox box3 = new HBox();
		box3.setSpacing(10);
		box3.getChildren().addAll(
				textPictureName, 
				makeButton
				);
	
		box.getChildren().addAll(box2, box3);
		
		return box;
	}
	
	public enum DataColumn{
		
		Screen_X("screenX","pix"),
		Screen_Y("screenY","pix"),
		Fovy("fovy","degree"),
		MaxDistance("farthestDistance","world_scale"),
		
		MiddleStarNumber("currentStarsNumber",""),     
		NearestDistance_MS("distancePercentageNearest","%"),
		FarthestDistance_MS("distancePercentageFarthest","%"),
		TexEnabledDistance("distancePercentegeTextureEnabled","%"),
		
		SmallStarNumber("backGroundPointStars",""),
		
		NebulaNumber("currentNebulaeNumber",""),
		NearestDistance_NB("distanceNebulaNearest","%");
		
		public String fieldName;
		public String unit;
		
		DataColumn(String fieldName, String unit){
			this.fieldName = fieldName;
			this.unit = unit;
		}
	}
	
	public static ListView<String> valueListView = new ListView<>();
	
	private static Pane genTablePane(){
		
		DataContainer dataContainer = new DataContainer();
		ListView<String> nameListView = new ListView<>();
		
		HBox pane = new HBox();
		
		nameListView.setPrefWidth(200);
		nameListView.setPrefHeight(300);
		valueListView.setPrefWidth(200);
		valueListView.setPrefHeight(300);
		
		valueListView.setEditable(true);
		
		String nameColumn[] = new String[DataColumn.values().length];
		
		for(DataColumn e: DataColumn.values()){
			
			nameColumn[e.ordinal()] = e.toString();	
		}
		
		nameListView.getItems().addAll(nameColumn);
	
		pane.getChildren().addAll(nameListView, valueListView);
		
		return pane;
	}
	
	public static void setListValue(DataContainer dataContainer) {
		
		String valueColumn[] = new String[DataColumn.values().length];
		
		for(DataColumn e: DataColumn.values()){
		
			valueColumn[e.ordinal()] = getReflectedFieldAsString(dataContainer,e.fieldName) +" "+ e.unit;
					
		}
		valueListView.getItems().addAll(valueColumn);
	}
	
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
