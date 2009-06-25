package org.cytoscape.view.presentation.processing.internal;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.NetworkRenderer;

import processing.core.PApplet;
import processing.core.PFont;

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

	public void setup() {
		size(windowSize.width, windowSize.height, MODE);
		hint(ENABLE_NATIVE_FONTS);
		frameRate(FRAME_RATE);
		// hint(ENABLE_OPENGL_4X_SMOOTH);
		noStroke();
		textFont(defaultFont);

	}

	float op = 0;

	public void draw() {

		background(0, 0, 0);
		fill(200, 0, 0, 200);
		text("Cytoscape Presentation", 100, 200);

	}

	public CyNetwork getSourceNetwork() {
		return network;
	}

	public CyNetworkView getSourceView() {
		return view;
	}

}
