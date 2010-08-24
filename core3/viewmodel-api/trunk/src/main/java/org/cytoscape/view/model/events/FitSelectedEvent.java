package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;


/**
 * Fit only selected graph object views to the container.
 * 
 * @author kono
 *
 */
public class FitSelectedEvent extends AbstractCyEvent<CyNetworkView> {
	public FitSelectedEvent(final CyNetworkView source) {
		super(source, FitSelectedEventListener.class);
	}
}