package org.cytoscape.cpath2.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskContext;
import org.cytoscape.task.NodeViewTaskContextImpl;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class ViewNetworkNeighborhoodTaskFactory implements NodeViewTaskFactory<NodeViewTaskContext> {
	// TODO: Wire this up
	
	// TODO: This should be a service property
    private static final String CONTEXT_MENU_TITLE = "View network neighborhood map";

	public ViewNetworkNeighborhoodTaskFactory() {
	}
	
	@Override
	public NodeViewTaskContext createTaskContext() {
		return new NodeViewTaskContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(NodeViewTaskContext context) {
		return new TaskIterator(new ViewNetworkNeighborhoodTask(context.getNodeView(), context.getNetworkView()));
	}
}
