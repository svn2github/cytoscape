package org.cytoscape.webservice.internal.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.SimpleNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class WebServiceNodeContextMenuTaskFactory extends SimpleNodeViewTaskFactory {
	
	WebServiceNodeContextMenuTaskFactory() {
		
	}

	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		return new TaskIterator(new WebServiceContextMenuTask<CyNode>(nodeView));
	}

}
