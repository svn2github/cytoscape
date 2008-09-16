package org.cytoscape.io.read.impl;


import java.net.URI;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTable;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyDataTableReader;

public class XGMMLReader implements CyNetworkReader, CyDataTableReader {

	public void setInput(URI u) {
	}

	public void read() {
	}

	public List<CyNetwork> getReadNetworks() {
		return null;
	}

	public List<CyDataTable> getReadDataTables() {
		return null;
	}
}
