package org.cytoscape.view.presentation.processing.internal;

import gestalt.render.Drawable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.swing.Icon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Renderer;
import org.cytoscape.view.presentation.processing.internal.particle.ParticleManager;
import org.cytoscape.view.presentation.processing.internal.shape.GCube;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;

public class ProcessingNetworkRenderer extends PApplet implements
		P5Renderer<CyNetwork> {

	/*
	 * Basic Processing settings
	 */
	// Mode of renderer. For now, it is set to non-OpenGL version.
	private static final String MODE = P3D;
	private static final int FRAME_RATE = 30;

	private Dimension windowSize;
	private View<CyNetwork> view;

	GraphRenderer renderer;

	Drawable[] nodes;

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Dimension size, View<CyNetwork> view) {
		this.windowSize = size;
		this.view = view;
		System.out.println("%%%%%%%%%%%%% Constructor called for P5");
		System.out.println("\n\n\n\n\n\n!!!!!!!!!! Calling constructor: ");
	}

	public Image getImage(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	public Printable getPrintable() {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProperties(Properties props) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public VisualLexicon getVisualLexicon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	PGraphicsOpenGL pgl;
	GL gl;

	ParticleManager particleManager;
	VerletPhysics physics;
	float rotX, rotY, zoom = 200;
	AABB boundingBox;
	
	GCube cube;
	
	public void setup() {
		System.out.println("%%%%%%%%%%%%% Setup called for P5");
		/* setup p5 */
		size(windowSize.width, windowSize.width, OPENGL);
		frameRate(30);
		
		physics = new VerletPhysics();
		physics.friction = 1000;
		AABB boundingBox = new AABB(new Vec3D(0, 0, 0), new Vec3D(width,
				height, height));
		physics.worldBounds = boundingBox;
		particleManager = new ParticleManager(200, this, physics);
		
		cube = new GCube(this);
		System.out.println("%%%%%%%%%%%%% Setup DONE for P5");
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
		particleManager.draw(gl);
		endGL();
		// println(frameRate);
		cube.draw();
		
		
	}

	public void beginGL() {
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL();
		gl.glViewport(0, 0, width, height);
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
	


	public Icon getDefaultIcon(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return null;
	}

	public CyDrawable getCyDrawable() {
		return null;
	}

	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public View<CyNetwork> getViewModel() {
		return null;
	}

}
