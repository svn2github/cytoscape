package org.cytoscape.view.model;

import java.awt.Shape;

/**
 * Defines the shape of an edge Arrow. ArrowShape is 
 * presumed to be an OSGi Service which will then be made
 * available through the OSGi ServiceRegistry.
 */
public interface ArrowShape extends Saveable {

	/**
	 * This is where we'd need define the constraints
	 * on the shape and how exactly we'd transform the 
	 * shape specified into an arrow that lines up with
	 * the edge stroke and node shapes. 
	 */
	public Shape getShape();
}

