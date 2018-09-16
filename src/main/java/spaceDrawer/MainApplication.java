

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

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}
	
	private final int screenX = 320;
	private final int screenY = 480;
	private final int fovy = 90;	//‰æ–Êc•ûŒü‚ÌƒJƒƒ‰‚Ì‰æŠp
	private MyGLWinWrap winWrap;
	private Drawer drawer;
	private DataContainer dataContainer;

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
		
		dataContainer.screenX = screenX;
		dataContainer.screenY = screenY;
		dataContainer.fovy = fovy;
		SceneUtil.updateListValue(dataContainer);
		SceneUtil.valueListView.setOnEditCommit(event -> onEditCommitedList(event));
		
		stage.show();
	}
	
	private void setupGLWinWrap(){
		
		winWrap = new MyGLWinWrap(GLProfile.GL2ES1);
		winWrap.setRenderer(new MyRenderable(){
			
			@Override
			public void init(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				MyGLUtil.setGL(gl2);
				
				drawer.init(gl2, screenX, screenY, fovy);
			}

			@Override
			public void render(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				
				drawer.draw(gl2);
			}
			
		});
		
		winWrap.getWindow().setSize(screenX, screenY);
		winWrap.getWindow().setVisible(true);
	}

	public void invalidate(){
		
		winWrap.getWindow().display();
	}
	
	private void updateSpace() {
		
		drawer.updateSpace();
		invalidate();
	}
	
	private void onEditCommitedList(EditEvent event) {
		
		SceneUtil.updateListValue(dataContainer);
	}
}
