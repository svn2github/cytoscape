
package org.cytoscape.model;

import java.util.List;

public interface CyNode extends GraphObject { 
	public int getIndex();

	// I still don't think these should be here.
	public List<CyNode> getNeighborList( CyEdge.Type edgeType );
	public List<CyEdge> getAdjacentEdgeList( CyEdge.Type edgeType );
	public List<CyEdge> getConnectingEdgeList( CyNode target, CyEdge.Type edgeType );
}
