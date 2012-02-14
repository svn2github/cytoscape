package org.cytoscape.biopax.internal.action;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.task.NodeViewTaskContext;
import org.cytoscape.task.NodeViewTaskContextImpl;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.work.TaskIterator;

public class DisplayBioPaxXmlTaskFactory implements NodeViewTaskFactory<NodeViewTaskContext> {
	private CySwingApplication cySwingApplication;

	public DisplayBioPaxXmlTaskFactory(CySwingApplication cySwingApplication) {
		this.cySwingApplication = cySwingApplication;
	}
	
	@Override
	public TaskIterator createTaskIterator(NodeViewTaskContext context) {
		return new TaskIterator(new DisplayBioPaxXmlTask(context.getNodeView(), context.getNetworkView(), cySwingApplication));
	}
	
	@Override
	public NodeViewTaskContext createTaskContext() {
		return new NodeViewTaskContextImpl();
	}
}
