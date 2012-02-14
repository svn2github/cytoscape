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
	 * An enumeration encoding rendering purposes that may be intended for certain {@link RenderingEngine} objects. For example,
	 * this could be used to differentiate between a {@link RenderingEngine} that renders for the bird's eye view versus
	 * a {@link RenderingEngine} that is designed for the main network view.
	 */
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
	/**
	 * Return the {@link CyNetworkViewFactory} used for creating {@link CyNetworkView} objects for the given rendering purpose.
	 */
	CyNetworkViewFactory getNetworkViewFactory(RenderPurpose renderPurpose);
	
	// Allow returning different VisualLexicons; a bird's eye RenderingEngine may not need some visual properties of the main view.
	/**
	 * Return the {@link VisualLexicon} for this renderer associated with the given rendering purpose.
	 */
	VisualLexicon getVisualLexicon(RenderPurpose renderPurpose);
	
	// Prepares the renderer to be removed.
	/**
	 * Notifies this renderer to release all its resources and prepare for removal. The renderer is allowed to become unuseable after this call,
	 * and should at least fail safely if asked to render anything else onwards.
	 */
	public void dispose();
	
	// TODO: Determine how RenderingEngine instances will be obtained: via returning RenderingEngineFactory objects or directly
	// creating RenderingEngine objects
	
	
}
