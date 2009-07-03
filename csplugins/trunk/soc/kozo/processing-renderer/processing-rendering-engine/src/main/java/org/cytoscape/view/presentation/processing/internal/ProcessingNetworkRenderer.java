package org.cytoscape.view.presentation.processing.internal;

import gestalt.Gestalt;
import gestalt.candidates.shadow.JoglShadowMap;
import gestalt.candidates.shadow.JoglShadowMapDisplay;
import gestalt.context.GLContext;
import gestalt.impl.jogl.shape.JoglSphere;

import gestalt.p5.GestaltPlugIn;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;
import gestalt.shape.Cube;
import gestalt.shape.Plane;
import gestalt.util.CameraMover;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.print.Printable;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
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

	private Dimension windowSize;
	private CyNetworkView view;
	private CyNetwork network;

	private PGraphicsOpenGL pgl;
	private GL gl;

	// Gestalt plugin
	private GestaltPlugIn gestalt;

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

	ParticleManager particleManager;
	VerletPhysics physics;
	float rotX, rotY, zoom = 200;
	AABB boundingBox;

	private Cube cube1;

	private JoglShadowMap _myShadowMapExtension;

	private JoglSphere mySphereA;

	private JoglSphere mySphereB;

	private JoglSphere mySphereC;

	private float _myCounter = 0;

	public void setup() {
		size(windowSize.width, windowSize.height, OPENGL);
		hint(ENABLE_OPENGL_4X_SMOOTH);
		frameRate(30);
		smooth();
		physics = new VerletPhysics();
		physics.friction = 1000;
		AABB boundingBox = new AABB(new Vec3D(0, 0, 0), new Vec3D(width,
				height, height));
		physics.worldBounds = boundingBox;
		particleManager = new ParticleManager(1000, this, physics);

		/* create gestalt plugin */
		gestalt = new GestaltPlugIn(this);

		/* setup shadow map */
		final int myShadowMapWidth = width;
		final int myShadowMapHeight = height;
		_myShadowMapExtension = new JoglShadowMap(gestalt.light(),
				myShadowMapWidth, myShadowMapHeight, true, false);
		gestalt.bin(Gestalt.BIN_FRAME_SETUP).add(_myShadowMapExtension);

		/*
		 * this is a workaround for a state issue between openl, processing and
		 * gestalt
		 */
		GestaltPlugIn.SKIP_FIRST_FRAME = true;

		/* create shapes and a floor */
		mySphereA = new JoglSphere();
		mySphereA.position().set(100, 50, -100);
		mySphereA.scale().set(100, 100, 100);
		mySphereA.material().lit = true;
		mySphereA.material().color.set(0.5f, 0.5f, 0, 1);

		mySphereB = new JoglSphere();
		mySphereB.position().set(0, 100, 0);
		mySphereB.scale().set(100, 100, 100);
		mySphereB.material().lit = true;
		mySphereB.material().color.set(1, 0, 0, 1);

		mySphereC = new JoglSphere();
		mySphereC.position().set(100, 350, 0);
		mySphereC.scale().set(100, 100, 100);
		mySphereC.material().lit = true;
		mySphereC.material().color.set(1, 0.5f, 0, 1);

		Plane myPlane = gestalt.drawablefactory().plane();
		myPlane.scale().set(1000, 1000, 1);
		myPlane.rotation().x = -Gestalt.PI_HALF;
		myPlane.material().lit = true;
		myPlane.material().color.set(1, 1);

		/* add shapes to bins */
		gestalt.bin(Gestalt.BIN_3D).add(mySphereA);
		gestalt.bin(Gestalt.BIN_3D).add(mySphereB);
		gestalt.bin(Gestalt.BIN_3D).add(mySphereC);
		gestalt.bin(Gestalt.BIN_3D).add(myPlane);

		/* add shapes to shadow extension */
		_myShadowMapExtension.addShape(mySphereA);
		_myShadowMapExtension.addShape(mySphereB);
		_myShadowMapExtension.addShape(mySphereC);
		_myShadowMapExtension.lightcamera.nearclipping = 100;
		_myShadowMapExtension.lightcamera.farclipping = 5000;

		/* light */
		gestalt.light().enable = true;
		gestalt.light().position().set(450, 720, 23);
		gestalt.light().diffuse.set(1, 1, 1, 1);
		gestalt.light().ambient.set(0, 0, 0, 1);

		/* camera() */
		gestalt.camera().position().set(-400, 1000, 1000);
		gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);

		/* create a display for the shadowmap */
		createShadowmapDisplay();

		// /* remove all gestalt presets */
		// RenderBin myRenderBin = new RenderBin();
		// gestalt.setBinRef(myRenderBin);
		// myRenderBin.add(new RawOpenGL());

	}

	private void createShadowmapDisplay() {
		JoglShadowMapDisplay myDisplay = new JoglShadowMapDisplay(
				_myShadowMapExtension, width, height);
		myDisplay.scale().scale(0.25f);
		myDisplay.position().x = myDisplay.scale().x / 2;
		myDisplay.position().y = myDisplay.scale().y / 2;
		myDisplay.material().color.a = 0.75f;
		myDisplay.material().depthtest = false;
		gestalt.bin(Gestalt.BIN_2D_FOREGROUND).add(myDisplay);
	}

	public void draw() {
		
		

		/* draw processing shape */
		// rect(gestalt.event().mouseX, gestalt.event().mouseY, 50, 150);
		/* clear screen */
		background(255, 255, 0);
		gl = gestalt.getGL();
		gl.glViewport(0, 0, width, height);

		/* move camera */
		CameraMover.handleKeyEvent(gestalt.camera(), gestalt.event(), 1 / 60f);

		/* bounce spheres */
		_myCounter += 1 / 60f;
		mySphereA.position().y += sin(_myCounter * 1.5f) * 2;
		mySphereB.position().y += sin(_myCounter * 2.3f) * 4;
		mySphereC.position().y += sin(_myCounter * 2.5f) * 3;
		
		
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

	public CyNetwork getSourceNetwork() {
		return network;
	}

	public CyNetworkView getSourceView() {
		return view;
	}

	private class RawOpenGL extends AbstractDrawable {

		public void draw(final GLContext theContext) {
			GL gl = gestalt.getGL();
			GLU glu = gestalt.getGLU();

			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			glu
					.gluPerspective(45.0f, (float) width / (float) height, 1.0,
							20.0);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();

			gl.glTranslatef(-1.5f, 0.0f, -6.0f);
			gl.glBegin(GL.GL_TRIANGLES);
			gl.glColor3f(1.0f, 0.0f, 0.0f);
			gl.glVertex3f(0.0f, 1.0f, 0.0f);
			gl.glColor3f(0.0f, 1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glColor3f(0.0f, 0.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glEnd();

			gl.glTranslatef(3.0f, 0.0f, 0.0f);
			gl.glBegin(GL.GL_QUADS);
			gl.glColor3f(0.5f, 0.5f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glEnd();
		}
	}

}
