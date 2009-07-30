package org.cytoscape.view.presentation.processing.internal;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.Printable;
import java.util.List;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
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
import org.cytoscape.view.presentation.processing.Pickable;
import org.cytoscape.view.presentation.processing.internal.particle.ParticleManager;

import processing.core.PApplet;
import processing.core.PFont;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.physics.VerletPhysics;

import com.sun.opengl.util.FPSAnimator;

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

	private final P5NodeRenderer nodeRenderer;
	private final P5EdgeRenderer edgeRenderer;
	
	private GLCanvas canvas;

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Dimension size, CyNetworkView view) {
		System.out.println("%%%%%%%%%%%%% Constructor called for P5");

		this.windowSize = size;
		System.out.println("%%%%%%%%%%%%% Constructor called for P5: 1");
		this.view = view;
		System.out.println("%%%%%%%%%%%%% Constructor called for P5: 2");
		this.nodeRenderer = new P5NodeRenderer(this);
		this.edgeRenderer = new P5EdgeRenderer(this, view);

		
		this.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				zoom +=e.getWheelRotation()*100;
				
			}
			
		});
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
	float transX, transY;
	AABB boundingBox;
	
	PFont defFont;

	class GLRenderer implements GLEventListener { 
		  GL gl; 
		 
		  public void init(GLAutoDrawable drawable) { 
		    this.gl = drawable.getGL(); 
		    gl.glClearColor(1, 0, 0, 0);    
		    canvas.setLocation(100, 80);     
		  } 
		 
		  public void display(GLAutoDrawable drawable) { 
		    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT ); 
		    gl.glColor3f(1, 1, 1);  
		    gl.glRectf(-0.8f, 0.8f, frameCount%100/100f -0.8f, 0.7f); 
		  } 
		 
		  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { 
		  } 
		 
		  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { 
		  } 
		}
	
	
	public void setup() {
		System.out.println("%%%%%%%%%%%%% Setup called for P5");
		/* setup p5 */
		size(windowSize.width, windowSize.width, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_NATIVE_FONTS);
		noStroke();

		frameRate(30);
		
		defFont = createFont("SansSerif", 32);
		textFont(defFont);

		// Convert views to drawable
		final List<View<CyNode>> nodeViews = view.getNodeViews();
		nodes = new CyDrawable[nodeViews.size()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = nodeRenderer.render(nodeViews.get(i));

		final List<View<CyEdge>> edgeViews = view.getEdgeViews();
		edges = new CyDrawable[edgeViews.size()];
		for (int i = 0; i < edges.length; i++)
			edges[i] = edgeRenderer.render(edgeViews.get(i));
		
		
		System.out.println("%%%%%%%%%%%%% Setup DONE for P5");
	}

	public void draw() {
		background(240);
		lights();
		
//		camera(width / 2.0f, height / 2.0f, (height / 2.0f)
//				/ tan((float) (PI * 60.0 / 360.0)) + zoom, width / 2.0f,
//				height / 2.0f, 0, 0, 1, 0);
		
		camera(width / 2.0f, height / 2.0f, (height / 2.0f) / tan((float) (PI * 60.0 / 360.0)) + zoom, 
				width / 2.0f, height / 2.0f, 0, 
				0, 1, 0);
		
		translate(width / 2, height / 2, height / 2);
		rotateX(rotY);
		rotateY(rotX);
		translate(-width / 2 + translateX, -height / 2 + translateY, -height / 2);
		beginGL();
		for (CyDrawable node : nodes)
			node.draw();
		
		for (CyDrawable edge : edges)
			edge.draw();
		endGL();
		
		fill(200, 0, 0);
		text("Cytoscape Test", 100, 100, 0);
		
	}

	public void beginGL() {
		pgl = (PGraphicsOpenGL) g;
		gl = pgl.beginGL();
		gl.glViewport(0, 0, width, height);
	}

	public void endGL() {
		pgl.endGL();
	}

	float translateX = 0;
	float translateY = 0;
	public void mouseDragged() {
		if (mouseButton == RIGHT) {
			rotX += (mouseX - pmouseX) * 0.01;
			rotY -= (mouseY - pmouseY) * 0.01;
		} else if (mouseButton == LEFT) {
			translateX += (mouseX - pmouseX) * 2;
			translateY += (mouseY - pmouseY) * 2;
		}
//		else if (mouseButton == LEFT) {
//			
//		}
	}
	
	public void mousePressed() {
		if(mouseButton != CENTER) return;
		
		System.out.println("===Mouse Click");
		for(int i=0; i<nodes.length; i++) {
			if(nodes[i] instanceof Pickable) {
				((Pickable) nodes[i]).pick(mouseX, mouseY);
			}
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
