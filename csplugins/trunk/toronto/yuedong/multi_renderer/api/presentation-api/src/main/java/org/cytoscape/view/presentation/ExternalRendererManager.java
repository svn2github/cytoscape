package org.cytoscape.view.presentation;

import java.util.List;

/**
 * Draft manager class for installed {@link ExternalRenderer} or Cytoscape renderers.
 */
public interface ExternalRendererManager {
	
	/**
	 * Adds a renderer to this manager.
	 * @param externalRenderer
	 */
	public void addRenderer(ExternalRenderer renderer);
	
	public void removeRenderer(ExternalRenderer renderer);
	
	public List<String> getInstalledRenderers();
	
	public void getRenderer(String rendererID);
	
	public void setDefaultRenderer(String rendererID);
	
	public void getDefaultRenderer();	
}
