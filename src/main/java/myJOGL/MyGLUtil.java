package myJOGL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.jogamp.nativewindow.util.Point;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

import javafx.scene.paint.Color;

public class MyGLUtil {
	
	public static GL2 gl;
	
	public static void setGL(GL arg){
		
		gl = arg.getGL2();
	}
	
	public static final FloatBuffer makeFloatBuffer(float[] arr){
		
		ByteBuffer bb  = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	public static Point setViewPortWithAspectRatio
				(int width, int height, double aspect){
		
		//表示可能な画面縦横を受け、指定された比率で画面中央に表示されるようにビューの描画位置を調整します
		
		int dstWidth, dstHeight;
		
		if(width < height * aspect){
			
			dstWidth = width;
			dstHeight = (int)(width / aspect);
		}
		else{
			
			dstWidth = (int)(height * aspect);
			dstHeight = height;
		}
		
		int offsetX = (width - dstWidth) / 2;
		int offsetY = (height - dstHeight) / 2;
		
		gl.glViewport(offsetX, offsetY, dstWidth, dstHeight);
		// 画面左下を(0,0)としてオフセット位置と幅高さを設定
		
		return new Point(dstWidth, dstHeight);
	}
	
	public static void setColor(float red, float green, float blue, float alpha){
		
		gl.glColor4f(red, green, blue, alpha);
	}
	
	public static void setColor(int color){
		
		float red   = ((color >> 16)&0xFF)/ 255f;
		float green = ((color >> 8)&0xFF) / 255f;
		float blue  = (color &0xFF)  / 255f;
		float alpha = (color >>> 24) / 255f;
		
		setColor(red, green, blue, alpha);
	}
	
	public static void drawLine(MyPointF start, MyPointF end){
		
		float[] vertices = {
				start.x,	start.y,
				end.x,		end.y
		};
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL2.GL_LINES, 0, 2);
	}
	
	public static void drawRectAngle(MyRectF rect){
	
		float[] vertices = {
				rect.left,	rect.bottom,
				rect.left,	rect.top,
				rect.right,	rect.bottom,
				rect.right,	rect.top
		};
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public static void drawStrokeCircle
		(MyPointF center, float radius, int divide){
		
		float[] vertices = getCircleVertices(center, radius, divide);
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);	
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, polygonVertices);
		
