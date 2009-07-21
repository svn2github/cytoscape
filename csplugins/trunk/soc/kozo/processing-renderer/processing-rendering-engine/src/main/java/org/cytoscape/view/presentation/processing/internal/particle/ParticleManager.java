package org.cytoscape.view.presentation.processing.internal.particle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

import processing.core.PApplet;
import toxi.physics.VerletParticle;
import toxi.physics.VerletPhysics;
import toxi.physics.VerletSpring;

public class ParticleManager {

	private final FloatBuffer pointBuffer;
	private IntBuffer indexBuffer;
	private Particle particles[];

	private final int numP;
	private static final int REST_LENGTH = 100;

	private VerletPhysics physics;

	public ParticleManager(int _numP, PApplet parent, VerletPhysics physics) {
		this.numP = _numP;
		this.physics = physics;

		final int numberIndices = numP * 2;
		indexBuffer = ByteBuffer.allocateDirect(4 * numberIndices).order(
				ByteOrder.nativeOrder()).asIntBuffer();
		indexBuffer.limit(numberIndices);
		indexBuffer.rewind();

		particles = new Particle[numP];

		for (int i = 0; i < numP; i++) {
			int rndX = (int) parent.random(0, parent.width);
			int rndY = (int) parent.random(0, parent.height);
			int rndZ = (int) parent.random(0, parent.height);

			VerletParticle newParticle = new VerletParticle(rndX, rndY, rndZ);

			physics.addParticle(newParticle);
			particles[i] = new Particle(rndX, rndY, rndZ);
			if (i > 0) {
				indexBuffer.put(i * 2, i);
				int rndChild = (int) parent.random(i);
				indexBuffer.put(i * 2 + 1, rndChild);
				VerletSpring newSpring = new VerletSpring(physics.particles
						.get(rndChild), newParticle, REST_LENGTH, 0.5f);
				// VerletConstrainedSpring2D newSpring = new
				// VerletConstrainedSpring2D(physics.particles.get(rndChild),
				// newParticle, REST_LENGTH, 0.5)
				// newSpring.limit = 0.5;
				physics.addSpring(newSpring);
			}

		}

//		for (int i = 0; i < numP; i++) {
//			VerletParticle p = physics.particles.get(i);
//			for (int j = i + 1; j < numP; j++) {
//				VerletParticle prevP = physics.particles.get(j);
//				// physics.addSpring(new VerletMinDistanceSpring(prevP, p,
//				// REST_LENGTH+100, 0.5));
//			}
//		}

		int numberElements = numP * 3;
		pointBuffer = ByteBuffer.allocateDirect(4 * numberElements).order(
				ByteOrder.nativeOrder()).asFloatBuffer();
		pointBuffer.limit(numberElements);
		pointBuffer.rewind();

	}

	public void draw(GL gl) {
		VerletParticle p;
		for (int i = 0; i < numP; i++) {
			p = physics.particles.get(i);
			particles[i].x = p.x;
			particles[i].y = p.y;
			particles[i].z = p.z;
			pointBuffer.rewind();
			particles[i].put(pointBuffer, i * 3);
		}

		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glPointSize(16.0f);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, pointBuffer);
		gl.glColor3f(100, 100, 100);
		gl.glDrawElements(GL.GL_LINES, numP * 2, GL.GL_UNSIGNED_INT,
				indexBuffer);
		gl.glColor3f(100, 50, 100);
		gl.glDrawArrays(GL.GL_POINTS, 0, numP);
		
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	}
	
	private void drawPolygon(GL gl, int vIdx0, int vIdx1, int vIdx2, int vIdx3)
	  // the polygon verticies come from the verts[] array
	  {
	    gl.glBegin(GL.GL_POLYGON);
	      gl.glVertex3f( verts[vIdx0][0], verts[vIdx0][1], verts[vIdx0][2] );
	      gl.glVertex3f( verts[vIdx1][0], verts[vIdx1][1], verts[vIdx1][2] );
	      gl.glVertex3f( verts[vIdx2][0], verts[vIdx2][1], verts[vIdx2][2] );
	      gl.glVertex3f( verts[vIdx3][0], verts[vIdx3][1], verts[vIdx3][2] );
	    gl.glEnd();
	  }
	
	private static final float[][] verts = { 
		     {-1.0f,-1.0f, 1.0f},  // vertex 0
		     {-1.0f, 1.0f, 1.0f},  // 1
		     { 1.0f, 1.0f, 1.0f},  // 2
		     { 1.0f,-1.0f, 1.0f},  // 3
		     {-1.0f,-1.0f,-1.0f},  // 4
		     {-1.0f, 1.0f,-1.0f},  // 5
		     { 1.0f, 1.0f,-1.0f},  // 6
		     { 1.0f,-1.0f,-1.0f},  // 7
		  };
}