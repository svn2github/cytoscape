package org.cytoscape.view.presentation.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * When {@linkplain RenderingEngineFactory} creates a new presentation (
 * {@linkplain RenderingEngine}), this event should be fired.
 * 
 * @author kono
 * 
 */
public class RenderingEngineCreatedEvent extends AbstractCyEvent<RenderingEngine<?>> {

	/**
	 * Create a new event for the new RenderingEngine object.
	 * 
	 * @param engine newly created RenderingEngine object.
	 */
	public RenderingEngineCreatedEvent(RenderingEngine<?> engine) {
		super(engine, RenderingEngineCreatedEventListener.class);
	}

}
