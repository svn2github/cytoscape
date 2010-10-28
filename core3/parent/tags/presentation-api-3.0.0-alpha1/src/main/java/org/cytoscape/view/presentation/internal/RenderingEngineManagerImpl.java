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
import org.cytoscape.view.presentation.events.PresentationCreatedListener;
import org.cytoscape.view.presentation.events.PresentationDestroyedEvent;
import org.cytoscape.view.presentation.events.PresentationDestroyedListener;

public class RenderingEngineManagerImpl implements RenderingEngineManager,
		PresentationCreatedListener, PresentationDestroyedListener {

	private final Map<View<?>, Set<RenderingEngine<?>>> renderingEngineMap;

	/**
	 * Create an instance of rendering engine manager. This implementation
	 * listens to Presentation events and update its map based on them.
	 */
	public RenderingEngineManagerImpl() {
		this.renderingEngineMap = new HashMap<View<?>, Set<RenderingEngine<?>>>();
	}

	/**
	 * This method never returns null.
	 */
	@Override
	public Collection<RenderingEngine<?>> getRendringEngines(final View<?> viewModel) {
		Collection<RenderingEngine<?>> engines = renderingEngineMap.get(viewModel);
		if(engines == null)
			engines = new HashSet<RenderingEngine<?>>();
		
		return engines;
	}

	@Override
	public void handleEvent(PresentationCreatedEvent e) {
		// This cannot be null.
		final RenderingEngine<?> renderingEngine = e.getRenderingEngine();

		final View<?> viewModel = renderingEngine.getViewModel();
		Set<RenderingEngine<?>> engines = renderingEngineMap
				.get(viewModel);
		if (engines == null)
			engines = new HashSet<RenderingEngine<?>>();

		engines.add(renderingEngine);
		this.renderingEngineMap.put(viewModel, engines);
	}

	@Override
	public void handleEvent(PresentationDestroyedEvent e) {
		// This cannot be null.
		final RenderingEngine<?> renderingEngine = e.getRenderingEngine();

		final View<?> viewModel = renderingEngine.getViewModel();
		final Set<RenderingEngine<?>> engineSet = renderingEngineMap.get(viewModel);
		
		engineSet.remove(renderingEngine);
		this.renderingEngineMap.put(viewModel, engineSet);
	}

}
