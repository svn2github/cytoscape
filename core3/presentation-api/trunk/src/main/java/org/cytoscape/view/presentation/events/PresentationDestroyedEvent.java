package org.cytoscape.view.presentation.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * When presentation (rendered graphics) is destroyed, this event should be
 * fired.
 * 
 */
public final class PresentationDestroyedEvent extends AbstractCyEvent<Object> {

	private final RenderingEngine<?> engine;

	/**
	 * Construct an event for destroyed {@linkplain RenderingEngine}.
	 * 
	 * @param source
	 *            source of event. In theory, this can be anything, but in most
	 *            cases, it will be an GUI components.
	 * @param engine
	 *            {@linkplain RenderingEngine} associated with the deleted
	 *            presentation.
	 */
	public PresentationDestroyedEvent(final Object source, final RenderingEngine<?> engine) {
		super(source, PresentationDestroyedListener.class);
		this.engine = engine;
	}

	public RenderingEngine<?> getRenderingEngine() {
		return this.engine;
	}

}
