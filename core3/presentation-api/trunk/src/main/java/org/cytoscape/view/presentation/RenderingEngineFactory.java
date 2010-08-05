package org.cytoscape.view.presentation;

import org.cytoscape.view.model.View;

public interface RenderingEngineFactory<T> {
	/**
	 * A presentation can contain multiple view models. This enable developers
	 * to render multiple View Models in the same display. For example, if
	 * View<CyNetwork> and View<Decoration> are passed to this, both of them
	 * will be rendered in a window, using same rendering engine.
	 */
	public RenderingEngine<T> render(final Object visualizationContainer, final View<T> viewModel);

}
