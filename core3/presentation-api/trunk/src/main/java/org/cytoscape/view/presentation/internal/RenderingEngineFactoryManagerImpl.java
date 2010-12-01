package org.cytoscape.view.presentation.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineFactoryManager;
import org.cytoscape.view.presentation.events.RenderingEngineFactoryAddedEvent;

public class RenderingEngineFactoryManagerImpl implements
		RenderingEngineFactoryManager {

	private static final String FACTORY_ID_TAG = "id";

	private final Map<String, RenderingEngineFactory<?>> factoryMap;
	
	private final CyEventHelper eventHelper;

	public RenderingEngineFactoryManagerImpl(final CyEventHelper eventHelper) {
		factoryMap = new HashMap<String, RenderingEngineFactory<?>>();
		this.eventHelper = eventHelper;
	}

	@Override
	public Collection<RenderingEngineFactory<?>> getAllRenderingEngineFactories() {
		return factoryMap.values();
	}

	@Override
	public RenderingEngineFactory<?> getRenderingEngine(String factoryID) {
		return this.factoryMap.get(factoryID);
	}

	public void addRenderingEngineFactory(
			final RenderingEngineFactory<?> factory, Map metadata) {
		final Object idObject = metadata.get(FACTORY_ID_TAG);

		if (idObject == null)
			throw new IllegalArgumentException(
					"Could not add factory: ID metadata is missing for RenderingEngineFactory.");

		final String id = idObject.toString();

		this.factoryMap.put(id, factory);
		
		eventHelper.fireSynchronousEvent(new RenderingEngineFactoryAddedEvent(this, factory));
		
		System.out.println("\n\nAdding Rendering Engine Factory: " + id);
	}

	public void removeRenderingEngineFactory(
			final RenderingEngineFactory<?> factory, Map metadata) {
		final Object idObject = metadata.get(FACTORY_ID_TAG);

		if (idObject == null)
			throw new IllegalArgumentException(
					"Could not remove factory: ID metadata is missing for RenderingEngineFactory.");

		final String id = idObject.toString();

		RenderingEngineFactory<?> toBeRemoved = this.factoryMap.remove(id);

		toBeRemoved = null;

	}

}
