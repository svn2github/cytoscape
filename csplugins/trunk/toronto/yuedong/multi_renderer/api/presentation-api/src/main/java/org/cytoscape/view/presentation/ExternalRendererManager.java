package org.cytoscape.view.presentation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Draft manager class for installed {@link ExternalRenderer} or Cytoscape renderers.
 */
public interface ExternalRendererManager {
	
	/**
	 * Adds a renderer to this manager. There can be only one {@link ExternalRenderer} of a given type
	 * registered to this manager.
	 * 
	 * @param ExternalRenderer The renderer to be added.
	 */
	void addRenderer(ExternalRenderer externalRenderer, Map<String, String> properties);
	
	/**
	 * Removes a renderer from this manager.
	 * 
	 * @param externalRenderer The renderer to be removed.
	 */
	void removeRenderer(ExternalRenderer externalRenderer, Map<String, String> properties);
	
	/**
	 * Removes an {@link ExternalRenderer} based on its class type which identifies that renderer.
	 */
	void removeRenderer(Class<? extends ExternalRenderer> rendererType);
	
	/**
	 * Return a collection of currently installed renderers.
	 * 
	 * @return A {@link Collection} of available renderers.
	 */ 
	Collection<ExternalRendererManager> getAvailableRenderers();
	
	/**
	 * Returns the {@link ExternalRenderer} associated with the given renderer type, if there is one.
	 * 
	 * @return The {@link ExternalRenderer} associated with the given renderer type, or <code>null</code> if there is none.
	 */
	ExternalRenderer getRenderer(Class<? extends ExternalRenderer> rendererType);
	
	/**
	 * Sets the current renderer.
	 * 
	 * @param rendererType The class type of the renderer desired to be set as the current renderer.
	 */
	void setCurrentRenderer(Class<? extends ExternalRenderer> rendererType);
	
	/**
	 * Returns the current {@link ExternalRenderer}. If there is only one renderer available, that
	 * renderer is returned as current.
	 * 
	 * @return The current renderer, or <code>null</code> if none are available.
	 */
	ExternalRenderer getCurrentRenderer();
}
