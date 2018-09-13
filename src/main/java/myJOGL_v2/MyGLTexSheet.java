package myJOGL_v2;

import java.io.InputStream;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class MyGLTexSheet {
	
	public Texture texture;
	public int resID;			//リソースとして自分が設定するID
	public int glTexID;			//GLインターフェイスにバインドされたID
	public int frameNumberX, frameNumberY;
	
	private MyRectF tempRect = new MyRectF(); //回数使われる事があるためメンバにして持ち回します
	
	private MyGLTexSheet() {}
	
	public MyGLTexSheet(int texResID, int frameNumberX, int frameNumberY){

		this.resID = texResID;
		this.frameNumberX = frameNumberX;
		this.frameNumberY = frameNumberY;
	}

	public void setTexture(String fileName){
		
		texture =MyGLUtil.loadTexture(fileName, resID);
	}
	
	public final MyRectF getFrameUVcoords(int frameNumber){
		
		float texFrameSizeX = 1.0f / frameNumberX;
		float texFrameSizeY = 1.0f / frameNumberY;
		
		tempRect.left = (frameNumber % frameNumberX) * texFrameSizeX;
		tempRect.top  = (frameNumber / frameNumberX) * texFrameSizeY;
		tempRect.right  = tempRect.left + texFrameSizeX;
		tempRect.bottom = tempRect.top + texFrameSizeY;
		
		return tempRect;
	}
	
	public void deleteTexture(GL2 gl2){
		
		texture.destroy(gl2);
	}
}
