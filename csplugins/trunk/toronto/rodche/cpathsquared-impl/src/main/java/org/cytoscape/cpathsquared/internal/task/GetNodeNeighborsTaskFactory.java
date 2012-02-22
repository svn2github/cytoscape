package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.work.TaskIterator;

// TODO: a lot of things (re-factoring), including - wire this in OSGi
public class GetNodeNeighborsTaskFactory extends AbstractNodeViewTaskFactory {
	private final CPath2Factory factory;

	public GetNodeNeighborsTaskFactory(CPath2Factory factory) {
		this.factory = factory;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		CyNetwork network = netView.getModel();
		CyNode node = nodeView.getModel();
		return new TaskIterator(new GetNodeNeighborsTask(network, node, factory));
	}	
}
