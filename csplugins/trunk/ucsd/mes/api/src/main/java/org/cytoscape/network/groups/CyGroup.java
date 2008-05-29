
package org.cytoscape.network.groups;

import org.cytoscape.network.CyNode;
import org.cytoscape.network.CyEdge;
import org.cytoscape.network.CyNetwork;
import java.util.List; 

/** 
 * Inner and outer nodes make sense to me because
 * the outer edges will need to connect to a node
 * and that node will need to be part of the network.
 */
public interface CyGroup extends CyNetwork {

	public List<CyNode> getInnerNodes();
	public List<CyNode> getOuterNodes();

	public List<CyEdge> getInnerEdges();
	public List<CyEdge> getOuterEdges();
}
