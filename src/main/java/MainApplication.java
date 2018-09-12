import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLProfile;

import javafx.application.Application;
import javafx.stage.Stage;
import myJOGL.MyGLWinWrap;
import myJOGL.MyGLWinWrap.MyRenderable;

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}
	
	private MyGLWinWrap winWrap;

	@Override
	public void start(Stage stage) throws Exception {
		
		SceneUtil.initStage(stage);
		stage.show();
		
		winWrap = new MyGLWinWrap(GLProfile.GL2ES1);
		winWrap.setRenderer(new MyRenderable(){

			@Override
			public void init(GL gl) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void render(GL gl) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		winWrap.getWindow().setSize(500, 500);
		winWrap.getWindow().setVisible(true);
	}

	
	public void invalidate(){
		
		winWrap.getWindow().display();
	}
}
