package myJOGL_v2;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

public class MyGLWinWrap implements GLEventListener{
	
	public interface MyRenderable{
		
		void init(GL gl);
		void render(GL gl);
	}
	
	private GLWindow window;
	private MyRenderable renderer;
	private Animator animator;
	
	@SuppressWarnings("unused")
	private MyGLWinWrap(){
		
	}
	
	public MyGLWinWrap(String glProfile){
		
		GLCapabilities caps = new GLCapabilities(
				
				GLProfile.get(glProfile)
		);
		window = GLWindow.create(caps);
		initWindow();
		
		initAnimator();
	}
	
	private void initWindow(){
		
		WindowAdapter adapter = new WindowAdapter(){
			
			@Override
		    public void windowDestroyed(final WindowEvent e) {
				
				System.exit(0);
		    }
		};
		window.addWindowListener(adapter);
		window.addGLEventListener(this);
	}
	
	private void initAnimator(){
		
		animator = new Animator();
		animator.add(window);
	}
	
	public GLWindow getWindow(){
		
		return window;
	}
	
	public Animator getAnimator(){
		
		return animator;
	}
	
	public void setRenderer(MyRenderable renderer){
		
		this.renderer = renderer;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		
		GL gl = drawable.getGL();
		
		renderer.init(gl);
		renderer.render(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		
		renderer.render(drawable.getGL());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
		
	}
}
