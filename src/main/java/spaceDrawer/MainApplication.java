package spaceDrawer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import javafx.application.Application;
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
	private final int fovy = 90;	//画面縦方向のカメラの画角
	private MyGLWinWrap winWrap;
	private Drawer drawer;
	private DataContainer dataContainer;

	@Override
	public void start(Stage stage) throws Exception {
		
		SceneUtil.initStage(stage);
		stage.show();
		
		SceneUtil.makeButton.setOnAction(event->updateSpace());
		dataContainer = new DataContainer();
		dataContainer.screenX = screenX;
		dataContainer.screenY = screenY;
		dataContainer.fovy = fovy;
		SceneUtil.setListValue(dataContainer);
		SceneUtil.valueListView.setOnMouseClicked(event->onClickedList(event));
		
		drawer = new Drawer();
		
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
	
	private void onClickedList(MouseEvent event) {
		
	}
}
