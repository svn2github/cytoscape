package org.cytoscape.view.presentation.processing.internal;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.swing.Icon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.NetworkRenderer;
import org.cytoscape.view.presentation.processing.internal.particle.ParticleManager;

import processing.core.PApplet;
import processing.core.PFont;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;

public class ProcessingNetworkRenderer extends PApplet implements
		NetworkRenderer {

	/*
	 * Basic Processing settings
	 */
	// Mode of renderer. For now, it is set to non-OpenGL version.
	private static final String MODE = P3D;
	private static final int FRAME_RATE = 30;

	private final PFont defaultFont;

	private Dimension windowSize;
	private CyNetworkView view;
	private CyNetwork network;

	//
	GraphRenderer renderer;

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Dimension size, CyNetworkView view) {
		this.view = view;
		this.windowSize = size;

		defaultFont = createFont("SansSerif", 24);
	}

	public Icon getDefaultIcon(VisualProperty vp) {
		// TODO Auto-generated method stub
		return null;
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

	private PGraphicsOpenGL pgl;
	private GL gl;

	ParticleManager particleManager;
	VerletPhysics physics;
	float rotX, rotY, zoom = 200;
	AABB boundingBox;

	public void setup() {
		size(1920, 1080, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		frameRate(30);
		physics = new VerletPhysics();
		physics.friction = 1000;
		AABB boundingBox = new AABB(new Vec3D(0, 0, 0), new Vec3D(width,
				height, height));
		physics.worldBounds = boundingBox;
		particleManager = new ParticleManager(50000, this, physics);
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

	public void mouseDragged() {
		if (mouseButton == RIGHT) {
			zoom += (mouseY - pmouseY) * 2;
		} else if (mouseButton == LEFT) {
			rotX += (mouseX - pmouseX) * 0.01;
			rotY -= (mouseY - pmouseY) * 0.01;
		}
	}

	public CyNetwork getSourceNetwork() {
		return network;
	}

	public CyNetworkView getSourceView() {
		return view;
	}

}
