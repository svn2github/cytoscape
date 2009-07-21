package org.cytoscape.view.presentation;

import org.cytoscape.view.model.View;

public interface PresentationFactory {
	/**
	 * A presentation can contain multiple view models. This enable developers
	 * to render multiple View Models in the same display. For example, if
	 * View<CyNetwork> and View<Decoration> are passed to this, both of them
	 * will be rendered in a window, using same rendering engine.
	 */
	public RenderingEngine addPresentation(Object container, View<?> viewModel);

	/**
	 * This method should be removed.
	 * 
	 * @param targetComponent
	 * @param navBounds
	 * @return
	 */
	public NavigationPresentation addNavigationPresentation(
			Object targetComponent, Object navBounds);
}
