package spaceDrawer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import javafx.application.Application;
import javafx.stage.Stage;
import myJOGL_v2.MyGLWinWrap;
import myJOGL_v2.MyGLWinWrap.MyRenderable;

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}
	
	private final int screenX = 320;
	private final int screenY = 480;
	private final int fovy = 45;	//âÊñ ècï˚å¸ÇÃÉJÉÅÉâÇÃâÊäp
	private MyGLWinWrap winWrap;
	private Drawer drawer = new Drawer();

	@Override
	public void start(Stage stage) throws Exception {
		
		SceneUtil.initStage(stage);
		stage.show();
		
		winWrap = new MyGLWinWrap(GLProfile.GL2ES1);
		winWrap.setRenderer(new MyRenderable(){

			@Override
			public void init(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				
				drawer.init(gl2, screenX, screenY, fovy);
			}

			@Override
			public void render(GL gl) {
				
				GL2 gl2 = gl.getGL2();
				gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
		        gl2.glClearColor(0f, 0f, 0f, 0f);
		        
		        
				drawer.draw(gl2);
			}
			
		});
		
		winWrap.getWindow().setSize(screenX, screenY);
		winWrap.getWindow().setVisible(true);
	}

	
	public void invalidate(){
		
		winWrap.getWindow().display();
	}
}
