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
	
	public static final float radian = 3.141592653589f/180;

	private GLU glu;
	private StarMaker starMaker;
	
	public int screenX, screenY;
	public float fovx, fovy;
	
	public ArrayList<MyGLTexSheet> texSheets;
	
	public Drawer(){
		
		glu = new GLU();
		texSheets = new ArrayList<>();
	}
	
	public void updateSpace(DataContainer dataContainer){
		
		this.screenX = dataContainer.screenX;
		this.screenY = dataContainer.screenY;
		this.fovy = dataContainer.fovy;
		
		float aspectratio = (float)screenX / screenY;
		this.fovx = getFovx(aspectratio);
		
		if(starMaker == null) starMaker = new StarMaker(this);
		starMaker.makeAllData(dataContainer);
	}
	
	public void init(GL2 gl2, DataContainer dataContainer) {
		
		updateSpace(dataContainer);
		
		loadTexture(gl2);
		
		setupStandard3DProcess(gl2);	
	}
	
	private float getFovx(float aspectratio){
		
		double tanHalfFovx = aspectratio * Math.tan(fovy/2*radian);
		
		return (float)Math.atan(tanHalfFovx) *2 / radian;
	}
	
	public enum TextureFile{
		
		dosei(0,"dosei.png"),
		kaiousei(1,"kaiousei.png"),
		moon(2,"moon.png"),
		suisei(3,"suisei.png"),
		nebula1(4,"nebula3.png"),
		nebula2(5,"nebula4.png"),
		nebula3(6,"nebula10.png"),
		nebula4(7,"nebula11.png"),
		nebula5(8,"nebula12.png");
		
		public static int maxPlanetIndex = 3;
		public static int firstNebulaIndex = 4;
		public static int maxNebulaIndex = 8;
		
		public int resID;
		public String fileName;
	
		private TextureFile(int id,String fn){
			
			resID = id;
			fileName = fn;
		}
	}
	
	public void loadTexture(GL2 gl){
		
		MyGLUtil.enableDefaultBlend();
		MyGLUtil.changeTexColor(null);
		
		String dir = ".\\texture\\";
		
		for(TextureFile e: TextureFile.values()) {
			
			MyGLTexSheet newSheet = new MyGLTexSheet(e.resID, 1,1);
			newSheet.setTexture(dir + e.fileName);
			texSheets.add(newSheet);
		}
	}
	
	public void draw(GL2 gl2) {
		
		gl2.glViewport(0, 0, screenX, screenY);
		
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		
		float aspectratio = (float)screenX / screenY;
		glu.gluPerspective(fovy, aspectratio, 0.1f, 1000);
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
		setLighting(gl2); 	//ライティングはワールド座標系で行われるので視野変換の設定前に設定します
							//ライティング設定はモデル変換の影響を受けますが影響を与えはしません
		
		My3DVectorF cameraPoint = new My3DVectorF(0,0,0);
		My3DVectorF lookPoint = new My3DVectorF(0,0,-1);
		
		setView(gl2, cameraPoint, lookPoint);
		
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT|GL2.GL_DEPTH_BUFFER_BIT);
        gl2.glClearColor(0f, 0f, 0f, 0f);
       
		starMaker.drawPointStars(gl2);
		testDraw(gl2);	
		starMaker.drawStars(gl2);	
		starMaker.drawNebulae(gl2);
		
	}
	
	private void testDraw(GL2 gl2) {
		
		gl2.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		
		gl2.glColor4f(1f, 1f, 1f, 0.5f);
		
		gl2.glPushMatrix();
		rotateAndTranslate(gl2,0,0,0,0,0,-10);
		Icosahedron.draw(gl2, 2);
		gl2.glPopMatrix();
		
		gl2.glPopAttrib();
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
	
	public void setLighting(GL2 gl2) {
		
		float[] ambient = {0.5f,0.5f,0.5f,1};
		float[] diffuse = {0.8f,0.8f,0.8f,0};
		float[] specular = {1f,1f,1f,1};
		float[] position = {10,0,0,1};
		
		FloatBuffer ambientLight0,diffuseLight0,specularLight0,positionLight0;
		
		//ambientLight0 = FloatBuffer.wrap(ambient);
		diffuseLight0 = FloatBuffer.wrap(diffuse);
		//specularLight0 = FloatBuffer.wrap(specular);
		positionLight0 = FloatBuffer.wrap(position);
		
		//gl2.glLightfv(GL2.GL_LIGHT0,GL2.GL_AMBIENT,ambientLight0);
		gl2.glLightfv(GL2.GL_LIGHT0,GL2.GL_DIFFUSE,diffuseLight0);
		//gl2.glLightfv(GL2.GL_LIGHT0,GL2.GL_SPECULAR,specularLight0);
		gl2.glLightfv(GL2.GL_LIGHT0,GL2.GL_POSITION,positionLight0);
	
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glEnable(GL2.GL_LIGHT0);
	}
}
