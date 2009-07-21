package org.cytoscape.view.presentation.processing.internal.particle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import processing.core.PApplet;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;

public class ParticleManager2D {
	FloatBuffer pointBuffer;
	IntBuffer indexBuffer;
	Particle2D particles[];
	int numP;
	int REST_LENGTH = 50;

	VerletPhysics2D physics;

	PApplet parent;

	public ParticleManager2D(PApplet parent, int _numP,
			VerletPhysics2D physics) {
		numP = _numP;
		this.physics = physics;
		this.parent = parent;

		int numberIndices = numP * 2;
		indexBuffer = ByteBuffer.allocateDirect(4 * numberIndices).order(
				ByteOrder.nativeOrder()).asIntBuffer();
		indexBuffer.limit(numberIndices);
		indexBuffer.rewind();

		particles = new Particle2D[numP];
		for (int i = 0; i < numP; i++) {
			int rndX = (int) parent.random(0, parent.width);
			int rndY = (int) parent.random(0, parent.height);
			VerletParticle2D newParticle = new VerletParticle2D(rndX, rndY);
			physics.addParticle(newParticle);
			particles[i] = new Particle2D(i, rndX, rndY);
			if (i > 0) {
				indexBuffer.put(i * 2, i);
				int rndChild = (int) parent.random(i);
				indexBuffer.put(i * 2 + 1, rndChild);
				physics.addSpring(new VerletSpring2D(physics.particles
						.get(rndChild), newParticle, REST_LENGTH, 0.5f));
			}

		}

		for (int i = 0; i < numP; i++) {
			VerletParticle2D p = physics.particles.get(i);
			for (int j = i + 1; j < numP; j++) {
				VerletParticle2D prevP = physics.particles.get(j);
				// physics.addSpring(new VerletMinDistanceSpring2D(prevP, p,
				// REST_LENGTH, 0.5));
			}
		}

		int numberElements = numP * 2;
		pointBuffer = ByteBuffer.allocateDirect(4 * numberElements).order(
				ByteOrder.nativeOrder()).asFloatBuffer();
		pointBuffer.limit(numberElements);
		pointBuffer.rewind();

	}

	public void manage(GL gl) {
		for (int i = 0; i < numP; i++) {
			VerletParticle2D p = physics.particles.get(i);
			particles[i].posX = p.x;
			particles[i].posY = p.y;
			particles[i].writeToBuffer(pointBuffer, i * 2);
		}
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glPointSize(2.0f);
		gl.glVertexPointer(2, GL.GL_FLOAT, 0, pointBuffer);
		gl.glColor3f(0, 0, 100);
		gl.glDrawElements(GL.GL_LINES, numP * 2, GL.GL_UNSIGNED_INT,
				indexBuffer);
		gl.glColor3f(100, 10, 0);
		gl.glDrawArrays(GL.GL_POINTS, 0, numP);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
	}
}
