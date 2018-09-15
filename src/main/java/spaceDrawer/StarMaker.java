package spaceDrawer;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import spaceDrawer.Drawer.TextureFile;

public class StarMaker {
	
	float farthestDistance = 1000;
	
	int currentStarsNumber = 100;     
	float distancePercentageNearest  = 20;
	float distancePercentageFarthest = 70;
	float distancePercentegeTextureEnabled    = 30 ;
	
	int backGroundPointStars = 3000;
	
	int currentNebulaeNumber      =10 ;
	float distanceNebulaNearest = 40;
	
	public static final int maxStarData = 500; 
	public static final int maxPointStarData = 5000;
	public static final int maxNebulaData = 100;
	private static StarData[] starData = new StarData[maxStarData];
	private static StarData[] pointStarData = new StarData[maxPointStarData];
	private static StarData[] nebulaData = new StarData[maxNebulaData];
	
	static {
		for(int i=0; i<maxStarData; i++) starData[i] = new StarData();
		for(int i=0; i<maxPointStarData; i++) pointStarData[i] = new StarData();
		for(int i=0; i<maxNebulaData; i++) nebulaData[i] = new StarData();
	}
	
	private static class StarData{
		
		public float x,y,z,rot,a,r,g,b;
		public int texIndex;	
	}
	
	private Drawer drawer;
	
	public StarMaker(Drawer drawer){
		
		this.drawer = drawer;
		
		makeAllData();
	}
	
	private void makeAllData(){

		makeStarsData();
		makePointStarsData();
		makeNebulaeData();
	}

	private void makeStarsData(){

		float n = distancePercentageNearest  * -farthestDistance / 100;
		float f = distancePercentageFarthest * -farthestDistance / 100;
		float textureEnabled = distancePercentegeTextureEnabled * -farthestDistance /100;

		for(int i=0; i<currentStarsNumber; i++){

			starData[i].z = n + (f - n) * (float)Math.random();
			float angleX = drawer.fovx * ((float)Math.random() - 0.5f);
			float angleY = drawer.fovy * ((float)Math.random() - 0.5f);
			starData[i].x = starData[i].z * (float)Math.tan(angleX * drawer.radian);
			starData[i].y = starData[i].z * (float)Math.tan(angleY * drawer.radian);

			starData[i].a = 1.0f;
			starData[i].r = (float)Math.random() * 0.7f +0.3f;
			starData[i].g = (float)Math.random() * 0.7f +0.3f;
			starData[i].b = (float)Math.random() * 0.7f +0.3f;
			starData[i].texIndex = (starData[i].z  > textureEnabled) ? 
				(int)(Math.random() * (TextureFile.maxPlanetIndex+1)): -1;
		}
	}

	private void makePointStarsData(){

		for(int i=0; i<backGroundPointStars; i++){

			float angleX = drawer.fovx * ((float)Math.random() - 0.5f);
			float angleY = drawer.fovy * ((float)Math.random() - 0.5f);
			pointStarData[i].x = -farthestDistance  * (float)Math.tan(angleX * drawer.radian);
			pointStarData[i].y = -farthestDistance  * (float)Math.tan(angleY * drawer.radian);

			pointStarData[i].r = (float)Math.random() * 0.2f + 0.8f;
			pointStarData[i].g = (float)Math.random() * 0.2f + 0.8f;
			pointStarData[i].b = (float)Math.random() * 0.2f + 0.8f;
			pointStarData[i].a = (float)Math.random() * 0.5f + 0.2f;
		}
	}

	private void makeNebulaeData(){

		float n = distanceNebulaNearest * -farthestDistance / 100;
		float f = -farthestDistance;
		
		int nebulaTexNumber = TextureFile.maxNebulaIndex - TextureFile.firstNebulaIndex +1;

		for(int i=0; i<currentNebulaeNumber; i++){

			nebulaData[i].z = n + (f - n) * (float)Math.random();
			float angleX = drawer.fovx * ((float)Math.random() - 0.5f);
			float angleY = drawer.fovy * ((float)Math.random() - 0.5f);
			nebulaData[i].x = nebulaData[i].z * (float)Math.tan(angleX * drawer.radian);
			nebulaData[i].y = nebulaData[i].z * (float)Math.tan(angleY * drawer.radian);
			nebulaData[i].rot = 360 * (float)Math.random();
			nebulaData[i].texIndex = 
					TextureFile.firstNebulaIndex + (int)(nebulaTexNumber * Math.random());
		}
	}