		gl.glDrawArrays(GL2.GL_LINE_LOOP, 0, divide);
	}
	
	public static void drawFillCircle
		(MyPointF center, float radius, int divide){
	
	float[] edges = getCircleVertices(center, radius, divide);
	float[] vertices = new float [(divide - 2) * 3 * 2];
	
	for(int i=0; i<divide-2; i++){
		
		vertices[i*6  ]= edges[0];			vertices[i*6+1]= edges[1];
		vertices[i*6+2]= edges[(i+1)*2];	vertices[i*6+3]= edges[(i+1)*2+1];
		vertices[i*6+4]= edges[(i+2)*2];	vertices[i*6+5]= edges[(i+2)*2+1];	
	};
	
	FloatBuffer polygonVertices = makeFloatBuffer(vertices);
	
	gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);	
	gl.glVertexPointer(2, GL2.GL_FLOAT, 0, polygonVertices);
	
	gl.glDrawArrays(GL2.GL_TRIANGLES, 0, (divide-2)*3);
}
	
	private static float[] getCircleVertices
			(MyPointF center, float radius, int divide){
		
		double dRad = 2 * Math.PI / divide;
		
		float[] result = new float[divide * 2];
		
		for(int i=0; i<divide; i++){
			
			result[i*2  ] = (float)(center.x + radius * Math.cos(i * dRad));
			result[i*2+1] = (float)(center.y + radius * Math.sin(i * dRad));
		}
		return result;
	}
	
	
	public static final boolean loadTexture(String fileName, int resID){
		
		Texture texture = null;
		
		InputStream resourceStream = MyGLUtil.class.getResourceAsStream(fileName);
		try {
			texture = TextureIO.newTexture(resourceStream, false, TextureIO.PNG);
		
		} catch (Exception e) {
			
			e.printStackTrace();
			if(texture !=null) texture.destroy(gl);
			return false;
		}
		
		TextureManager.addTexture(resID, texture);
		
		return true;
	}
	
	public static void enableDefaultBlend(){
		
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static class TextureManager{
		
		private static Map<Integer, Texture> textures
			= new Hashtable<Integer, Texture>();
		
		public static void addTexture(int resID, Texture texture){
			textures.put(resID, texture);
		}
		
		public static Texture getTexture(int resID){
			return textures.get(resID);
		}
		
		public static void deleteTexture(int resID){
			
			Texture texture = textures.get(resID);
			texture.destroy(gl);
			textures.remove(resID);
		}
		
		public static void deleteAll(){
			List<Integer> keys = new ArrayList<Integer>(textures.keySet());
			for(Integer key : keys){
				deleteTexture(key);
			}
		}
	}
	
	public static float[] textureSTCoords ={
		0.0f,	0.0f,
		0.0f,	1.0f,
		1.0f,	0.0f,
		1.0f,	1.0f
	};
	
	public static void setTextureSTCoords(MyRectF rect){ 
		//uv座標を受け取り　頂点に一致したST座標として定義します
		//頂点は右手座標系(左下が(0,0))の上下左右になるようRectの値を一致させて下さい
		
		textureSTCoords[0]=rect.left;	textureSTCoords[1]=rect.top;
		textureSTCoords[2]=rect.left;	textureSTCoords[3]=rect.bottom;
		textureSTCoords[4]=rect.right;	textureSTCoords[5]=rect.top;
		textureSTCoords[6]=rect.right;	textureSTCoords[7]=rect.bottom;
	}
	
	public static float[] vertices = new float [8];
	static float vLeft, vRight, vTop, vBottom;
	
	public static void drawTexture(MyPointF center, MyPointF size, int resID){
		
		int texObject = TextureManager.getTexture(resID).getTextureObject();
		
		vLeft = center.x - size.x / 2;
		vTop = center.y + size.y / 2;
		vRight = vLeft + size.x;
		vBottom = vTop - size.y;
		
		vertices[0] = vLeft;	vertices[1] = vTop;
		vertices[2] = vLeft;	vertices[3] = vBottom;
		vertices[4] = vRight;	vertices[5] = vTop;
		vertices[6] = vRight;	vertices[7] = vBottom;
		
		FloatBuffer polygonVertices = makeFloatBuffer(vertices);
		FloatBuffer texCoords = makeFloatBuffer(textureSTCoords);
		
		gl.glColor4f(0f, 0f, 0f, 1.0f);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texObject);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(2, GL2.GL_FLOAT, 0, polygonVertices);
		gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texCoords);
		
		gl.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	private static TextureSheet fontSheet = new TextureSheet();
	private static int asciiOffset;
	
	private static MyRectF fontRect = new MyRectF();
	private static MyPointF fontCenter = new MyPointF(), fontSize = new MyPointF();
	
	
	public static void setupFont
			(String fileName, int resID, int nx, int ny, int offset){
		
		if(!loadTexture(fileName, resID)) return;
		
		fontSheet.set(resID, nx, ny);
		asciiOffset = offset;
	}
	
	public static void drawText(MyRectF position, String text){
		
		int textLength = text.length();
		if(textLength==0) return;
		
		float drawChrSizeX = position.width() / textLength;
		float drawChrSizeY = position.height();
		float drawCenterY = position.centerY();
		
		for(int i=0; i<textLength; i++){
			
			float drawCenterX = position.left + drawChrSizeX*(i+0.5f);
			int code = text.codePointAt(i) - asciiOffset;
			
			fontRect.copy(fontSheet.getFrameUVcoords(code));
			
			convertUVForRightHandCoords(fontRect);
			flipTexCoordsVertically(fontRect);
			
			setTextureSTCoords(fontRect);
			
			fontCenter.set(drawCenterX, drawCenterY);
			fontSize.set(drawChrSizeX, drawChrSizeY);
			drawTexture(fontCenter, fontSize, fontSheet.textureResID);
		}
	}
	
	public static MyRectF convertUVForRightHandCoords(MyRectF rect){
		//　通常テクスチャ座標として使われるuv座標は左上が(0,0)ですが
		// Texture型では左下が(0,0)となる右手系の座標になっているため変換します
		
		rect.top    = 1- rect.top;
		rect.bottom = 1- rect.bottom;

		return rect;
	}
	
	public static void flipTexCoordsVertically(MyRectF rect){
		// テクスチャを上下逆に使いたい時に使います
		
		float top = rect.top;
		rect.top = rect.bottom;
		rect.bottom = top;
	}
	
	static FloatBuffer colorMatrix = MyGLUtil.makeFloatBuffer(new float[4]);
	
	public static void changeTexColor(float[] color){
		
		if(color == null){
			gl.glTexEnvf
			(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			return;
		}
		
		colorMatrix.put(color);
		colorMatrix.position(0);
		
		gl.glTexEnvfv
			(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, colorMatrix);
		gl.glTexEnvf
			(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
	}
}
