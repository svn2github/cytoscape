package org.cytoscape.view.presentation;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;

/**
 * Draft version of top-level ExternalRenderer class that represents a Cytoscape renderer, and should
 * correspondingly have one instance per installed renderer.
 */
public interface ExternalRenderer {
	
	String rendererID = null;
	
	enum RenderPurpose {
		BIRDS_EYE_VIEW,
		DETAIL_VIEW,
		VISUAL_STYLE_PREVIEW
	}
	
	/**
	 * Return the renderer's ID. This ID is used to distinguish between different renderers.
	 * 
	 * @return The renderer's ID
	 */
	public String getRendererID();
	
	/**
	 * Return a {@link RenderingEngineFactory} object to be used for creating {@link RenderingEngine} objects with the given rendering purpose,
	 * such as bird's eye view or detail view.
	 */
	RenderingEngineFactory<?> getRenderingEngineFactory(RenderPurpose renderPurpose);
	
	// Allow returning different CyNetworkView factories. This could be helpful for efficiency. For example, a bird's eye RenderingEngine may not
	// need some visual properties of the main view.
	CyNetworkViewFactory getNetworkViewFactory(RenderPurpose renderPurpose);
	
	// Allow returning different VisualLexicons; a bird's eye RenderingEngine may not need some visual properties of the main view.
	VisualLexicon getVisualLexicon(RenderPurpose renderPurpose);
	
	
	// TODO: Determine how RenderingEngine instances will be obtained: via returning RenderingEngineFactory objects or directly
	// creating RenderingEngine objects
	
	
}
