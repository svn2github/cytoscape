package org.cytoscape.task;

import org.cytoscape.model.CyNetwork;

public class NetworkTaskContextImpl implements NetworkTaskContext {

    /**
     * Network to be passed into any task constructed by descendants of this class.
     */
    protected CyNetwork network;

    @Override
    public void setNetwork(final CyNetwork network) {
	if (network == null)
	    throw new NullPointerException("CyNetwork object is null.");
	this.network = network;
    }

    @Override
	public CyNetwork getNetwork() {
		return network;
	}
}
