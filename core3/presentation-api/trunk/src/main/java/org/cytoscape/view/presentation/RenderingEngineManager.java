package org.cytoscape.view.presentation;

import java.util.Collection;

import org.cytoscape.view.model.View;

/**
 * Manager for {@linkplain RenderingEngine} objects. All RenderingEngine objects
 * created by {@linkplain RenderingEngineFactory} should be registered to this
 * manager.
 * <P>
 * Register/unregister engines are handled through
 * {@linkplain PresentationCreatedEvent}s.
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
}
