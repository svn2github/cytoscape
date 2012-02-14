package org.cytoscape.task;

import org.cytoscape.model.CyNetwork;

public interface NetworkTaskContext {

    /**
     * Provisions descendants of this factory class with the network for any
     * task to be constructed by them.
     * 
     * @param network the network to provision descendants of this factory class with.
     *            must be a non-null {@link CyNetwork}
     */
	void setNetwork(CyNetwork network);

	CyNetwork getNetwork();

}
