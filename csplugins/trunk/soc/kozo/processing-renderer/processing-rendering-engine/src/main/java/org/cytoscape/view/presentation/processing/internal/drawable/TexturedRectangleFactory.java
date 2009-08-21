package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class TexturedRectangleFactory extends AbstractCyDrawableFactory<TexturedRectangle> {
	
	public TexturedRectangleFactory() {
		this(null);
	}

	public TexturedRectangleFactory(PApplet p) {
		super(p);
		// This is required
		this.type = TexturedRectangle.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new TexturedRectangle(p);
	}
}