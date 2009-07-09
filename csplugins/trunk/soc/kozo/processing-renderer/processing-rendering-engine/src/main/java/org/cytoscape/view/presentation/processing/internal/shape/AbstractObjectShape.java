package org.cytoscape.view.presentation.processing.internal.shape;

import gestalt.shape.AbstractShape;

import org.cytoscape.view.presentation.processing.P5Shape;

public abstract class AbstractObjectShape extends AbstractShape implements P5Shape {
	
	
	// Human readable name of this shape.  This is mandatory and immutable.
	protected String displayName;
	
	public AbstractObjectShape() {
		
	}
	
	
}
