package org.cytoscape.cytobridge.json;

import java.util.Vector;

import org.cytoscape.cytobridge.NetworkManager;

public class JSONNetworkTable implements JSONCommand {
	
	private String network_name;
	private Vector<String> table_headings;
	private Vector<String> table_data;
	
		  
		   
	public void run(NetworkManager netMan) {
		 netMan.pushNetTable(network_name, table_headings, table_data);
	}

}
