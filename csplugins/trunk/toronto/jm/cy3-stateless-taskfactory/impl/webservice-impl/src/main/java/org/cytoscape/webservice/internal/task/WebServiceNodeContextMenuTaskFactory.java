package org.cytoscape.webservice.internal.task;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.task.NodeViewTaskContext;
import org.cytoscape.work.TaskIterator;

public class WebServiceNodeContextMenuTaskFactory extends AbstractNodeViewTaskFactory {
	
	WebServiceNodeContextMenuTaskFactory() {
		
	}

	@Override
	public TaskIterator createTaskIterator(NodeViewTaskContext context) {
		return new TaskIterator(new WebServiceContextMenuTask<CyNode>(context.getNodeView()));
	}

}
