package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.events.PresentationCreatedEvent;
import org.cytoscape.view.presentation.events.PresentationCreatedEventListener;
import org.cytoscape.view.presentation.events.PresentationDestroyedEvent;
import org.cytoscape.view.presentation.events.PresentationDestroyedEventListener;

public class RenderingEngineManagerImpl implements RenderingEngineManager,
		PresentationCreatedEventListener, PresentationDestroyedEventListener {

	private final Map<View<?>, Set<RenderingEngine<?>>> renderingEngineMap;

	/**
	 * Create an instance of rendering engine manager. This implementation
	 * listens to Presentation events and update its map based on them.
	 */
	public RenderingEngineManagerImpl() {
		this.renderingEngineMap = new HashMap<View<?>, Set<RenderingEngine<?>>>();
	}

	@Override
	public Collection<RenderingEngine<?>> getRendringEngines(
			final View<?> viewModel) {
		return renderingEngineMap.get(viewModel);
	}

	@Override
	public void handleEvent(PresentationCreatedEvent e) {
		final RenderingEngine<?> renderingEngine = e.getSource();
		if (renderingEngine == null)
			return;

		Set<RenderingEngine<?>> engines = renderingEngineMap
				.get(renderingEngine.getViewModel());
		if (engines == null)
			engines = new HashSet<RenderingEngine<?>>();

		engines.add(renderingEngine);
		this.renderingEngineMap.put(renderingEngine.getViewModel(), engines);

	}

	@Override
	public void handleEvent(PresentationDestroyedEvent e) {
		final RenderingEngine<?> renderingEngine = e.getSource();
		if (renderingEngine == null)
			return;

		this.renderingEngineMap.remove(renderingEngine.getViewModel());

	}

}
