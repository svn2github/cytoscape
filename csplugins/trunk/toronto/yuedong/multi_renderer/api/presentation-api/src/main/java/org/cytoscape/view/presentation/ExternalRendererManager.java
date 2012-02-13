package org.cytoscape.view.presentation;

import java.util.List;

/**
 * Draft manager class for installed {@link ExternalRenderer} or Cytoscape renderers.
 */
public interface ExternalRendererManager {
	
	/**
	 * Installs a renderer and adds it to this manager.
	 * 
	 * @param ExternalRenderer The renderer to be installed.
	 */
	public void installRenderer(ExternalRenderer externalRenderer);
	
	/**
	 * Uninstalls a renderer and removes it from this manager.
	 * 
	 * @param externalRenderer The renderer to be installed.
	 */
	public void uninstallRenderer(ExternalRenderer externalRenderer);
	
	/**
	 * Removes a renderer based on its renderer ID.
	 * 
	 * @param renderID The renderer ID of the renderer to be removed.
	 */
	public void uninstallRenderer(String rendererID);
	
	/**
	 * Return a defensively copied {@link List} containing the list of installed renderers' IDs.
	 * 
	 * @return A defensively copied list of the IDs of the currently installed renderers.
	 */
	public List<String> getInstalledRenderers();
	
	/**
	 * Returns the installed {@link ExternalRenderer} associated with the given renderer ID, if there is one.
	 * 
	 * @param rendererID The renderer ID of the desired renderer.
	 * @return The {@link ExternalRenderer} associated with the given renderer ID, or <code>null</code> if there is none.
	 */
	public ExternalRenderer getRenderer(String rendererID);
	
	/**
	 * Sets the ID used to look for the default renderer.
	 * 
	 * @param rendererID The ID of the renderer to be used as the default renderer.
	 * @return The ID of the new default renderer, or <code>null</code> if there is no new default or if no renderer was found with the given renderer ID.
	 */
	public String setDefaultRendererID(String rendererID);
	
	
	/**
	 * Return the default renderer's renderer ID.
	 * 
	 * @return The ID of the default renderer, or null if there are no renderers installed. If there is only 1 renderer installed, the ID of that renderer
	 * will be returned.
	 */
	public String getDefaultRendererID();	
}
