package org.cytoscape.task;

import org.cytoscape.view.model.CyNetworkView;

public interface NetworkViewTaskContext {
	/** Provides this class and its descendants with the network view that will be passed into
	 *  any tasks created by descendants of this class.
	 *  @param view  must be a non-null network view
	 */
	void setNetworkView(final CyNetworkView view);

	CyNetworkView getNetworkView();
}
