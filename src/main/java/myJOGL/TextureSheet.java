package myJOGL;

import com.jogamp.opengl.util.texture.Texture;

public class TextureSheet {
	
	public Texture texture;
	public int textureResID;
	public int frameNumberX, frameNumberY;
	
	private MyRectF tempRect = new MyRectF(); //‰ñ”g‚í‚ê‚é–‚ª‚ ‚é‚½‚ßƒƒ“ƒo‚É‚µ‚Ä‚¿‰ñ‚µ‚Ü‚·
	
	public TextureSheet() {}
	
	public TextureSheet(int texResID, int frameNumberX, int frameNumberY){

		set(texResID, frameNumberX, frameNumberY);
	}

	public void set(int texResID, int frameNumberX, int frameNumberY){
		
		texture =MyGLUtil.TextureManager.getTexture(texResID);
		
		this.textureResID = texResID;
		this.frameNumberX = frameNumberX;
		this.frameNumberY = frameNumberY;
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
	
	public void deleteTexture(){
		
		MyGLUtil.TextureManager.deleteTexture(textureResID);
	}
}
