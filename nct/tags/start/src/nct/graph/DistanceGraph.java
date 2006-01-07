package nct.graph;

/**
 * This interface describes a method that returns the distance between
 * two nodes.  What constitutes the distance is left to the implementer.
 */
public interface DistanceGraph<NodeType extends Comparable<NodeType>,
                               WeightType extends Comparable<WeightType>> 
	extends Graph<NodeType,WeightType> {

	/**
	 * Returns the minimum distance between the specified nodes.
	 * @param nodeA From node.
	 * @param nodeB To node.
	 * @return The distance between the nodes specified.
	 */
	public byte getDistance(NodeType nodeA, NodeType nodeB);
}
