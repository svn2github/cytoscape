package org.cytoscape.view.presentation.processing.internal;

import gestalt.render.Drawable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.print.Printable;
import java.util.List;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.swing.Icon;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.internal.particle.ParticleManager;
import org.cytoscape.view.presentation.processing.internal.shape.Cube;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;

public class ProcessingNetworkRenderer extends PApplet implements
		RenderingEngine {

	/*
	 * Basic Processing settings
	 */
	// Mode of renderer. For now, it is set to non-OpenGL version.
	private static final String MODE = P3D;
	private static final int FRAME_RATE = 30;

	private Dimension windowSize;
	private CyNetworkView view;

	GraphRenderer renderer;

	private CyDrawable[] nodes;
	private CyDrawable[] edges;

	private P5NodeRenderer nodeRenderer;

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Dimension size, CyNetworkView view) {
		System.out.println("%%%%%%%%%%%%% Constructor called for P5");
		try {
			this.windowSize = size;
			System.out.println("%%%%%%%%%%%%% Constructor called for P5: 1");
			this.view = view;
			System.out.println("%%%%%%%%%%%%% Constructor called for P5: 2");
			this.nodeRenderer = new P5NodeRenderer(this);
			System.out.println("%%%%%%%%%%%%% Constructor called for P5: 3");
		} catch (Exception e) {
			e.printStackTrace();
		}

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


	public void setup() {
		System.out.println("%%%%%%%%%%%%% Setup called for P5");
		/* setup p5 */
		size(windowSize.width, windowSize.width, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_NATIVE_FONTS);
		noStroke();
		
		frameRate(30);

		// Convert views to drawable
		final List<View<CyNode>> nodeViews = view.getNodeViews();
		nodes = new CyDrawable[nodeViews.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = nodeRenderer.render(nodeViews.get(i));
		}

		final List<View<CyEdge>> edgeViews = view.getEdgeViews();
		edges = new CyDrawable[edgeViews.size()];

		System.out.println("%%%%%%%%%%%%% Setup DONE for P5");
	}

	public void draw() {
		background(0);
		lights();
		
		camera(width / 2.0f, height / 2.0f, (height / 2.0f)
				/ tan((float) (PI * 60.0 / 360.0)) + zoom, width / 2.0f,
				height / 2.0f, 0, 0, 1, 0);
		translate(width / 2, height / 2, height / 2);
		rotateX(rotY);
		rotateY(rotX);
		translate(-width / 2, -height / 2, -height / 2);
		beginGL();		
		for (CyDrawable node : nodes)
			node.draw();

		// for(CyDrawable edge: edges)
		// edge.draw();
		
		endGL();
		
		

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
			zoom += (mouseY - pmouseY) * 2;
		} else if (mouseButton == LEFT) {
			rotX += (mouseX - pmouseX) * 0.01;
			rotY -= (mouseY - pmouseY) * 0.01;
		}
	}

	public Icon getDefaultIcon(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return null;
	}

	public View<CyNetwork> getViewModel() {
		return view;
	}

}
