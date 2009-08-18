package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class CubeFactory extends AbstractCyDrawableFactory<Cube> {
	
	public CubeFactory() {
		this(null);
	}

	public CubeFactory(PApplet p) {
		super(p);
		// This is required
		this.type = Cube.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new Cube(p);
	}
}
