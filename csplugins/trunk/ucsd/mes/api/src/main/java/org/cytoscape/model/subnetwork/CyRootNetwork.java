package org.cytoscape.model.subnetwork;

import java.util.List;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

/**
 *
 */
public interface CyRootNetwork extends CyNetwork {

	/**
	 *
	 */
	public List<CyNode> getAllNodes();

	/**
	 *
	 */
	public List<CyEdge> getAllEdges();

	/**
	 *
	 */
	public CyMetaNode createMetaNode( List<CyNode> nodes );

	/**
	 * Removes the metanode and the CySubNetwork, not the nodes contained in the subnetwork.
	 */
	public void removeMetaNode( CyMetaNode node ); 

	/**
	 *
	 */
	public List<CySubNetwork> getAllSubNetworks();
}

