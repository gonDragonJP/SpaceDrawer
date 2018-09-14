package spaceDrawer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import myJOGL_v2.My3DVectorF;
import myJOGL_v2.MyGLTexSheet;
import myJOGL_v2.MyGLUtil;
import myJOGL_v2.MyPointF;

public class Drawer {

	private GLU glu = new GLU();
	private StarMaker starMaker;
	
	public void init(GL2 gl2, int screenX, int screenY, float fovy) {
		
		MyGLUtil.setGL(gl2);
		
		starMaker = new StarMaker(this);
		
		//setupStandard3DProcess(gl2);
		
		gl2.glViewport(0, 0, screenX, screenY);
		
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		
		double aspectratio = (double)screenX / screenY;
		glu.gluPerspective(fovy, aspectratio, 0.1f, 1000);
		
		My3DVectorF cameraPoint = new My3DVectorF(0,0,0);
		My3DVectorF lookPoint = new My3DVectorF(0,0,-1);
		
		setView(gl2, cameraPoint, lookPoint);
		
		loadTexture(gl2);
	}
	
	private enum TextureFile{
		
		dosei(0,"dosei.bmp"),
		kaiousei(1,"kaiousei.bmp"),
		moon(2,"moon.bmp"),
		suisei(3,"suisei.bmp"),
		nebula1(4,"nebula1.bmp"),
		nebula2(5,"nebula2.bmp");
		
		public int resID;
		public String fileName;
	
		private TextureFile(int id,String fn){
			
			resID = id;
			fileName = fn;
		}
	}
	
	public ArrayList<MyGLTexSheet> texSheets = new ArrayList<>();
	
	public void loadTexture(GL2 gl){
		
		MyGLUtil.enableDefaultBlend();
		MyGLUtil.changeTexColor(null);
		
		String dir ="texture\\";
		
		for(TextureFile e: TextureFile.values()) {
			
			MyGLTexSheet newSheet = new MyGLTexSheet(e.resID, 1,1);
			newSheet.setTexture(dir + e.fileName);
		}
	}
	
	public void draw(GL2 gl2) {
		
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl2.glClearColor(0f, 0f, 0f, 0f);
        
        gl2.glColor3f(1f, 1f, 1f);
		
		MyPointF start = new MyPointF(0,0);
		MyPointF end = new MyPointF(100,100);
		MyGLUtil.drawLine(start, end);
		
		rotateAndTranslate(gl2,0,0,0,0,0,-10);
		Icosahedron.draw(gl2, 1);
		
		starMaker.drawPointStars(gl2);
		
	}
	
	//　何も設定しないと視点は原点、視方向はｚ軸の負方向です。
	// 視野変換（視点と視線の変換）はモデル行列に影響します。
	// 投影変換は視野方向からどれだけの視体積を切り出すかを設定し、投影行列に影響します。
	
	// 視野変換
	// モデル全体を回転平行移動させるのと同じ概念で視野を変換します。
	// 結果、変換後の視点は原点、視線はｚ軸負方向のままだと考える事も出来ます。
	private void setView(GL2 gl2, My3DVectorF cameraPoint, My3DVectorF lookPoint)
	{
		if( (lookPoint.x == cameraPoint.x) && (lookPoint.z == cameraPoint.z) ){
			// ジンバルロックなので行列変換せず
			return;	
		}
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);					// 行列変換をモデル座標に適用

		My3DVectorF v1, v2, v3, v4;

		v1 = new My3DVectorF(lookPoint);
		v1.minus(cameraPoint);
		v2 = new My3DVectorF(0,1,0);						// 常に重力方向が画面中心線にあるカメラ
		v3 = My3DVectorF.crossProduct(v1, v2);
		v4 = My3DVectorF.crossProduct(v3, v1);
		v4.normalize();

		glu.gluLookAt(	cameraPoint.x, 	cameraPoint.y, 	cameraPoint.z,
				  		lookPoint.x,	lookPoint.y,	lookPoint.z,
				  		v4.x,			v4.y,			v4.z
		);
	}
	
	private void setupStandard3DProcess(GL2 gl2){
		
		setupPrimitiveAsembleProcess(gl2);
		setupPixelAsembleProcess(gl2);	
		setupRasterizeProcess(gl2, GL2.GL_SMOOTH);
		setupFragmentProcess(gl2);
	}

	// 幾何データに関する処理
	private void setupPrimitiveAsembleProcess(GL2 gl2)
	{
		gl2.glPolygonMode(GL2.GL_FRONT,GL2.GL_FILL);		// ポリゴン表面は塗りつぶし
		gl2.glPolygonMode(GL2.GL_BACK,GL2.GL_LINE);			// ポリゴン裏面は線のみで描画
		gl2.glFrontFace(GL2.GL_CCW);						// 反時計周りでの頂点定義を表面とする	
	}

	//　ピクセルデータ（テクスチャ等）に関する処理
	private void setupPixelAsembleProcess(GL2 gl2)
	{
		gl2.glEnable(GL2.GL_TEXTURE_2D);				// 二次元テクスチャ有効
	}

	//　幾何データとピクセルデータを合わせてフラグメント化する処理のこと
	private void setupRasterizeProcess(GL2 gl2, int mode)
	{
		gl2.glShadeModel(mode);							// シェーディングモード設定

		float[] a = new float[2];
		FloatBuffer ps = FloatBuffer.wrap(a, 0, 2);
		gl2.glGetFloatv(GL2.GL_LINE_WIDTH_RANGE,ps);	// 描画線の太さ範囲を取得
		gl2.glLineWidth(ps.get(0));						// 最も細い線（ps[0])を選択
	}

	// フラグメントを加工してバッファに格納する処理のこと
	private void setupFragmentProcess(GL2 gl2){
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);				// デプステスト有効
	}
	
	public void rotateAndTranslate
		(GL2 gl2, float heading, float pitch, float bank, float tx, float ty, float tz){
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);				// 行列変換をモデル座標に適用
		
		gl2.glTranslatef(tx,ty,tz);						// 平行移動(回転移動の後なので先にかける）
		
		gl2.glRotatef(bank,0,0,1);
		gl2.glRotatef(pitch,1,0,0);						// 回転(EulerAngles-degree)
		gl2.glRotatef(heading,0,1,0);
	}
}
