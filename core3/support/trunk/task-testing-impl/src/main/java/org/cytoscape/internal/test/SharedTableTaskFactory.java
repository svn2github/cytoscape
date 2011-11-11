
package org.cytoscape.internal.test;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;


public class SharedTableTaskFactory extends AbstractNetworkTaskFactory {
	CyRootNetworkFactory rnf;

	public SharedTableTaskFactory(CyRootNetworkFactory rnf) { 
		super();
		this.rnf = rnf;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SharedTableTask(rnf,network)); 
	}
}
