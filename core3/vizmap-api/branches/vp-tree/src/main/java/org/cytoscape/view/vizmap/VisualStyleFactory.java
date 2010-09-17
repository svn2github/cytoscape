package org.cytoscape.view.vizmap;

import org.cytoscape.view.presentation.RenderingEngine;

public interface VisualStyleFactory {
	
	/**
	 * Create a new Visual Style.
	 * 
	 * @param title Title of the visual style.  This can be null, but in that case, 
	 * 					default title will be used.
	 * 			Note: This is NOT an identifier of this object, just a title.
	 *
	 * @return New Visual Style
	 */
	VisualStyle createVisualStyle(final String title, final RenderingEngine<?> engine);
	
	
	/**
	 * Create a copy of given Visual Style.
	 *
	 * @param original
	 *            VS to be copied from.
	 *
	 * @return Copied VS
	 */
	VisualStyle createVisualStyle(final VisualStyle original);

}
