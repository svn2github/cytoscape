package org.cytoscape.cytobridge;

import org.cytoscape.model.CyNetwork;

public class NetworkSync {
	
	private CyNetwork network;
	
	public NetworkSync(CyNetwork net) {
		this.network = net;
	}
	
	public CyNetwork getNetwork() {
		return network;
	}

}
