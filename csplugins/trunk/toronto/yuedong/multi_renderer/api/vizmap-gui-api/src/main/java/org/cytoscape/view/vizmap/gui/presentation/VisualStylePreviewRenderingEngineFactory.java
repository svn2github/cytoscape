package org.cytoscape.view.vizmap.gui.presentation;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;

/**
 * This interface represents a {@link RenderingEngineFactory} used specifically
 * for generating {@link RenderingEngine} objects for rendering
 * the visual style preview.
 */
public interface VisualStylePreviewRenderingEngineFactory extends
		RenderingEngineFactory<CyNetwork> {
}
