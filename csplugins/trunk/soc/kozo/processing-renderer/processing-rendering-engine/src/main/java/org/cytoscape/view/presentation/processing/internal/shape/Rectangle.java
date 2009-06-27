package org.cytoscape.view.presentation.processing.internal.shape;

import processing.core.PApplet;

public class Rectangle extends Abstract2DObjectShape {
	
	private static final String RECTANGLE = "rectangle";
	
	public Rectangle(float x, float y, PApplet p) {
		super(x, y, p);
		this.displayName = RECTANGLE;
	}

	@Override
	public void draw() {
		
	}

}
