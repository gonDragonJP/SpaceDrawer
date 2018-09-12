import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import myJOGL.My3DVectorF;

public class Drawer {

	private GLU glu = new GLU();
	
	public void init(GL2 gl2, int screenX, int screenY) {
		
		setupStandard3DProcess(gl2);
		
		gl2.glViewport(0, 0, screenX, screenY);
		
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		
		double aspectratio = (double)screenX / screenY;
		glu.gluPerspective(45, aspectratio, 0.1, 1000);
		
		My3DVectorF cameraPoint = new My3DVectorF(0,0,0);
		My3DVectorF lookPoint = new My3DVectorF(0,0,-1);
		
		setView(gl2, cameraPoint, lookPoint);
	}
	
	public void draw(GL2 gl2) {
		
		
		
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
		gl2.glEnable(GL2.GL_TEXTURE_2D);					// 二次元テクスチャ有効
	}

	//　幾何データとピクセルデータを合わせてフラグメント化する処理のこと
	private void setupRasterizeProcess(GL2 gl2, int mode)
	{
		gl2.glShadeModel(mode);							// シェーディングモード設定

		float[] a = new float[2];
		FloatBuffer ps = FloatBuffer.wrap(a, 0, 2);
		gl2.glGetFloatv(GL2.GL_LINE_WIDTH_RANGE,ps);		// 描画線の太さ範囲を取得
		gl2.glLineWidth(ps.get(0));							// 最も細い線（ps[0])を選択
	}

	// フラグメントを加工してバッファに格納する処理のこと
	private void setupFragmentProcess(GL2 gl2){
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);					// デプステスト有効
	}
}
