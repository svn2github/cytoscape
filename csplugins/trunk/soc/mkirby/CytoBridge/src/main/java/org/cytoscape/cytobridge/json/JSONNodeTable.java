package org.cytoscape.cytobridge.json;

import java.util.Vector;

import org.cytoscape.cytobridge.NetworkManager;

public class JSONNodeTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<Integer> node_cytobridge_ids;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushNodeTable(network_name, table_headings, node_cytobridge_ids, table_data);
	}

}