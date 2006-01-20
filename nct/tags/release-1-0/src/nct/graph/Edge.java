
//============================================================================
// 
//  file: Edge.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.graph;

/**
 * An generic interface describing the basic components of an edge: 
 * source and target nodes, edge weight, and description.
 */
public interface Edge<NodeType extends Comparable<NodeType>,
                      WeightType extends Comparable<WeightType>> 
	extends Comparable<Edge<NodeType,WeightType>> {

	/**
	 * @return The description of the edge.
	 */
	public String getDescription();

	/**
	 * @param desc The description of the edge.
	 */
	public void setDescription(String desc);

	/**
	 * @return The source node of this edge.
	 */
	public NodeType getSourceNode();

	/**
	 * @return The target node of this edge.
	 */
	public NodeType getTargetNode();

	/**
	 * @return The weight of this edge.
	 */
	public WeightType getWeight();
}
