package spaceDrawer;

import java.nio.FloatBuffer;

import com.jogamp.opengl.GL2;

import myJOGL_v2.My3DVectorF;

public class Icosahedron {

	private Icosahedron() {
		
	};
	
	private static final float X = 0.525731112119133606f;
	private static final float Z = 0.850650808352039932f;
	
	static float vdata[][] = {
		{-X, 0, Z}, { X, 0, Z}, {-X, 0,-Z}, { X, 0,-Z},
		{ 0, Z, X}, { 0, Z,-X}, { 0,-Z, X}, { 0,-Z,-X},
		{ Z, X, 0}, {-Z, X, 0}, { Z,-X, 0}, {-Z,-X, 0},
	};
	
	static int tindices[][] = {
		{ 1, 4, 0}, { 4, 9, 0}, { 4, 5, 9}, { 8, 5, 4}, { 1, 8, 4},
		{ 1,10, 8}, {10, 3, 8}, { 8, 3, 5}, { 3, 2, 5}, { 3, 7, 2},
		{ 3,10, 7}, {10, 6, 7}, { 6,11, 7}, { 6, 0,11}, { 6, 1, 0},
		{10, 1, 6}, {11, 0, 9}, { 2,11, 9}, { 5, 2, 9}, {11, 2, 7},
	};
	
	
	private static GL2 gl2;
	
	public static void draw(GL2 argGL2, int divideCount){
		
		gl2 = argGL2;
		
		My3DVectorF v1 = new My3DVectorF();
		My3DVectorF v2 = new My3DVectorF();
		My3DVectorF v3 = new My3DVectorF();
	
		for(int i=0; i<20; i++){
			
			v1.set(vdata[tindices[i][0]][0], vdata[tindices[i][0]][1], vdata[tindices[i][0]][2]);
			v2.set(vdata[tindices[i][1]][0], vdata[tindices[i][1]][1], vdata[tindices[i][1]][2]);
			v3.set(vdata[tindices[i][2]][0], vdata[tindices[i][2]][1], vdata[tindices[i][2]][2]);
			
			subdivide(v1,v2,v3,divideCount); 		
		}
	}
	
	private static void drawTriangle(My3DVectorF v1, My3DVectorF v2, My3DVectorF v3){
		
		float v1a[] = {v1.x, v1.y, v1.z};
		float v2a[] = {v2.x, v2.y, v2.z};
		float v3a[] = {v3.x, v3.y, v3.z};
		
		FloatBuffer v1b = FloatBuffer.wrap(v1a) ;
		FloatBuffer v2b = FloatBuffer.wrap(v2a) ;
		FloatBuffer v3b = FloatBuffer.wrap(v3a) ;
		
		gl2.glBegin(GL2.GL_TRIANGLES);
	
			gl2.glNormal3fv(v1b);
			gl2.glVertex3fv(v1b);
			gl2.glNormal3fv(v2b);
			gl2.glVertex3fv(v2b);
			gl2.glNormal3fv(v3b);
			gl2.glVertex3fv(v3b);
	
		gl2.glEnd();
	}
	
	private static void subdivide
		(My3DVectorF v1, My3DVectorF v2, My3DVectorF v3, int divideCount){
	
		if(divideCount == 0){
	
			drawTriangle( v1, v2, v3);
			return;
		}
	
		My3DVectorF v12 = new My3DVectorF(v1);
		v12.plus(v2);
		v12.mult(0.5f);
		v12.normalize();
		
		My3DVectorF v23 = new My3DVectorF(v2);
		v23.plus(v3);
		v23.mult(0.5f);
		v23.normalize();
		
		My3DVectorF v31 = new My3DVectorF(v3);
		v31.plus(v1);
		v31.mult(0.5f);
		v31.normalize();
	
		subdivide( v1, v12, v31, divideCount-1);
		subdivide( v2, v23, v12, divideCount-1);
		subdivide( v3, v31, v23, divideCount-1);
		subdivide(v12, v23, v31, divideCount-1);
	}

}
