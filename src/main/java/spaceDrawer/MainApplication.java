

package spaceDrawer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import javafx.application.Application;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import myJOGL_v2.MyGLUtil;
import myJOGL_v2.MyGLWinWrap;
import myJOGL_v2.MyGLWinWrap.MyRenderable;
import spaceDrawer.MenuUtil.MenuCallback;
import spaceDrawer.SceneUtil.FieldNameColumn;

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}
	
	private MyGLWinWrap winWrap;
	private Drawer drawer;
	private DataContainer dataContainer;
	private boolean screenSaveSwitch = false;

	@Override
	public void start(Stage stage) throws Exception {
		
		drawer = new Drawer();
		dataContainer = new DataContainer();
		
		setupStage(stage);
		setupGLWinWrap();
	}
	
	private void setupStage(Stage stage){
		
		SceneUtil.initStage(stage);
		
		SceneUtil.makeButton.setOnAction(event->updateSpace());	
		
		SceneUtil.updateListValue(dataContainer);
		SceneUtil.valueListView.setOnEditCommit(event -> onEditCommitedList(event));
		
		SceneUtil.setMenuCallback(() -> screenSaveSwitch = true);

		stage.show();
	}
	
	private void setupGLWinWrap(){
		
		winWrap = new MyGLWinWrap(GLProfile.GL2ES1);
		winWrap.setRenderer(new MyRenderable(){
			
			@Override
			public void init(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				MyGLUtil.setGL(gl2);
				
				drawer.init(gl2, dataContainer);
			}

			@Override
			public void render(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				
				drawer.draw(gl2);
				
				if(screenSaveSwitch) onScreenSave(gl2);
			}
			
		});
		
		winWrap.getWindow().setSize(dataContainer.screenX, dataContainer.screenY);
		winWrap.getWindow().setVisible(true);
	}

	public void invalidate(){
		
		winWrap.getWindow().display();
	}
	
	private void updateSpace() {
		
		drawer.updateSpace(dataContainer);
		winWrap.getWindow().setSize(dataContainer.screenX, dataContainer.screenY);
		invalidate();
	}
	
	private void onEditCommitedList(EditEvent event) {
		
		int listIndex = event.getIndex();
		String newText = (String)event.getNewValue();
		
		FieldNameColumn data = SceneUtil.nameListView.getItems().get(listIndex);
		SceneUtil.setTextDataToReflectedField(dataContainer, data.fieldName, newText);
		
		SceneUtil.updateListValue(dataContainer);
	}
	
	private void onScreenSave(GL2 gl2) {
		
		screenSaveSwitch = false;
	}
}
