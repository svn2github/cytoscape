import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.FloatBuffer;
import java.util.Random;

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

		private static final int NODE_COUNT = 100;
		private static final int EDGE_COUNT = 200;
		private static final float LARGE_SPHERE_RADIUS = 2.0f;
		private static final float SMALL_SPHERE_RADIUS = 0.03f;
		private static final float EDGE_RADIUS = 0.01f;
		
		private static final int NODE_SLICES_DETAIL = 6;
		private static final int NODE_STACKS_DETAIL = 3;
		
		private static final int EDGE_SLICES_DETAIL = 4;
		private static final int EDGE_STACKS_DETAIL = 1;
		
		private DrawnNode[] nodes;
		private DrawnEdge[] edges;
		
		private float yRotate = 0;
		
		private int nodeListIndex;
		
		private long startTime;
		private long endTime;
		private int framesElapsed = 0;
		
		private class DrawnNode {
			public float x;
			public float y;
			public float z;
		}
		
		private class DrawnEdge {
			public float x;
			public float y;
			public float z;
			public float rotateAxisX;
			public float rotateAxisY;
			public float rotateAxisZ;
			public float rotateAngle;
			public float length;
		}
		
		private int nodeSeed = 556;
		private int edgeSeed = 556;
		
        /**
         * @param args
         */
        public static void main(String[] args) {
                JFrame frame = new JFrame("Test");
                frame.setSize(650, 650);

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

			gl.glColor3f(0.3f, 0.6f, 0.3f);
			
			gl.glTranslatef(0.0f, 0.0f, -6.0f);
			//gl.glRotatef(yRotate, 1.0f, -0.5f, 0.0f);
			gl.glRotatef(yRotate, 0.0f, -1.0f, 0.0f);
			// gl.glRotatef(90, 0.0f, -1.0f, 0.0f);
			
			//drawNodes(gl);
			drawEdges(gl);
			
			//yRotate += 0.035;
			yRotate += 0.8;
			
			/*
			GLUT glut = new GLUT();
			// glut.glutSolidCylinder(0.5f, 1.0f, 6, 3);
			
			float x1, y1, z1, x2, y2, z2;
			x1 = -1.0f;
			y1 = 0.5f;
			z1 = 0.0f;
			x2 = 0.9f;
			y2 = -0.6f;
			z2 = 0.0f;
			
			float length = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
			
			gl.glTranslatef(x1, y1, z1);
			glut.glutSolidSphere(0.5f, 6, 3);
			gl.glTranslatef(-x1, -y1, -z1);
			
			gl.glTranslatef(x2, y2, z2);
			glut.glutSolidSphere(0.5f, 6, 3);
			gl.glTranslatef(-x2, -y2, -z2);
			
			gl.glTranslatef(x1, y1, z1);
			
			// float angle = (float) Math.acos(((length)(y2 - y1))/((length)(length)));
			
			float angle = (float) (Math.acos((z2 - z1)/length) * 180 / Math.PI);
			gl.glRotatef(angle, -(y2 - y1), x2 - x1, 0);
			

			glut.glutSolidCylinder(0.1f, length, 6, 1);
			*/
			framesElapsed++;
			
			/*
			endTime = System.nanoTime();
			double duration = (endTime - startTime) / Math.pow(10, 9);
			if (duration > 2.5) {
				System.out.println("Generating new nodes with seed: " + nodeSeed);
				// System.out.println("Generating new edges with seed: " + edgeSeed);
				
				generateNodes();
				generateEdges();
				
				startTime = System.nanoTime();
			}
			*/
		}

		private void drawNodes(GL2 gl) {
			float x, y, z;
			
			// gl.glColor3f(0.5f, 0.5f, 0.5f);
			for (int i = 0; i < NODE_COUNT; i++) {
				x = nodes[i].x;
				y = nodes[i].y;
				z = nodes[i].z;
				
				gl.glTranslatef(x, y, z);
				//glut.glutSolidSphere(SMALL_SPHERE_RADIUS, 5, 5);
				gl.glCallList(nodeListIndex);
				gl.glTranslatef(-x, -y, -z);
			}
		}
		
		private void drawEdges(GL2 gl) {			
			GLUT glut = new GLUT();
			
			// gl.glColor3f(0.9f, 0.1f, 0.1f);
			for (int i = 0; i < EDGE_COUNT; i++) {
				gl.glTranslatef(edges[i].x, edges[i].y, edges[i].z);
				gl.glRotatef(edges[i].rotateAngle, edges[i].rotateAxisX, edges[i].rotateAxisY, edges[i].rotateAxisZ);
				glut.glutSolidCylinder(EDGE_RADIUS, edges[i].length, EDGE_SLICES_DETAIL, EDGE_STACKS_DETAIL);
				// gl.glCallList(nodeListIndex);
				// Undo the transformation operations we performed above
				gl.glRotatef(-edges[i].rotateAngle, edges[i].rotateAxisX, edges[i].rotateAxisY, edges[i].rotateAxisZ);
				gl.glTranslatef(-edges[i].x, -edges[i].y, -edges[i].z);
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
			// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			
			gl.glViewport(0, 0, drawable.getWidth(), drawable.getHeight());
			
			generateNodes();
			generateEdges();
			startTime = System.nanoTime();
			createNodeDisplayList(gl);
			
		}
		
		private void createNodeDisplayList(GL2 gl) {
			nodeListIndex = gl.glGenLists(1);
			GLUT glut = new GLUT();

			gl.glNewList(nodeListIndex, GL2.GL_COMPILE);
			glut.glutSolidSphere(SMALL_SPHERE_RADIUS, NODE_SLICES_DETAIL, NODE_STACKS_DETAIL);
			gl.glEndList();
		}
		
		private void generateNodes() {
			Random random = new Random();
			random.setSeed(nodeSeed);
			nodeSeed++;
			// 500 should be the default seed
			
			nodes = new DrawnNode[NODE_COUNT];
			
			float x, y, z;
			float radius = LARGE_SPHERE_RADIUS;
			
			for (int i = 0; i < NODE_COUNT; i++) {
				nodes[i] = new DrawnNode();
				
				do {
					x = (float)(radius * 2 * random.nextFloat() - radius);
					y = (float)(radius * 2 * random.nextFloat() - radius);
					z = (float)(radius * 2 * random.nextFloat() - radius);
				} while (Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) > Math.pow(radius, 2));
			
				nodes[i].x = x;
				nodes[i].y = y;
				nodes[i].z = z;
			}
			
			//System.out.println("Last node float: " + random.nextFloat());
		}
		
		private void generateEdges() {
			Random random = new Random();
			random.setSeed(edgeSeed);
			edgeSeed++;
			
			edges = new DrawnEdge[EDGE_COUNT];

			int firstNode, secondNode;			
			DrawnNode first, second;
			
			for (int i = 0; i < EDGE_COUNT; i++ ) {
				firstNode = random.nextInt(NODE_COUNT);
				secondNode = random.nextInt(NODE_COUNT);
				
				// System.out.println("Edge from, " + firstNode + " to, " + secondNode);
				
				first = nodes[firstNode];
				second = nodes[secondNode];
				
				// System.out.println("Edge runs from (" + first.x + ",");
				
				edges[i] = new DrawnEdge();
				
				edges[i].x = first.x;
				edges[i].y = first.y;
				edges[i].z = first.z;
				edges[i].length = (float) Math.sqrt(
						Math.pow(first.x - second.x, 2) 
						+ Math.pow(first.y - second.y, 2) 
						+ Math.pow(first.z - second.z, 2));
				
				//System.out.println("Edge has length " + edges[i].length);
				
				edges[i].rotateAxisX = first.y - second.y;
				edges[i].rotateAxisY = second.x - first.x;
				edges[i].rotateAxisZ = 0;
				
				// Convert radians to degrees as well
				edges[i].rotateAngle = (float) (Math.acos((second.z - first.z)/edges[i].length) * 180 / Math.PI);
			}
			
			//System.out.println("Last edge int: " + random.nextInt(NODE_COUNT));
		}
		
		private void initLighting(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			
			float[] global = {0.3f, 0.3f, 0.3f, 1.0f};

			gl.glEnable(GL2.GL_LIGHTING);
			gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(global));
			gl.glShadeModel(GL2.GL_SMOOTH);
			
			float[] ambient = {0.2f, 0.2f, 0.3f, 1.0f};
			float[] diffuse = {0.8f, 0.8f, 0.9f, 1.0f};
			float[] specular = {0.5f, 0.5f, 0.5f, 1.0f};
			float[] position = {8.5f, 5.5f, -1.0f, 1.0f};
			
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

			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			
			endTime = System.nanoTime();
			
			double duration = (endTime - startTime) / Math.pow(10, 9);
			// double frameRate = framesElapsed / duration;
			// System.out.println("Average fps over " + duration + " seconds: " + frameRate);
		}
}