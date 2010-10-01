package org.cytoscape.io.write;

import java.io.IOException;

import org.cytoscape.io.FileIOFactory;
import org.cytoscape.view.model.CyNetworkView;

/**
 *
 */
public interface CyNetworkViewWriterFactory extends CyWriterFactory {
	void setNetworkView(CyNetworkView view);
}
