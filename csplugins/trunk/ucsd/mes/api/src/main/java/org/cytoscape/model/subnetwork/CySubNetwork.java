package org.cytoscape.model.subnetwork;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;

/**
 *
 */
public interface CySubNetwork extends CyNetwork {
	
	/**
	 *
	 */
	public CyMetaNode getParentNode();
	
	/**
	 *
	 */
	public void copyToNetwork( CyNode node );
	
	/**
	 *
	 */
	public void removeFromNetwork( CyNode node );
}
