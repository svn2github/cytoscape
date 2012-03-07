package org.cytoscape.webservice.internal.task;

import org.cytoscape.model.CyEdge;
import org.cytoscape.task.SimpleEdgeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class WebServiceEdgeContextMenuTaskFactory extends SimpleEdgeViewTaskFactory {
	
	WebServiceEdgeContextMenuTaskFactory() {
		
	}

	@Override
	public TaskIterator createTaskIterator(View<CyEdge> edgeView, CyNetworkView networkView) {
		return new TaskIterator(new WebServiceContextMenuTask<CyEdge>(edgeView));
	}

}
