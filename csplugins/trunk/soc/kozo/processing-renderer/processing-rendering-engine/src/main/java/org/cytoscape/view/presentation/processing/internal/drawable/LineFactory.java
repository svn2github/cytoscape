package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class LineFactory extends AbstractCyDrawableFactory<Line> {
	
	public LineFactory() {
		this(null);
	}
	
	public LineFactory(PApplet p) {
		super(p);
		this.type = Line.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new Line(p);
	}
}