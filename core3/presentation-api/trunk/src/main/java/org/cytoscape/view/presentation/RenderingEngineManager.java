package org.cytoscape.view.presentation;

import java.util.Collection;

import org.cytoscape.view.model.View;

/**
 * Manager for {@linkplain RenderingEngine} objects. All RenderingEngine objects
 * created by {@linkplain RenderingEngineFactory} should be registered to this
 * manager.
 * <P>
 * Register/unregister engines are handled through
 * {@linkplain RenderingEngineAddedEvent}s.
 * 
 */
public interface RenderingEngineManager {

	/**
	 * Get all rendering engines for the given view model.
	 *
	 * @param viewModel
	 *            View model for the presentation.
	 * 
	 * @return collection of rendering engines (presentations) for the given
	 *         view model.
	 */
	Collection<RenderingEngine<?>> getRendringEngines(final View<?> viewModel);
	
	/**
	 * Get all {@link RenderingEngine}s registered in this manager.
	 * 
	 * @return all rendering engines.  
	 */
	Collection<RenderingEngine<?>> getAllRenderingEngines();
	
	
	/**
	 * Add new {@link RenderingEngine} to this manager.
	 * <p>
	 * This method fires {@link VisualStyleCreatedEvent}.
	 * 
	 * @param engine New engine to be added.
	 */
	void addRenderingEngine(final RenderingEngine<?> engine);
	
	
	/**
	 * Remove a rendering engine.
	 * 
	 * <p>
	 * This method fires {@link VisualStyleAboutToBeRemovedEvent}.
	 * 
	 * @param engine engine to be removed.
	 */
	void removeRenderingEngine(final RenderingEngine<?> engine);
}
