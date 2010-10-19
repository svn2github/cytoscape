package org.cytoscape.view.presentation.events;

import org.cytoscape.event.CyListener;

/**
 * Listener for {@linkplain RenderingEngineCreatedEvent}.
 * {@linkplain RenderingEngineManager} implementation should implement this
 * interface, too.
 * 
 * @author kono
 * 
 */
public interface RenderingEngineCreatedEventListener extends CyListener {

	/**
	 * Listener can extract source RenderingEngine object in this method. This
	 * is mainly for {@linkplain RenderingEngineManager}.
	 * 
	 * @param e
	 *            an event object which contains source RenderingEngine.
	 */
	void handleEvent(final RenderingEngineCreatedEvent e);
}
