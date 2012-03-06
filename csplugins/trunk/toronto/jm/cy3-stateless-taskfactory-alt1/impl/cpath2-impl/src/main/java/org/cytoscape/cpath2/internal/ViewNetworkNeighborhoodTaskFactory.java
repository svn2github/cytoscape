package org.cytoscape.cpath2.internal;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.SimpleNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class ViewNetworkNeighborhoodTaskFactory extends SimpleNodeViewTaskFactory {
	// TODO: Wire this up
	
	// TODO: This should be a service property
    private static final String CONTEXT_MENU_TITLE = "View network neighborhood map";

	public ViewNetworkNeighborhoodTaskFactory() {
	}
	
	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		return new TaskIterator(new ViewNetworkNeighborhoodTask(nodeView, networkView));
	}
}
