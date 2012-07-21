package org.cytoscape.cytobridge.json;

import java.util.Vector;

import org.cytoscape.cytobridge.NetworkManager;

public class JSONNetwork implements JSONCommand {
	
	public String network_name;
	public Vector<Integer> node_cytobridge_ids;
	public Vector<Integer> edge_cytobridge_ids;
	public Vector<Integer> edge_source_cytobridge_ids;
	public Vector<Integer> edge_target_cytobridge_ids;
	
	
	public void run(NetworkManager netMan) {
		netMan.pushNetwork(network_name, node_cytobridge_ids, edge_cytobridge_ids, edge_source_cytobridge_ids, edge_target_cytobridge_ids);
	}

}
