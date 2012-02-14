package org.cytoscape.view.presentation;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

/**
 * Draft version of top-level ExternalRenderer class that represents a Cytoscape renderer, and should
 * correspondingly have one instance per installed renderer.
 */
public interface ExternalRenderer {
	
	/**
	 * Returns a human-readable form of the renderer's name
	 * 
	 * @return The renderer's name, in human-readable form
	 */
	String getRendererName();
	
	/**
	 * Returns a {@link RenderingEngineFactory} of the given type, if available.
	 * 
	 * @param <T> The type of the {@link RenderingEngineFactory}
	 * @param type The {@link Class} object containing the type of the desired {@link RenderingEngineFactory}
	 * @return A {@link RenderingEngineFactory} with the desired type if available, <code>null</code> otherwise.
	 */
	<T extends RenderingEngineFactory<?>> T getRenderingEngineFactory(Class<T> type);
	
	/**
	 * Returns the {@link CyNetworkViewFactory} for this renderer.
	 * 
	 * @return The {@link CyNetworkViewFactory} used by this renderer.
	 */
	CyNetworkViewFactory getNetworkViewFactory();
	
	/**
	 * Returns the {@link VisualLexicon} for this renderer.
	 * 
	 * @return The {@link VisualLexicon} used by this renderer.
	 */
	VisualLexicon getVisualLexicon();
}
