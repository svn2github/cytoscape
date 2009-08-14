package org.cytoscape.view.presentation.processing;

import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public interface CyDrawable {
	
	/**
	 * Draw this object on canvas.
	 * 
	 */
	public void draw();
	
	/**
	 * Set all visual properties 
	 * 
	 * @param viewModel
	 */
	public void setContext(View<?> viewModel);
	
	/**
	 * Set only specified visual property value.
	 * 
	 * @param viewModel
	 * @param vp
	 */
	public void setContext(View<?> viewModel, VisualProperty<?> vp);
	
	/**
	 * Get all children of this drawable.
	 * These visual objects will be painted automatically if draw() is called.
	 * 
	 * @return
	 */
	public List<CyDrawable> getChildren();
	
	public void addChild(CyDrawable child);

	/**
	 * Get compatible data type. For example, if this object accepts CyNode,
	 * return the collection with CyNode class.
	 * 
	 * @return set of compatible data types.
	 */
	public Set<Class<?>> getCompatibleModels();
	
	public void setDetailFlag(boolean flag);

}
