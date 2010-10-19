package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;


/**
 * Fit only selected graph object views to the container.
 *
 */
public class FitSelectedEvent extends AbstractCyEvent<CyNetworkView> {
	
	/**
	 * Fit the selected part of network view to the container.  Usually this fires event to the presentation payer for redraw.
	 * 
	 * @param source target network view for fitting.
	 */
	public FitSelectedEvent(final CyNetworkView source) {
		super(source, FitSelectedEventListener.class);
	}
}