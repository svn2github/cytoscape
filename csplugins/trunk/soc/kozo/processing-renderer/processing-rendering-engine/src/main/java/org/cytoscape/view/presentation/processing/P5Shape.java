package org.cytoscape.view.presentation.processing;

import java.util.Collection;

/**
 * 
 * Defines shape of object rendered in Processing.
 * 
 * @author kono, kozo
 * @version 0.0.1
 * 
 * 
 */
public interface P5Shape {

	/**
	 * Name of this shape, such as ellipse, rectangle, triangle, etc. This is
	 * immutable.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();
	
	public Collection<?> getCompatibleModels();
	
}
