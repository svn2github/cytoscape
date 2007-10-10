package org.cytoscape.io.export;

public interface CyNetworkWriter {

	public void write(OutputStream os, CyNetwork net, CyNetworkView view, CyAttributes cyAttr);
}

