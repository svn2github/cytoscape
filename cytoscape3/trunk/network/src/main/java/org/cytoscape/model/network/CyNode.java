
package org.cytoscape.model.network;

import java.util.List;

public interface CyNode extends GraphObject { 
	public int getIndex();

	// I still don't think these should be here.
	public List<CyNode> getNeighborList( EdgeType edgeType );
	public List<CyEdge> getAdjacentEdgeList( EdgeType edgeType );
	public List<CyEdge> getConnectingEdgeList( CyNode target, EdgeType edgeType );
}
