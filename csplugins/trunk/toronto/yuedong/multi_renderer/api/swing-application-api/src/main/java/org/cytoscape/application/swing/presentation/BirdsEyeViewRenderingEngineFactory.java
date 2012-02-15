package org.cytoscape.application.swing.presentation;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;

/**
 * This interface represents a {@link RenderingEngineFactory} used specifically
 * for generating {@link RenderingEngine} objects for the bird's eye view.
 */
public interface BirdsEyeViewRenderingEngineFactory extends
		RenderingEngineFactory<CyNetwork> {
}
