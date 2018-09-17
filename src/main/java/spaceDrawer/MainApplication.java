

package spaceDrawer;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import javafx.application.Application;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.stage.Stage;
import javafx.stage.Window;
import myJOGL_v2.MyGLUtil;
import myJOGL_v2.MyGLWinWrap;
import myJOGL_v2.MyGLWinWrap.MyRenderable;
import spaceDrawer.SceneUtil.FieldNameColumn;

public class MainApplication extends Application{
	
	public static void main(String[] args){
		
		Application.launch(args);
	}
	
	private PictureDialog pictureDialog;
	private MyGLWinWrap winWrap;
	private Drawer drawer;
	private DataContainer dataContainer;
	private boolean screenSaveSwitch = false;

	@Override
	public void start(Stage stage) throws Exception {
		
		Window wnd = stage;
		pictureDialog = new PictureDialog(wnd);
		
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
		
		SceneUtil.setMenuCallback(() -> {screenSaveSwitch = true; invalidate();});

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
				
				drawer.draw(gl2, dataContainer);
				
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
		
		int w = dataContainer.screenX;
		int h = dataContainer.screenY;
		
		int bufferSize = w * h * 4;
		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
		
		gl2.glReadBuffer(GL2.GL_FRONT);
		gl2.glReadPixels(
				0, 0, dataContainer.screenX, dataContainer.screenY, 
				GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, byteBuffer
				);
		
		WritableImage img = new WritableImage(w, h);
		PixelWriter writer = img.getPixelWriter();
		WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
		writer.setPixels(0, 0, w, h, format, byteBuffer , w * 4);
		
		PixelReader reader = img.getPixelReader();
		WritableImage revImg = new WritableImage(w, h);
		writer = revImg.getPixelWriter();
		for(int y=0; y<h; y++) {
			writer.setPixels(0, y, w, 1, reader, 0, h-y-1);
		}
		
		pictureDialog.show();
		pictureDialog.drawScreen(revImg);
		pictureDialog.saveImage();
		pictureDialog.hide();
		
		screenSaveSwitch = false;
	}
}