	public void drawStars(GL2 gl2){

		gl2.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

		gl2.glDisable(GL2.GL_BLEND);

		for(int i=0; i<currentStarsNumber; i++){

			float model_ambient[] = { 	starData[i].r,
										starData[i].g,
										starData[i].b,
										1.0f};
			FloatBuffer arg = FloatBuffer.wrap(model_ambient);
			gl2.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, arg);

			int texIndex = starData[i].texIndex;
			
			if(texIndex !=-1){
				int texGLID = drawer.texSheets.get(texIndex).texture.getTextureObject();
				gl2.glBindTexture(GL2.GL_TEXTURE_2D, texGLID);
			}
			else
				gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);

			gl2.glEnable(GL2.GL_TEXTURE_GEN_S);
			gl2.glEnable(GL2.GL_TEXTURE_GEN_T);
			gl2.glTexGeni(GL2.GL_S,GL2.GL_TEXTURE_GEN_MODE,GL2.GL_SPHERE_MAP);
			gl2.glTexGeni(GL2.GL_T,GL2.GL_TEXTURE_GEN_MODE,GL2.GL_SPHERE_MAP);
			
			gl2.glPushMatrix();

			drawer.rotateAndTranslate(gl2, 0,0,0,
				starData[i].x,
				starData[i].y,
				starData[i].z
				);
			Icosahedron.draw(gl2, 2);

			gl2.glPopMatrix();
		}

		gl2.glPopAttrib();
	}

	public void drawPointStars(GL2 gl2){
		
		gl2.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);

		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glDisable(GL2.GL_TEXTURE_2D);	//ライティングとテクスチャを無効にしないと点はうまく表示できません
		gl2.glDisable(GL.GL_DEPTH_TEST);	//デプスも無効にしないと表示されないが理由が解らない…
		
		for(int i=0; i<backGroundPointStars; i++){

			gl2.glColor4f(	pointStarData[i].r,
							pointStarData[i].g,
							pointStarData[i].b,
							pointStarData[i].a
			);
			
			gl2.glPushMatrix();

			drawer.rotateAndTranslate(gl2,0,0,0,
				pointStarData[i].x,
				pointStarData[i].y,
				-farthestDistance * 0.95f
				);

			gl2.glBegin(GL2.GL_POINTS);
				gl2.glNormal3f(0,0,1);
				gl2.glVertex3f(0,0,0);
			gl2.glEnd();

			gl2.glPopMatrix();
		}

		gl2.glPopAttrib();
	}

	public void drawNebulae(GL2 gl2){

		gl2.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

		gl2.glEnable(GL2.GL_BLEND);	
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE_MINUS_SRC_ALPHA);
		//gl2.glDisable(GL2.GL_LIGHTING);

		for(int i=0; i<currentNebulaeNumber; i++){

			int texIndex = nebulaData[i].texIndex;
			int texGLID = drawer.texSheets.get(texIndex).texture.getTextureObject();
			
			gl2.glBindTexture(GL2.GL_TEXTURE_2D, texGLID);

			gl2.glPushMatrix();
			drawer.rotateAndTranslate(gl2,(float)Math.random()*80-40,(float)Math.random()*80-40,
				nebulaData[i].rot,
				nebulaData[i].x,
				nebulaData[i].y,
				nebulaData[i].z
				);
			drawTexRect(gl2);
			gl2.glPopMatrix();
			
		}

		gl2.glPopAttrib();
	}

	private void drawTexRect(GL2 gl2){

		gl2.glBegin(GL2.GL_QUADS);
			gl2.glTexCoord2f(0, 0);	gl2.glVertex2i(0,   0);
			gl2.glTexCoord2f(1, 0);	gl2.glVertex2i(100, 0);
			gl2.glTexCoord2f(1, 1);	gl2.glVertex2i(100, 100);
			gl2.glTexCoord2f(0, 1); gl2.glVertex2i(0,   100);
		gl2.glEnd();
	}

}
