package spaceDrawer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PictureDialog extends Stage{
	
	private Canvas canvas = new Canvas();
	
	private int canvasSizeX = 320;
	private int canvasSizeY = 480;
	
	private Image image;
	
	public PictureDialog(Window parentWnd){
		
		this.initOwner(parentWnd);
		this.setTitle("‰æ‘œ•Û‘¶");
		
		setScene();
		fitDialogSize(canvasSizeX, canvasSizeY);
	}
	
	
	private void setScene(){
	
		VBox root = new VBox();
		root.getChildren().addAll(canvas);
		Scene scene = new Scene(root);
		this.setScene(scene);
	}
	
	
	private void fitDialogSize(int width, int height){
		
		canvasSizeX = width;
		canvasSizeY = height;
		
		canvas.setWidth(width);
		canvas.setHeight(height);
		
		this.sizeToScene();
	}
	
	public void drawScreen(Image image){
		
		this.image = image;
		
		fitDialogSize((int)image.getWidth(), (int)image.getHeight());
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		fillMonotone(Color.GRAY);
		gc.drawImage(image, 0, 0);
	}
	
	private void fillMonotone(Color color){
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(color);
		gc.fillRect(0, 0, canvasSizeX, canvasSizeY);
	}
	
	public void saveImage() {
		
		FileChooser fc = new FileChooser();
		
		fc.setTitle("‰æ‘œƒtƒ@ƒCƒ‹•Û‘¶");
		
		File file = fc.showSaveDialog(this);
		
		if(file == null) return;
		
		String suffix;
		String fileName = file.getName();
		String[] nameParts = fileName.split(".");

		if(nameParts.length == 0){
			
			file = new File(file.getPath()+".png");
			suffix = "png";
		}
		else{
			
			suffix = nameParts[1];
		}
		
		try {
			
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), suffix, file);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
}
