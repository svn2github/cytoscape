package org.cytoscape.phylotree.parser;

import java.util.List;

public interface Phylotree {

	/**
	 *  Return the List of Node names
	 */
	public List<PhylotreeNode> getNodeList();
		
	/**
	 *  Return the List of Edges for the given node
	 */
	public List<PhylotreeEdge> getEdges(PhylotreeNode pNode);

	
	/**
	 *  Return the Edges attributes
	 *  @param layout DOCUMENT ME!
	 *  @return  List of Edges (Node pairs)
	 */	
	public List<Object> getEdgeAttribute(PhylotreeEdge pEdge);
	
}
