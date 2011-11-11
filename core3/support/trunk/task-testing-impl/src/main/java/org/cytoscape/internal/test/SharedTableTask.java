
package org.cytoscape.internal.test;


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.CyNetwork;

public class SharedTableTask extends AbstractNetworkTask {
	
	@Tunable(description="new column name")
	public String colName;

	private CyRootNetworkFactory rnf;

	public SharedTableTask(CyRootNetworkFactory rnf, CyNetwork net) {
		super(net);
		this.rnf = rnf;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		CyRootNetwork root = rnf.convert(network);	
		root.getSharedNodeTable().createColumn(colName,String.class,false);
	}
}
