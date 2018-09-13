package myJOGL_v2;

public class MyPointF implements Cloneable{
	
	float x;
    float y;

    public MyPointF(final float x, final float y) {
        this.x=x;
        this.y=y;
    }

    public MyPointF() {
        this(0, 0);
    }

    public Object cloneMutable() {
      return clone();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }

	public final float getX() {

		return x;
	}

	public final float getY() {
		
		return y;
	}
	
	@Override
    public String toString() {
        return x + " / " + y;
    }

    public final void set(final float x, final float y) { this.x = x; this.y = y; }
    public final void setX(final float x) { this.x = x; }
    public final void setY(final float y) { this.y = y; }
}
