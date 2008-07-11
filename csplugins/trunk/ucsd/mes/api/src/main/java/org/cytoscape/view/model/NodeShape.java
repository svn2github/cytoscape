package org.cytoscape.view.model; 

import java.awt.Shape;


/**
 * Defines the shape of a Node. NodeShape is 
 * presumed to be an OSGi Service which will then be made
 * available through the OSGi ServiceRegistry.
 */
public interface NodeShape extends Saveable {

	/**
	 * This is where we'd need define the constraints
	 * on the shape. 
	 */
	public Shape getShape();
}

