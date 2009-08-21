package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class DataPlotRectangleFactory extends AbstractCyDrawableFactory<DataPlotRectangle> {
	
	public DataPlotRectangleFactory() {
		this(null);
	}

	public DataPlotRectangleFactory(PApplet p) {
		super(p);
		// This is required
		this.type = DataPlotRectangle.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new DataPlotRectangle(p);
	}
}