
package org.cytoscape.internal.test;


import org.cytoscape.work.*;
import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.*;
import java.util.*; 


public class InfiniteTask extends AbstractTask {

	private CyNetwork net;
	private CyRootNetworkManager rootMgr;
	public InfiniteTask(CyNetwork net, CyRootNetworkManager rootMgr) {
		this.net = net;
		this.rootMgr = rootMgr;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		CyRootNetwork root = rootMgr.getRootNetwork(net);
		int numNodes = net.getNodeList().size();
		int i = 0;
		for ( CyNode n : net.getNodeList() ) {
			if ( cancelled )
				return;
			List<CyNode> nl = net.getNeighborList(n,CyEdge.Type.ANY);
			Set<CyEdge> es = new HashSet<CyEdge>();
			for ( CyNode nn : nl ) {
				List<CyEdge> ee = net.getConnectingEdgeList(n,nn,CyEdge.Type.ANY);
				es.addAll(ee);
			}

			root.addSubNetwork(nl,es);
			taskMonitor.setProgress( (double)i++/(double)numNodes);
		}
	}
}
