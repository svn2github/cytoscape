package org.cytoscape.view.presentation.events;

/**
 * Listener for destroyed presentations.
 *
 */
public interface PresentationDestroyedEventListener {
	
	/**
	 * Perform post-deletion tasks in this method.
	 * 
	 * @param evt event containing deleted presentation's RenderingEngine.
	 */
	void handleEvent(final PresentationDestroyedEvent evt);

}
