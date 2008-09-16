package org.cytoscape.model;

import java.util.List;
import java.util.Map;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;

public interface CyNetwork extends Identifiable, GraphObject {

	public CyNode addNode();
	public boolean removeNode(CyNode node);

	public CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);
	public boolean removeEdge(CyEdge edge);

	public int getNodeCount();
	public int getEdgeCount();

	public List<CyNode> getNodeList();
	public List<CyEdge> getEdgeList();

	public boolean containsNode( CyNode node );
	public boolean containsEdge( CyEdge edge );
	public boolean containsEdge( CyNode from, CyNode to );

	public CyNode getNode(int index);
	public CyEdge getEdge(int index);

	public List<CyNode> getNeighborList( CyNode node, CyEdge.Type edgeType );
	public List<CyEdge> getAdjacentEdgeList( CyNode node, CyEdge.Type edgeType );
	public List<CyEdge> getConnectingEdgeList( CyNode source, CyNode target, CyEdge.Type edgeType );

	/**
	 * Defines the attributes available for the CyNetwork.
	 */
	public Map<String,? extends CyDataTable> getNetworkCyDataTables();

	/**
	 * Defines the attributes available for all of the CyNode objects in the CyNetwork. 
	 */
	public Map<String,? extends CyDataTable> getNodeCyDataTables();

	/**
	 * Defines the attributes available for all of the CyEdge objects in the CyNetwork. 
	 */
	public Map<String,? extends CyDataTable> getEdgeCyDataTables();
}
