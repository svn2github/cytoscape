package org.cytoscape.view.model.events;

import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.view.model.CyNetworkView;

/**
 * Fit network visualization to the given container.
 * 
 * @author kono
 *
 */
public final class FitContentEvent extends AbstractCyEvent<CyNetworkView> {
	
	/**
	 * Fit the size of network view to the current container.
	 * 
	 * @param source network view to be fitted to the container.
	 */
	public FitContentEvent(final CyNetworkView source) {
		super(source, FitContentEventListener.class);
	}
}