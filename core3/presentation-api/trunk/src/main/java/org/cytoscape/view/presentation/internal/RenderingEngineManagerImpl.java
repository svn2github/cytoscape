package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineManager;

public class RenderingEngineManagerImpl implements RenderingEngineManager {

	private final Map<View<?>, RenderingEngine<?>> renderingEngineMap;

	/**
	 * Create an instance of rendering engine manager. This implementation
	 * listens to Presentation events and update its map based on them.
	 */
	public RenderingEngineManagerImpl() {
		this.renderingEngineMap = new HashMap<View<?>, RenderingEngine<?>>();
	}

	/**
	 * This method never returns null.
	 */
	@Override
	public RenderingEngine<?> getRendringEngine(final View<?> viewModel) {
		return renderingEngineMap.get(viewModel);
	}

	@Override
	public Collection<RenderingEngine<?>> getAllRenderingEngines() {
		return renderingEngineMap.values();
	}
	

	@Override
	public void addRenderingEngine(final RenderingEngine<?> renderingEngine) {

		System.out.println("##Adding Engine 0: ");
		final View<?> viewModel = renderingEngine.getViewModel();
		this.renderingEngineMap.put(viewModel, renderingEngine);
	}
	

	@Override
	public void removeRenderingEngine(RenderingEngine<?> renderingEngine) {

		final View<?> viewModel = renderingEngine.getViewModel();
		this.renderingEngineMap.remove(viewModel);
	}

}
