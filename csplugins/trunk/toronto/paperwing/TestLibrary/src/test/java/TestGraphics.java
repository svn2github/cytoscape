import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;

public class TestGraphics implements GLEventListener{

		private static final int NODE_COUNT = 10000;
		private static final float LARGE_SPHERE_RADIUS = 2.0f;
		private static final float SMALL_SPHERE_RADIUS = 0.02f;
		private float[] x;
		private float[] y;
		private float[] z;
		
		private float yRotate;
		
		private int nodeListIndex;
		
        /**
         * @param args
         */
        public static void main(String[] args) {
                JFrame frame = new JFrame("Test");
                frame.setSize(600, 600);

                frame.setLocationRelativeTo(null);
                
                // Use the system's default version of OpenGL
                GLProfile profile = GLProfile.getDefault();

                GLProfile.initSingleton(true);

                GLCapabilities capab = new GLCapabilities(profile);
                GLCanvas canvas = new GLCanvas(capab);

                canvas.addGLEventListener(new TestGraphics());
                frame.add(canvas);
                
                frame.addWindowListener(new WindowAdapter() {
                	
                	@Override
                	public void windowClosing(WindowEvent e) {
                		System.exit(0);
                	}
                });
                
                frame.setVisible(true);
                
                FPSAnimator animator = new FPSAnimator(60);
                animator.add(canvas);
                animator.start();
        }

		@Override
		public void display(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();

			drawNodes(gl, -6.0f);
		}

		private void drawNodes(GL2 gl, float zTranslate) {
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, zTranslate);
			
			// gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			GLUT glut = new GLUT();
			
			gl.glRotatef(yRotate, 0.5f, 0.5f, 0.0f);
			yRotate--;
			
			for (int i = 0; i < NODE_COUNT; i++) {
				gl.glTranslatef(x[i], y[i], z[i]);
				//glut.glutSolidSphere(SMALL_SPHERE_RADIUS, 5, 5);
				//glut.
				gl.glCallList(nodeListIndex);
				gl.glTranslatef(-x[i], -y[i], -z[i]);
			}
		}
		
		@Override
		public void dispose(GLAutoDrawable arg0) {
			
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			initLighting(drawable);
			
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glEnable(GL.GL_DEPTH_TEST);
			
			gl.glDepthFunc(GL.GL_LEQUAL);
			// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
			gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_FASTEST);
			//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			
			gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
			
			computeCoordinates();
			
			createNodeDisplayList(gl);
		}
		
		private void createNodeDisplayList(GL2 gl) {
			nodeListIndex = gl.glGenLists(1);
			GLUT glut = new GLUT();

			gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
			glut.glutSolidSphere(SMALL_SPHERE_RADIUS, 6, 6);
			gl.glEndList();
		}
		
		private void computeCoordinates() {
			x = new float[NODE_COUNT];
			y = new float[NODE_COUNT];
			z = new float[NODE_COUNT];
			
			float radius = LARGE_SPHERE_RADIUS;
			
			for (int i = 0; i < NODE_COUNT; i++) {
				
				do {
					x[i] = (float)(radius * 2 * Math.random() - radius);
					y[i] = (float)(radius * 2 * Math.random() - radius);
					z[i] = (float)(radius * 2 * Math.random() - radius);
				} while (Math.pow(x[i], 2) + Math.pow(y[i], 2) + Math.pow(z[i], 2) > Math.pow(radius, 2));
			}
		}
		
		private void initLighting(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			float[] global = {0.5f, 0.5f, 0.5f, 1.0f};
			// float[] global = {1.0f, 1.0f, 1.0f, 1.0f};
			
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
			gl.glShadeModel(GL2.GL_SMOOTH);
			
			float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
			float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
			float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
			float[] position = {8.5f, 5.5f, -1.0f, 1.0f};
			//float[] position = {0.0f, 0.0f, -1.5f, 1.0f};
			
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambient));
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specular));
			gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(position));
			
			gl.glEnable(GL2.GL_LIGHT0);
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height) {

			if (height <= 0) {
				height = 1;
			}
			
			GL2 gl = drawable.getGL().getGL2();
			
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			
			GLU glu = new GLU();
			glu.gluPerspective(45.0f, (float) width/height, 0.2f, 50.0f);
			// glu.gluCylinder(arg0, arg1, arg2, arg3, arg4, arg5)
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
		}
}