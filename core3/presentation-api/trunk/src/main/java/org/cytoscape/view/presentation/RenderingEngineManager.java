package org.cytoscape.view.presentation;

import java.util.Collection;

import org.cytoscape.view.model.View;

/**
 * Manager for {@linkplain RenderingEngine} objects. All RenderingEngine objects
 * created by {@linkplain RenderingEngineFactory} should be registered to this
 * manager.
 * <P>
 * Register/unregister engines are handled through
 * {@linkplain RenderingEngineCreatedEvent}s.
 * 
 * 
 * @author kono
 * 
 */
public interface RenderingEngineManager {

	/**
	 * Get all rendering engines for the given view model.
	 * 
	 * @param <T>
	 *            Data model type. Usually they are {@linkplain CyNode},
	 *            {@linkplain CyEdge}, {@linkplain CyNetwork}, or
	 *            {@linkplain CyDataTable}.
	 * @param viewModel
	 *            View model for the presentation.
	 * 
	 * @return collection of rendering engines (presentations) for the given
	 *         view model.
	 */
	<T> Collection<RenderingEngine<T>> getRendringEngines(
			final View<T> viewModel);
}
