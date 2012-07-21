package org.cytoscape.cytobridge.json;

import java.util.Vector;

import org.cytoscape.cytobridge.NetworkManager;

public class JSONEdgeTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<Integer> edge_cytobridge_ids;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushEdgeTable(network_name, table_headings, edge_cytobridge_ids, table_data);
	}

}