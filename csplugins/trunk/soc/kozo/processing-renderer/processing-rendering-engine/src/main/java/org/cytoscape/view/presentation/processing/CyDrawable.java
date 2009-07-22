package org.cytoscape.view.presentation.processing;

import java.util.List;
import java.util.Set;

public interface CyDrawable {
	
	/**
	 * Draw this object on canvas.
	 * 
	 */
	public void draw();
	
	/**
	 * Get all children of this drawable.
	 * These visual objects will be painted automatically if draw() is called.
	 * 
	 * @return
	 */
	public List<CyDrawable> getChildren();

	/**
	 * Get compatible data type. For example, if this object accepts CyNode,
	 * return the collection with CyNode class.
	 * 
	 * @return set of compatible data types.
	 */
	public Set<Class<?>> getCompatibleModels();

}
