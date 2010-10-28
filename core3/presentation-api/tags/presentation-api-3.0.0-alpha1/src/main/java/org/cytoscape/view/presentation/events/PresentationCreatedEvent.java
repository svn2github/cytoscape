package org.cytoscape.view.presentation.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;

/**
 * When {@linkplain RenderingEngineFactory} creates a new presentation (
 * {@linkplain RenderingEngine}), this event should be fired.
 * 
 */
public final class PresentationCreatedEvent extends
		AbstractCyEvent<RenderingEngineFactory<?>> {

	private final RenderingEngine<?> engine;

	/**
	 * Create an event for newly created presentation.
	 * 
	 * @param source factory taht creates the new engine.
	 * @param engine New {@linkplain RenderingEngine} just created.
	 */
	public PresentationCreatedEvent(final RenderingEngineFactory<?> source,
			final RenderingEngine<?> engine) {
		super(source, PresentationCreatedListener.class);
		this.engine = engine;

	}

	/**
	 * Get new rendering engine.
	 * 
	 * @return new rendering engine.
	 */
	public RenderingEngine<?> getRenderingEngine() {
		return this.engine;
	}

}
