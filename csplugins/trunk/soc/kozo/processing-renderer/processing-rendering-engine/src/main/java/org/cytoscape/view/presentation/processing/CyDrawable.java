package org.cytoscape.view.presentation.processing;

import java.util.List;
import java.util.Set;

import javax.swing.Icon;

public interface CyDrawable {
	
	public void draw();
	
	public List<CyDrawable> getChildren();

	/**
	 * Name of this object. Can be anything.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();

	/**
	 * Get compatible data type. For example, if this object accepts CyNode,
	 * return the collection with CyNode class.
	 * 
	 * @return set of compatible data types.
	 */
	public Set<Class<?>> getCompatibleModels();

	// Render Icon based on current state
	public Icon getIcon(int width, int height);

}
