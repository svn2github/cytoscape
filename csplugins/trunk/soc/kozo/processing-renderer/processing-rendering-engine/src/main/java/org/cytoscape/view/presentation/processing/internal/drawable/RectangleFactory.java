package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class RectangleFactory extends AbstractCyDrawableFactory<Rectangle> {
	
	public RectangleFactory() {
		this(null);
	}

	public RectangleFactory(PApplet p) {
		super(p);
		// This is required
		this.type = Rectangle.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new Rectangle(p);
	}
}