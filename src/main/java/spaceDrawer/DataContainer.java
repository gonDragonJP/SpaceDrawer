package spaceDrawer;

public class DataContainer {
	
	boolean isEnabledTestPlanet = true;
	
	int screenX = 320;
	int screenY = 480;
	float fovy = 90;		//画面縦方向のカメラの画角
	
	float farthestDistance = 1000;
	
	int currentStarsNumber = 100;     
	float distancePercentageNearest  = 20;
	float distancePercentageFarthest = 70;
	float distancePercentegeTextureEnabled    = 30 ;
	
	int backGroundPointStars = 3000;
	
	int currentNebulaeNumber      =10 ;
	float distanceNebulaNearest = 40;
	float textureTransparency = 60; //Textureのアルファ値に乗算された透明度となります

}
