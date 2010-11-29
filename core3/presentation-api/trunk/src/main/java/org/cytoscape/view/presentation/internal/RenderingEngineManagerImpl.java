package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;

public class RenderingEngineManagerImpl implements RenderingEngineManager {

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
	public Collection<RenderingEngine<?>> getAllRenderingEngines() {
		final Set<RenderingEngine<?>> engines = new HashSet<RenderingEngine<?>>();
		
		for(View<?> key:renderingEngineMap.keySet())
			engines.addAll(renderingEngineMap.get(key));

		return engines;
	}
	

	@Override
	public void addRenderingEngine(final RenderingEngine<?> renderingEngine) {

		System.out.println("##Adding Engine 0: ");
		final View<?> viewModel = renderingEngine.getViewModel();
		Set<RenderingEngine<?>> engines = renderingEngineMap.get(viewModel);
		if (engines == null)
			engines = new HashSet<RenderingEngine<?>>();

		
		engines.add(renderingEngine);
		System.out.println("##Adding Engine: " + engines.size());
		
		this.renderingEngineMap.put(viewModel, engines);
	}
	

	@Override
	public void removeRenderingEngine(RenderingEngine<?> renderingEngine) {

		final View<?> viewModel = renderingEngine.getViewModel();
		final Set<RenderingEngine<?>> engineSet = renderingEngineMap.get(viewModel);
		
		engineSet.remove(renderingEngine);
		this.renderingEngineMap.put(viewModel, engineSet);
	}

}
