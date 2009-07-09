package org.cytoscape.view.presentation.processing;

import gestalt.render.Drawable;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.Renderer;

/**
 * Base interface for all renderers.  This will be used by:
 * <ul>
 *  <li>Node Renderer</li>
 *  <li>Edge Renderer</li>
 *  <li>Network Renderer</li>
 *  <li>Decoration Renderer</li>
 * </ul>
 * 
 * @author kono
 *
 * @param <T>
 */
public interface P5Presentation <T> extends Renderer {
	
	/**
	 * Gte backend data structure for this presentation.
	 * 
	 * @return View Model of this object.
	 * 
	 */
	public View<T> getViewModel();
	
	/**
	 * Set the View Model.
	 * TODO: Is this necessary?  (Immutable?)
	 * 
	 * @param model
	 */
	public void setViewModel(View<T> model);
	
	/**
	 * Get top-level Drawable Object.
	 * 
	 * @return
	 */
	public Drawable getDrawable();
	
	/**
	 * Set top-level Drawable Object.
	 * 
	 * @param drawable
	 */
	public void setDrawable(Drawable drawable);
	
}
