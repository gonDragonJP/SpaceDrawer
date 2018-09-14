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
	
	//�@�����ݒ肵�Ȃ��Ǝ��_�͌��_�A�������͂����̕������ł��B
	// ����ϊ��i���_�Ǝ����̕ϊ��j�̓��f���s��ɉe�����܂��B
	// ���e�ϊ��͎����������ǂꂾ���̎��̐ς�؂�o������ݒ肵�A���e�s��ɉe�����܂��B
	
	// ����ϊ�
	// ���f���S�̂���]���s�ړ�������̂Ɠ����T�O�Ŏ����ϊ����܂��B
	// ���ʁA�ϊ���̎��_�͌��_�A�����͂����������̂܂܂��ƍl���鎖���o���܂��B
	private void setView(GL2 gl2, My3DVectorF cameraPoint, My3DVectorF lookPoint)
	{
		if( (lookPoint.x == cameraPoint.x) && (lookPoint.z == cameraPoint.z) ){
			// �W���o�����b�N�Ȃ̂ōs��ϊ�����
			return;	
		}
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);					// �s��ϊ������f�����W�ɓK�p

		My3DVectorF v1, v2, v3, v4;

		v1 = new My3DVectorF(lookPoint);
		v1.minus(cameraPoint);
		v2 = new My3DVectorF(0,1,0);						// ��ɏd�͕�������ʒ��S���ɂ���J����
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

	// �􉽃f�[�^�Ɋւ��鏈��
	private void setupPrimitiveAsembleProcess(GL2 gl2)
	{
		gl2.glPolygonMode(GL2.GL_FRONT,GL2.GL_FILL);		// �|���S���\�ʂ͓h��Ԃ�
		gl2.glPolygonMode(GL2.GL_BACK,GL2.GL_LINE);			// �|���S�����ʂ͐��݂̂ŕ`��
		gl2.glFrontFace(GL2.GL_CCW);						// �����v����ł̒��_��`��\�ʂƂ���	
	}

	//�@�s�N�Z���f�[�^�i�e�N�X�`�����j�Ɋւ��鏈��
	private void setupPixelAsembleProcess(GL2 gl2)
	{
		gl2.glEnable(GL2.GL_TEXTURE_2D);				// �񎟌��e�N�X�`���L��
	}

	//�@�􉽃f�[�^�ƃs�N�Z���f�[�^�����킹�ăt���O�����g�����鏈���̂���
	private void setupRasterizeProcess(GL2 gl2, int mode)
	{
		gl2.glShadeModel(mode);							// �V�F�[�f�B���O���[�h�ݒ�

		float[] a = new float[2];
		FloatBuffer ps = FloatBuffer.wrap(a, 0, 2);
		gl2.glGetFloatv(GL2.GL_LINE_WIDTH_RANGE,ps);	// �`����̑����͈͂��擾
		gl2.glLineWidth(ps.get(0));						// �ł��ׂ����ips[0])��I��
	}

	// �t���O�����g�����H���ăo�b�t�@�Ɋi�[���鏈���̂���
	private void setupFragmentProcess(GL2 gl2){
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);				// �f�v�X�e�X�g�L��
	}
	
	public void rotateAndTranslate
		(GL2 gl2, float heading, float pitch, float bank, float tx, float ty, float tz){
		
		gl2.glMatrixMode(GL2.GL_MODELVIEW);				// �s��ϊ������f�����W�ɓK�p
		
		gl2.glTranslatef(tx,ty,tz);						// ���s�ړ�(��]�ړ��̌�Ȃ̂Ő�ɂ�����j
		
		gl2.glRotatef(bank,0,0,1);
		gl2.glRotatef(pitch,1,0,0);						// ��](EulerAngles-degree)
		gl2.glRotatef(heading,0,1,0);
	}
}
