package myJOGL;

public class My3DVectorF{
	
	public float x, y, z;

	public My3DVectorF(){}
	
	public My3DVectorF(My3DVectorF a) {
		
		x = a.x;
		y = a.y;
		z = a.z;
	}
	
	public My3DVectorF(float nx, float ny, float nz) {
		x=nx; y=ny; z=nz;
	}

	public void set(My3DVectorF a){

		x = a.x;
		y = a.y;
		z = a.z;
	}
	
	public void set(float nx, float ny, float nz) {
		x=nx; y=ny; z=nz;
	}
	
	public boolean isEqual(My3DVectorF a){

		return x == a.x && y == a.y && z == a.z;
	}

	public void plus(My3DVectorF a){

		x+=a.x; y+=a.y; z+=a.z;
	}
	
	public void minus(My3DVectorF a){

		x-=a.x; y-=a.y; z-=a.z;
	}

	public void mult(float a){

		x=x*a; y=y*a; z=z*a;
	}

	public void zero(){
		x = y = z = 0.0f;
	}

	public void normalize(){

		float magSq = x*x + y*y + z*z;
		if(magSq > 0.0f){
			float oneOverMag = 1.0f / (float)Math.sqrt(magSq);
			x *= oneOverMag;
			y *= oneOverMag;
			z *= oneOverMag;
		}
	}

	public static float vectorMag(My3DVectorF a){
	
		return (float)Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
	}
	public static My3DVectorF crossProduct(My3DVectorF a, My3DVectorF b){
	
		return new My3DVectorF(	a.y * b.z - a.z * b.y,
								a.z * b.x - a.x * b.z,
								a.x * b.y - a.y * b.x	);
	}
	
	public static float distance(My3DVectorF a, My3DVectorF b){
	
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		float dz = a.z - b.z;
	
		return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
};

