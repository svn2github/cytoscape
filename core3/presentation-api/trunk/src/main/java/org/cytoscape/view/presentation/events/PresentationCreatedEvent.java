package org.cytoscape.view.presentation.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * When {@linkplain RenderingEngineFactory} creates a new presentation (
 * {@linkplain RenderingEngine}), this event should be fired.
 * 
 */
public final class PresentationCreatedEvent extends AbstractCyEvent<RenderingEngine<?>> {

	/**
	 * Create a new event for the new RenderingEngine object.
	 * 
	 * @param engine newly created RenderingEngine object.
	 */
	public PresentationCreatedEvent(RenderingEngine<?> engine) {
		super(engine, PresentationCreatedEventListener.class);
	}

}
