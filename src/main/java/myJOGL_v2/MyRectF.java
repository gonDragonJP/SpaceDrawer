package myJOGL_v2;

public class MyRectF {
	
	public float left,top,right,bottom;
	
	public MyRectF(float left, float top, 
					float right, float bottom){
		
		this.left = left;	this.top = top;
		this.right = right; this.bottom = bottom;
	}
	
	public MyRectF() {
		initialize();
	}
	
	public void initialize(){
		left = top = right = bottom = 0;
	}

	public void set(float left, float top, float right, float bottom) {
	    this.left   = left;
	    this.top    = top;
	    this.right  = right;
	    this.bottom = bottom;
	}

	public void copy(final MyRectF src){
		this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
	}
	
	public final float width(){
		
		return right - left;
	}
	
	public final float height(){
		
		return bottom - top;
	}
	
	public final float centerX() {
	        return (left + right) * 0.5f;
	    }

	public final float centerY() {
	        return (top + bottom) * 0.5f;
	}
	
	@Override
	public String toString() {
        return "RectF(" + left + ", " + top + ", "
                      + right + ", " + bottom + ")";
    }
	
	 public final boolean isEmpty() {
	        return left >= right || top >= bottom;
	}

}
