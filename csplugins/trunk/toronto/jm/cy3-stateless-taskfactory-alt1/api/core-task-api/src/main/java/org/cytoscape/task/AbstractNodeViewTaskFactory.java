package org.cytoscape.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

public abstract class AbstractNodeViewTaskFactory implements NodeViewTaskFactory {
	@Override
	public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView) {
		return true;
	}
}
