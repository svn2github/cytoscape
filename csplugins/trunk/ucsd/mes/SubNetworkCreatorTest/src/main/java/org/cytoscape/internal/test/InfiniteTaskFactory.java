
package org.cytoscape.internal.test;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.model.subnetwork.*;


public class InfiniteTaskFactory extends AbstractNetworkTaskFactory {
	CyRootNetworkManager rootMgr;
	public InfiniteTaskFactory(CyRootNetworkManager rootMgr) { 
		this.rootMgr = rootMgr;
	}

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new InfiniteTask(network,rootMgr));
	}
}
