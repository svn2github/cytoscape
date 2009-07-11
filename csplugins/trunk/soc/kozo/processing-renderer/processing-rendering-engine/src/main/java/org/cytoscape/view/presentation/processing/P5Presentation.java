package org.cytoscape.view.presentation.processing;

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
	 * Get backend data structure for this presentation.
	 * 
	 * @return View Model of this object. 
	 */
	public View<T> getViewModel();
	
	
	/**
	 * Get top-level Drawable Object.
	 * 
	 * @return Gestalt's Drawable object with Cytoscape dependent properties.
	 */
	public CyDrawable getCyDrawable();
	
}
