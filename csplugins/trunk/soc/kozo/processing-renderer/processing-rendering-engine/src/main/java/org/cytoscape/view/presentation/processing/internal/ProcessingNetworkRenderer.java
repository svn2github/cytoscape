package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.internal.particle.ParticleManager;
import org.cytoscape.view.presentation.processing.internal.ui.Overlay;
import org.cytoscape.view.vizmap.events.VisualStyleChangedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleChangedListener;
import org.cytoscape.view.vizmap.events.VisualStyleSwitchedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleSwitchedListener;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PMatrix;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.physics.VerletPhysics;


public class ProcessingNetworkRenderer extends PApplet implements
		RenderingEngine, VisualStyleChangedListener, VisualStyleSwitchedListener {
	
	// TODO: We need to remove this static strings!!
	private static final String NAME = "name";

	/*
	 * Basic Processing settings
	 */
	// Mode of renderer. For now, it is set to non-OpenGL version.
	private static final String MODE = P3D;
	private static final int FRAME_RATE = 30;

	private Dimension windowSize;
	private CyNetworkView view;
	
	private CyDrawableManager manager;

	GraphRenderer renderer;

	// Drawables
	private CyDrawable[] nodes;
	private CyDrawable[] edges;

	// These are singletons.
	private final P5NodeRenderer nodeRenderer;
	private final P5EdgeRenderer edgeRenderer;

	// Overlay UI
	private Overlay overlay;
	
	// Default fonts
	private final PFont DEF_FONT = createFont("SansSerif", 30);
	
	
	// pan and locations
	public float rotY, rotX, zoom = 100;
	
	// For extreme rendering mode (currently not in use)
	private ParticleManager particleManager;
	private VerletPhysics physics;
	private AABB boundingBox;
	
	//////////////////////////
	// Controls
	//////////////////////////
	
	// Turn on/off overlay windows
	private boolean isOverlay = false;
	
	// Stop/start loop
	private boolean freeze = false;
	
	// Switch rendering mode
	private boolean fastRendering = false;
	
	private boolean twoDMode = false;
	
	// Network Visuals
	private Color bgColor;

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Dimension size, CyNetworkView view, CyDrawableManager manager) {
		this.manager = manager;
		this.windowSize = size;
		this.view = view;
		System.out.println("%%%%%%%%%%%%% Constructor called for P5: 2");
		this.nodeRenderer = new P5NodeRenderer(this, manager);
		this.edgeRenderer = new P5EdgeRenderer(this, manager, view);

		this.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent e) {
				if(freeze) return;
				
				zoom += e.getWheelRotation() * 100;

			}

		});
		
		manager.setFactoryParent(this);
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
	PMatrix startMatrix;
	
	public void setup() {
		System.out.println("%%%%%%%%%%%%% Setup called for P5");
		/* setup p5 */
		size(windowSize.width, windowSize.width, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		hint(ENABLE_NATIVE_FONTS);
		noStroke();
		frameRate(FRAME_RATE);

		// Particle simulator
		physics = new VerletPhysics();
		physics.friction = 1000;
		AABB boundingBox = new AABB(new Vec3D(0, 0, 0), new Vec3D(width,
				height, height));
		physics.worldBounds = boundingBox;

		// Convert views to drawable
		final List<View<CyNode>> nodeViews = view.getNodeViews();
		nodes = new CyDrawable[nodeViews.size()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = nodeRenderer.render(nodeViews.get(i));

		final List<View<CyEdge>> edgeViews = view.getEdgeViews();
		edges = new CyDrawable[edgeViews.size()];
		for (int i = 0; i < edges.length; i++)
			edges[i] = edgeRenderer.render(edgeViews.get(i));
		
		renderNetworkVisualProperties();

		numP = nodes.length;
		particleManager = new ParticleManager(numP, this, physics);

		// Create Overlay Windows
		overlay = new Overlay(this, view.getSource().attrs().get(NAME, String.class));
		
		System.out.println("%%%%%%%%%%%%% Setup DONE for P5");

	}

	private void renderNetworkVisualProperties() {
		bgColor = (Color) view.getVisualProperty(NETWORK_BACKGROUND_COLOR);
		if(bgColor == null)
			bgColor = Color.green;
	}
	
	private int numP;

	float rotXDelta = 0;
	public void draw() {
		background(bgColor.getRed(), bgColor.getGreen(), bgColor.getGreen());
		lights();

		camera(width / 2.0f, height / 2.0f, (height / 2.0f)
				/ tan((float) (PI * 60.0 / 360.0)) + zoom, width / 2.0f,
				height / 2.0f, 0, 0, 1, 0);
		
		translate(width / 2+ translateX, height / 2+ translateY, height / 2);
		rotateX(rotX);
		rotateY(rotY);
		
		translate(-width / 2 + translateX, -height / 2 + translateY,
				-height / 2);

		textFont(DEF_FONT);
		
		
		for (CyDrawable edge : edges)
			edge.draw();
		
		for (CyDrawable node : nodes)
			node.draw();

		

		// Reser camera and draw overlay
		camera();
		beginGL();
		gl.glClear(javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT);
		endGL();
		
		// 2D OpenGL UI
		if(isOverlay)
			overlay.draw();

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
		if(freeze) return;
		
		if (mouseButton == RIGHT && twoDMode == false) {
			rotY += (mouseX - pmouseX) * 0.01;
			rotX -= (mouseY - pmouseY) * 0.01;
		} else if (mouseButton == LEFT) {
			translateX += (mouseX - pmouseX) * 2;
			translateY += (mouseY - pmouseY) * 2;
		}
	}

	public void mousePressed() {
		if (mouseButton != CENTER)
			return;

		// System.out.println("===Mouse Click");
		// for (int i = 0; i < nodes.length; i++) {
		// if (nodes[i] instanceof Pickable) {
		// ((Pickable) nodes[i]).pick(mouseX, mouseY);
		// }
		// }
	}

	
	private boolean captureFlag = false;
	@Override
	public void keyPressed(){
	  // if the key is between 'A'(65) and 'z'(122)
	  if( key == 'o')
		  isOverlay = !isOverlay;
	  else if( key == 'f') {
		  // freeze
		  freeze = !freeze;
		  if(freeze) {
			  fill(100, 100, 100, 100);
			  rect(0, 0, width, height);
			  noLoop();
		  } else
			  loop();
		  
	  } else if( key == 'c') {
		  saveFrame("capture-####.png");
	  } else if( key == 'r') {
		  fastRendering = !fastRendering;
		  for (int i = 0; i < nodes.length; i++)
				nodes[i].setDetailFlag(fastRendering);
	  } else if( key == 'l') {
		  twoDMode = !twoDMode;
		  if(twoDMode) {
			  System.out.println("2D mode");
			 rotY = 0;
			 rotX = 0;
		  } else {
			  System.out.println("3D mode");
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
	
	public PFont getDefaultFont(){
		return DEF_FONT;
	}

	public void handleEvent(VisualStyleChangedEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void handleEvent(VisualStyleSwitchedEvent e) {
		// TODO Auto-generated method stub
		
	}

}
