package org.cytoscape.ding.impl; 


import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskIterator;


public class AddEdgeNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory<AddEdgeNodeViewTaskContext> {
	private final CyNetworkManager netMgr;

	public AddEdgeNodeViewTaskFactoryImpl(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public TaskIterator createTaskIterator(AddEdgeNodeViewTaskContext context) {
		return new TaskIterator(new AddEdgeTask(context, AddEdgeStateMonitor.getTransferable(context.getNetworkView())));
	}
	
	@Override
	public AddEdgeNodeViewTaskContext createTaskContext() {
		return new AddEdgeNodeViewTaskContext();
	}
}
