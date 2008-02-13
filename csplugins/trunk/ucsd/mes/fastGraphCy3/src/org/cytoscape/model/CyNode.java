
package org.cytoscape.model;

import java.util.List;

public interface CyNode extends GraphObject { 
	public int getIndex();

	// I still don't think these should be here.
	public List<CyNode> getNeighborList( EdgeType edgeType );
	public List<? extends CyBaseEdge> getAdjacentEdgeList( EdgeType edgeType );
	public List<? extends CyBaseEdge> getConnectingEdgeList( CyNode target, EdgeType edgeType );
}
