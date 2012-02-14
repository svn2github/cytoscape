package org.cytoscape.task;

import org.cytoscape.view.model.CyNetworkView;

public class NetworkViewTaskContextImpl implements NetworkViewTaskContext {
	/** The network view that will be passed into any tasks are are being created by descendants of this class. */
	protected CyNetworkView networkView;

	@Override
	public void setNetworkView(final CyNetworkView view) {
		if ( view == null )
			throw new NullPointerException("CyNetworkView is null");
		networkView = view;
	}

	@Override
	public CyNetworkView getNetworkView() {
		return networkView;
	}
}
