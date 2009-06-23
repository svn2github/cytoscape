package org.cytoscape.view.presentation.processing.internal;

import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.NetworkRenderer;

import processing.core.PApplet;

public class ProcessingNetworkRenderer extends PApplet implements NetworkRenderer {

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
	
	// Used for oveall rotation
	float ang;

	// Cube count-lower/raise to test P3D/OPENGL performance
	int limit = 500;
	private int windowSize;


	public ProcessingNetworkRenderer(int size) {
		this.windowSize = size;
	}

	public void setup() {
		size(windowSize, windowSize);
//		hint(ENABLE_OPENGL_4X_SMOOTH);
		noStroke();
	}

	public void draw() {
		background(15);
		fill(250, 250, 250, 100);

		this.rect(10, 10, 100, 100);
	}

}
