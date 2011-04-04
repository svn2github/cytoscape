package org.cytoscape.biopax;

import org.cytoscape.model.CyNetwork;

/**
 * This API is provisional and is subject to change at any time.
 */
public interface NetworkListener {
	void registerNetwork(CyNetwork cyNetwork);
}
