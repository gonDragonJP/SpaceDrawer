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
		
		String dir =".\\texture\\";
		
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
		
		setLighting(gl2); 	//���C�e�B���O�̓��[���h���W�n�ōs����̂Ŏ���ϊ��̐ݒ�O�ɐݒ肵�܂�
							//���C�e�B���O�ݒ�̓��f���ϊ��̉e�����󂯂܂����e����^���͂��܂���
		
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
