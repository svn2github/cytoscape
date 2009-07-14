package org.cytoscape.view.presentation.processing;

import java.awt.Component;

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
public interface P5Renderer <T> extends Renderer<View<T>> {
	
	/**
	 * Get top-level Drawable Object.
	 * 
	 * @return Gestalt's Drawable object with Cytoscape dependent properties.
	 */
	public CyDrawable getCyDrawable();
	
	public Component getComponent();
}
