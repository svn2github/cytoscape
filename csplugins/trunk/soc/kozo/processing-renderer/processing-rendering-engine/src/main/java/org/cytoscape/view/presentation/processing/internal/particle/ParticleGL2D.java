package org.cytoscape.view.presentation.processing.internal.particle;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.physics2d.VerletPhysics2D;

public class ParticleGL2D extends PApplet {

	PGraphicsOpenGL pgl;
	GL gl;

	ParticleManager2D particleManager;
	VerletPhysics2D physics;

	public void setup() {
		size(1024, 600, OPENGL);
		frameRate(30);
		physics = new VerletPhysics2D();
		particleManager = new ParticleManager2D(this, 10000, physics);
	}

	public void draw() {
		background(0);
		physics.update();
		beginGL();
		particleManager.manage(gl);
		endGL();
		// println(frameRate);
	}

	public void beginGL() {
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL();
	}

	public void endGL() {
		pgl.endGL();
	}
}