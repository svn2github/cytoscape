
package org.mike;

import java.util.List;


/**
 * DOCUMENT ME!
  */
public interface CyNode {
	int getIndex();

	List<CyNode> getNeighborList(EdgeType edgeType);

	List<CyEdge> getAdjacentEdgeList(EdgeType edgeType);

	List<CyEdge> getConnectingEdgeList(CyNode target, EdgeType edgeType);
}
