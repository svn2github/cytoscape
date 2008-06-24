package org.cytoscape.io.read.impl;


import java.net.URI;
import java.util.List;

import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.attrs.CyAttributes;

import org.cytoscape.io.read.CyNetworkReader;
import org.cytoscape.io.read.CyAttributesReader;

public class XGMMLReader implements CyNetworkReader, CyAttributesReader {

	public void setInput(URI u) {
	}

	public void read() {
	}

	public List<CyNetwork> getReadNetworks() {
		return null;
	}

	public List<CyAttributes> getReadAttributes() {
		return null;
	}
}
