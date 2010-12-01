package org.cytoscape.view.presentation.events;

import org.cytoscape.event.CyListener;

public interface RenderingEngineFactoryAddedListener extends CyListener {

	/**
	 * Listener can extract source RenderingEngine object in this method. This
	 * is mainly for {@linkplain RenderingEngineManager}.
	 * 
	 * @param e
	 *            an event object which contains source RenderingEngine.
	 */
	void handleEvent(final RenderingEngineFactoryAddedEvent e);
}
