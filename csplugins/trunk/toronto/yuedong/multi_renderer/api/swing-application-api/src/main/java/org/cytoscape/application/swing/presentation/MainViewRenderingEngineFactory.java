package org.cytoscape.application.swing.presentation;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;

/**
 * This interface represents a {@link RenderingEngineFactory} used specifically
 * for generating {@link RenderingEngine} objects for the main network viewing
 * window.
 */
public interface MainViewRenderingEngineFactory extends
		RenderingEngineFactory<CyNetwork> {
}
