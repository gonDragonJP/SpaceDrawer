package spaceDrawer;


import java.lang.reflect.Field;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import spaceDrawer.MenuUtil.BackGroundColor;
import spaceDrawer.MenuUtil.MenuCallback;
import spaceDrawer.MenuUtil.OnOrOff;

public class SceneUtil{
	
	private static DataContainer dataContainer;
	
	public static void initStage(Stage stage, DataContainer dc){
		
		dataContainer = dc;
		
		stage.setTitle("SpaceDrawer ver1.0");
		stage.setWidth(650);
		stage.setHeight(500);
		
		VBox root = new VBox();
		
		VBox box = new VBox();
		box.setPadding(new Insets(30));
		box.setSpacing(30);
		box.getChildren().addAll(genConsoleBox(), genTablePane());
		
		root.getChildren().addAll(MenuUtil.generateMenu(), box);
		Scene scene = new Scene(root);
		stage.setScene(scene);

	}
	
	public static void setMenuCallback(MenuCallback callable) {
	
		MenuUtil.menuCallback = callable;
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
	
	public enum FieldNameColumn{
		
		IsEnabledTestPlanet("isEnabledTestPlanet",""),
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
		NearestDistance_NB("distanceNebulaNearest","%"),
		TextureTransparency("textureTransparency","%");
		
		public String fieldName;
		public String unit;
		
		FieldNameColumn(String fieldName, String unit){
			this.fieldName = fieldName;
			this.unit = unit;
		}
	}
	
	public static TableView<FieldNameColumn> tableView = new TableView<>(); 
	public static TableColumn <FieldNameColumn, String>[] columns= new TableColumn[2];
	
	private static Pane genTablePane(){
		
		Pane pane = new Pane();
		
		tableView.setPrefWidth(420);
		tableView.setPrefHeight(300);
		
		columns[0] = new TableColumn<>("Variable Name");
		columns[0].setPrefWidth(200);
		columns[0].setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));
		
		columns[1] = new TableColumn<>("Value");
		columns[1].setPrefWidth(200);
		columns[1].setCellValueFactory(param ->{
			
				String valText = getReflectedFieldAsString(dataContainer, param.getValue().fieldName);
				valText += param.getValue().unit;
				
				return new SimpleStringProperty(valText);
			});
		columns[1].setCellFactory(TextFieldTableCell.forTableColumn());
		columns[1].setOnEditCommit(event -> onEditCommited(event));
		
		tableView.getColumns().addAll(columns);
		tableView.setEditable(true);
		
		ObservableList<FieldNameColumn> tableData = FXCollections.observableArrayList();
		tableData.setAll(FieldNameColumn.values());
		tableView.itemsProperty().setValue(tableData);
		
		pane.getChildren().add(tableView);
		
		return pane;
	}
	
	private static void onEditCommited(CellEditEvent event) {
		
		String newText = (String)event.getNewValue();
		FieldNameColumn data = (FieldNameColumn)event.getRowValue();
		
		SceneUtil.setTextDataToReflectedField(dataContainer, data.fieldName, newText);
		tableView.refresh();
	}

	public static void updateListValue(DataContainer dataContainer) {
		
		String valueColumn[] = new String[FieldNameColumn.values().length];
		
		for(FieldNameColumn e: FieldNameColumn.values()){
		
			valueColumn[e.ordinal()] = getReflectedFieldAsString(dataContainer,e.fieldName) +" "+ e.unit;
					
		}
		//valueListView.getItems().clear();
		//valueListView.getItems().addAll(valueColumn);
	}
	
	private static ArrayList<ScrollBar> getScrollBarsFromView(ListView listView) {
		
		ArrayList<ScrollBar> scrollBars = new ArrayList<>();
		
		for(Node node : listView.lookupAll(".scroll-bar")) {
			
			if(node instanceof ScrollBar) {
				
				scrollBars.add((ScrollBar)node);
			}
		}		
		return scrollBars;
	}
	
	public static void synchronizeScrollBars(ScrollEvent event) {
		
		double movement = event.getDeltaY();
		
		
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
	
	public static boolean setTextDataToReflectedField
		(Object object, String fieldName, String text){
		
		Class<?> clazz = object.getClass();
		
		try{
		
			Field field = clazz.getDeclaredField(fieldName);
			
			Class<?> type = field.getType();
			
			switch(type.getName()){
			
			case "java.lang.String":
				field.set(object, text);
				break;

			case "int":
				field.set(object, Integer.valueOf(text));
				break;
				
			case "boolean": 
				field.set(object, Boolean.valueOf(text));
				break;
				
			case "float":
				field.set(object, Float.valueOf(text));
				break;	
				
			case "double":
				field.set(object, Double.valueOf(text));
				break;
				
			default:
				return false;
			}
			
		}catch(Exception e){
		
			return false;
		}
		return true;
	}

}
