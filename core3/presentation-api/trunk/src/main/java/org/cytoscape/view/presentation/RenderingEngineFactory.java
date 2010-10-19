package org.cytoscape.view.presentation;

import org.cytoscape.view.model.View;

/**
 * A factory class to create visualization for a given view model. One
 * visualization (presentation) have one rendering engine
 * 
 * @author kono
 * 
 * @param <T>
 *            Compatible data model for this factory. For example, if this
 *            parameter is set to CyNetwork, the factory creates rendering
 *            engine for CyNetwork objects.
 */
public interface RenderingEngineFactory<T> {

	/**
	 * A presentation can contain multiple view models. This enable developers
	 * to render multiple View Models in the same display. For example, if
	 * View<CyNetwork> and View<Decoration> are passed to this, both of them
	 * will be rendered in a window, using same rendering engine.
	 * 
	 * @param visualizationContainer
	 *            Window component which contains the rendered view. In most
	 *            cases, {@linkplain Window} components in Swing will be used.
	 * @param viewModel
	 *            view-model to be rendered by the RenderingEngine.
	 * 
	 * @return Rendering Engine for visualization on the visualizationContainer.
	 */
	public RenderingEngine<T> render(final Object visualizationContainer,
			final View<T> viewModel);

}
