package org.cytoscape.view.presentation;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

public interface VisualItemRenderer<T extends View<?>> {
	/**
	 * Provide a set of Visual Properties this renderer can visualize.
	 *
	 * @return Set of VP as a VisualLexicon
	 */
	public VisualLexicon getVisualLexicon();
}
