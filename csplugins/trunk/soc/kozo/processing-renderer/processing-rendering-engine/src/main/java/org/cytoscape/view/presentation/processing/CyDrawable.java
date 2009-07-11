package org.cytoscape.view.presentation.processing;

import gestalt.render.Drawable;

import java.util.Collection;

import javax.swing.Icon;

public interface CyDrawable extends Drawable {
	
	/**
	 * Name of this object.  Can be anything.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();
	
	/**
	 * Get compatible data type.
	 * For example, if this object accepts CyNode, return the collection with CyNode class.
	 * 
	 * @return set of compatible data types.
	 */
	public Collection<Class<?>> getCompatibleModels();
	
	// Render Icon based on current state
	public Icon getIcon(int width, int height);

}
