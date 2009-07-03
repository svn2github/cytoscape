package org.cytoscape.view.presentation.processing.internal.shape;

import gestalt.shape.AbstractShape;

import org.cytoscape.view.presentation.processing.ObjectShape;

public abstract class AbstractObjectShape extends AbstractShape implements ObjectShape {
	
	
	// Human readable name of this shape.  This is mandatory and immutable.
	protected String displayName;
	
	public AbstractObjectShape() {
		
	}
	
	
}
