package org.cytoscape.task;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public interface EdgeViewTaskContext {
	/** Used to provision this factory with the edge view and associated network view that will
	 *  be used to create tasks.
	 *  @param edgeView  a non-null edge view
	 *  @param netView   a non-null network view associated with the edge view
	 */
	void setEdgeView(View<CyEdge> edgeView, CyNetworkView netView);

	View<CyEdge> getEdgeView();

	CyNetworkView getNetworkView();
}
