package org.cytoscape.io.write;

import java.io.IOException;

import org.cytoscape.io.FileIOFactory;
import org.cytoscape.model.CyNetwork;

/**
 *
 */
public interface CyNetworkWriterFactory extends CyWriterFactory {
	void setNetwork(CyNetwork net);
}
