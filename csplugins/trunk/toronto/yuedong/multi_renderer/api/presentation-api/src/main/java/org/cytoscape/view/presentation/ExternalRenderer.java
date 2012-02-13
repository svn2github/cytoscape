package org.cytoscape.view.presentation;

/**
 * Draft version of top-level ExternalRenderer class that represents a Cytoscape renderer, and should
 * correspondingly have one instance per installed renderer.
 */
public interface ExternalRenderer {

	public String getRendererID();
	
	// TODO: Determine how RenderingEngine instances will be obtained: via returning RenderingEngineFactory objects or directly
	// creating RenderingEngine objects
}
