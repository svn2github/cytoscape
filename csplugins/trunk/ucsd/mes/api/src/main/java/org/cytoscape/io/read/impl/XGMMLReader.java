package org.cytoscape.io.read.impl;


import java.io.InputStream;
import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTable;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyDataTableReader;

public class XGMMLReader implements CyNetworkReader, CyDataTableReader {

	public void setInput(InputStream is) {
	}

	public void read() throws IOException {
	}

	public List<CyNetwork> getReadNetworks() {
		return null;
	}

	public List<CyDataTable> getReadDataTables() {
		return null;
	}
}
