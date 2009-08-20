package org.cytoscape.view.presentation.processing.internal.drawable;

import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class SphereFactory extends AbstractCyDrawableFactory<Sphere> {
	
	public SphereFactory() {
		this(null);
	}

	public SphereFactory(PApplet p) {
		super(p);
		// This is required
		this.type = Sphere.class;
	}
	
	public CyDrawable getInstance() {
		if(this.p == null)
			throw new IllegalStateException("Could not locate parent PApplet (Processing main window) object.");
		
		return new Sphere(p);
	}
}