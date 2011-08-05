package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.work.TaskIterator;

// TODO: Wire this in OSGi
public class GetParentInteractionsTaskFactory extends AbstractNodeViewTaskFactory {
	private final CPath2Factory factory;

	public GetParentInteractionsTaskFactory(CPath2Factory factory) {
		this.factory = factory;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		CyNetwork network = netView.getModel();
		CyNode node = nodeView.getModel();
		return new TaskIterator(new GetParentInteractions(network, node, factory));
	}	
}
