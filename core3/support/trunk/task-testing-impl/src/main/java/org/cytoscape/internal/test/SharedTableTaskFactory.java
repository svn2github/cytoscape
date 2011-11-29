
package org.cytoscape.internal.test;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;


public class SharedTableTaskFactory extends AbstractNetworkTaskFactory {
	CyRootNetworkManager rnf;

	public SharedTableTaskFactory(CyRootNetworkManager rnf) { 
		super();
		this.rnf = rnf;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SharedTableTask(rnf,network)); 
	}
}
