package org.cytoscape.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public interface NodeViewTaskContext {

	/** Provisions descendants of this class with a node view and its associated network view
	 *  that will be used to pass to created tasks.
	 *  @param nodeView  a non-null node view
	 *  @param netView   the non-null network view associated with {@link #nodeView}
	 */
	void setNodeView(View<CyNode> nodeView, CyNetworkView netView);

	View<CyNode> getNodeView();

	CyNetworkView getNetworkView();

}
