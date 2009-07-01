package org.cytoscape.view.presentation.processing.internal.particle;

import javax.media.opengl.GL;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;

public class ParTest1 extends PApplet {
	PGraphicsOpenGL pgl;
	GL gl;

	ParticleManager particleManager;
	VerletPhysics physics;
	float rotX, rotY, zoom = 200;
	AABB boundingBox;

	public void setup() {
		size(1024, 600, OPENGL);
		frameRate(30);
		physics = new VerletPhysics();
		physics.friction = 1000;
		AABB boundingBox = new AABB(new Vec3D(0, 0, 0), new Vec3D(width,
				height, height));
		physics.worldBounds = boundingBox;
		particleManager = new ParticleManager(10000, this, physics, gl);
	}

	public void draw() {
		background(0);
		physics.update();
		camera(width / 2.0f, height / 2.0f, (height / 2.0f)
				/ tan((float) (PI * 60.0 / 360.0)) + zoom, width / 2.0f,
				height / 2.0f, 0, 0, 1, 0);
		translate(width / 2, height / 2, height / 2);
		rotateX(rotY);
		rotateY(rotX);
		translate(-width / 2, -height / 2, -height / 2);
		beginGL();
		particleManager.manage();
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

	public void mouseDragged() {
		if (mouseButton == RIGHT) {
			zoom += (mouseY - pmouseY) * 1;
		} else if (mouseButton == LEFT) {
			rotX += (mouseX - pmouseX) * 0.01;
			rotY -= (mouseY - pmouseY) * 0.01;
		}
	}

}
