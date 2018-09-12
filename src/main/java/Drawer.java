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
		gl2.glEnable(GL2.GL_TEXTURE_2D);					// �񎟌��e�N�X�`���L��
	}

	//�@�􉽃f�[�^�ƃs�N�Z���f�[�^�����킹�ăt���O�����g�����鏈���̂���
	private void setupRasterizeProcess(GL2 gl2, int mode)
	{
		gl2.glShadeModel(mode);							// �V�F�[�f�B���O���[�h�ݒ�

		float[] a = new float[2];
		FloatBuffer ps = FloatBuffer.wrap(a, 0, 2);
		gl2.glGetFloatv(GL2.GL_LINE_WIDTH_RANGE,ps);		// �`����̑����͈͂��擾
		gl2.glLineWidth(ps.get(0));							// �ł��ׂ����ips[0])��I��
	}

	// �t���O�����g�����H���ăo�b�t�@�Ɋi�[���鏈���̂���
	private void setupFragmentProcess(GL2 gl2){
		
		gl2.glEnable(GL2.GL_DEPTH_TEST);					// �f�v�X�e�X�g�L��
	}
}
