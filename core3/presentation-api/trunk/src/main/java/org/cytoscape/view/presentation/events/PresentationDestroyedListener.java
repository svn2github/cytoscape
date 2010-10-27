package org.cytoscape.view.presentation.events;

/**
 * Listener for destroyed presentations.
 *
 */
public interface PresentationDestroyedListener {
	
	/**
	 * Perform post-deletion tasks in this method.
	 * 
	 * @param evt event containing deleted presentation's RenderingEngine.
	 */
	void handleEvent(final PresentationDestroyedEvent evt);

}
