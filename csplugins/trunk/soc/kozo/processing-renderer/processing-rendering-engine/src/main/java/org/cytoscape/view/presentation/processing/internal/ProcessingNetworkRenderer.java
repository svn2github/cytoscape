package org.cytoscape.view.presentation.processing.internal;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.p5.GestaltPlugIn;
import gestalt.render.Drawable;
import gestalt.render.bin.RenderBin;
import gestalt.shape.AbstractDrawable;
import gestalt.shape.Cube;
import gestalt.shape.Plane;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.print.Printable;
import java.util.List;
import java.util.Properties;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.Icon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Renderer;

import processing.core.PApplet;

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


	// Gestalt plugin
	private GestaltPlugIn gestalt;

	//
	GraphRenderer renderer;
	
	Drawable[] nodes; 

	/**
	 * Constructor. Create a PApplet component based on the size given as
	 * parameter.
	 * 
	 * @param size
	 */
	public ProcessingNetworkRenderer(Container parent, View<CyNetwork> view) {
		this.view = view;
		this.windowSize = parent.getSize();
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
	
	private Cube _myPlaneIn3D;

    private Plane _myPlaneIn2D;

    public void setup() {
        /* setup p5 */
        size(1900, 1000, OPENGL);
        rectMode(CENTER);
        stroke(122);

        gestalt = new GestaltPlugIn(this);
        gestalt.setDrawablefactoryRef(new CyDrawableFactoryImpl());
        
        
        nodes = new Drawable[view.getSource().getNodeCount()];
		CyNetwork net = view.getSource();
		List<CyNode> nodeList = net.getNodeList();
		int i = 0;
		Cube cube;
		for(CyNode node: nodeList) {
			cube = gestalt.drawablefactory().cube();
			cube.position().x = width/2+random(0, width);
	        cube.position().y = height/2+random(0, height);
	        cube.position().z = random(-1500, 1500);
	        cube.rotation(random(0, 10), random(0, 10), random(0, 10));
	        cube.scale(random(30, 100), random(30, 100), random(30, 100));
	        cube.material().getColor().set(random(0, 1), random(0, 1), random(0, 1), random(0, 1));
	        cube.material().lit = true;
//	        cube.material().wireframe = true;
	        gestalt.bin(Gestalt.BIN_3D).add(cube);
	        i++;
		}

        /* create planes */
        _myPlaneIn3D = gestalt.drawablefactory().cube();
        _myPlaneIn3D.scale().x = 50;
        _myPlaneIn3D.scale().y = 150;
        
        gestalt.bin(Gestalt.BIN_3D).add(_myPlaneIn3D);

        _myPlaneIn2D = gestalt.drawablefactory().plane();
        _myPlaneIn2D.scale().x = 50;
        _myPlaneIn2D.scale().y = 150;
       
        gestalt.bin(Gestalt.BIN_2D_FOREGROUND).add(_myPlaneIn2D);

        gestalt.light().enable = true;
        gestalt.light().position().set(width/2, height/2, 23);
        gestalt.light().diffuse.set(1, 1, 1, 1);
        gestalt.light().ambient.set(0, 0, 0, 1);
        
        /* camera() */
//        gestalt.camera().position().set(0, 0, 2000);
//        gestalt.camera().setMode(Gestalt.CAMERA_MODE_LOOK_AT);
    }


    public void draw() {
        /* glue gestalt shapes to mouse */
        _myPlaneIn3D.position().x = mouseX - 60;
        _myPlaneIn3D.position().y = mouseY;

        _myPlaneIn2D.position().x = mouseX + 60;
        _myPlaneIn2D.position().y = mouseY;
        
        /* clear screen */
        background(255, 128, 0);

        /* draw processing shape */
        //rect(gestalt.event().mouseX, gestalt.event().mouseY, 50, 150);
    }
	
//	public void setup() {
//		size(windowSize.width, windowSize.height, OPENGL);
////		hint(ENABLE_OPENGL_4X_SMOOTH);
////		frameRate(30);
////		smooth();
//
//		/* create gestalt plugin */
//		gestalt = new GestaltPlugIn(this);
//		
//		//Set Custom DrawableFactory
//		//gestalt.setDrawablefactoryRef(new CyDrawableFactoryImpl());
////		Cube cube = gestalt.drawablefactory().cube();
////		cube.scale().x = 50;
////	    cube.scale().y = 150;
//	    gestalt.bin(Gestalt.BIN_3D).add(new RawOpenGL());
//		System.out.println(" @@@@@@@@@@ Setup is OK.");
//		
//	
//	}
//
//	long i = 0;
//	public void draw() {
//		System.out.println("Loop: " + i++);
//      
//		background(0);
//	}
//


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
		return view;
	}

}
