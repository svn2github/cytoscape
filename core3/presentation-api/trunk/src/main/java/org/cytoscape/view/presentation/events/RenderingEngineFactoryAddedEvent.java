package org.cytoscape.view.presentation.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineFactoryManager;

public final class RenderingEngineFactoryAddedEvent extends
		AbstractCyEvent<RenderingEngineFactoryManager> {

	private final RenderingEngineFactory<?> factory;

	/**
	 * Create an event for newly created presentation.
	 * 
	 * @param source manager holding reference to this new rendering engine
	 * @param engine New {@linkplain RenderingEngine} just created.
	 */
	public RenderingEngineFactoryAddedEvent(final RenderingEngineFactoryManager source,
			final RenderingEngineFactory<?> factory) {
		super(source, RenderingEngineAddedListener.class);
		this.factory = factory;
	}

	/**
	 * Get new rendering engine.
	 * 
	 * @return new rendering engine.
	 */
	public RenderingEngineFactory<?> getRenderingEngineFactory() {
		return this.factory;
	}

}
