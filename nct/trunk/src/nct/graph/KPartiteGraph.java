package nct.graph;

import java.util.*;

/**
 * A generic interface to a K-partite graph.
 */
public interface KPartiteGraph<NodeType extends Comparable<NodeType>,
                               WeightType extends Comparable<WeightType>,
			       PartitionType extends Comparable<? super PartitionType>>
	extends Graph<NodeType,WeightType> { 

	/**
	 * Returns a list of the partitions currently contained in the graph 
	 * (not necessarily K if K partitions haven't yet been added).
	 * @return A List of the current partitions in the graph.
	 */
	public List<PartitionType> getPartitions();

	/**
	 * Returns the number of partitions currently contained in the graph 
	 * (not necessarily K if K partitions haven't yet been added).
	 */
	public int getNumPartitions();

	/**
	 * Returns the number of possible partitions in the graph. The "K" in K-partite.
	 * This value is not necessarily the number of partitions currently in the graph
	 * if K partitions have not yet been specified.
	 */
	public int getK(); 

	/**
	 * Checks whether a specified partition is one of the partitions used in the graph.
	 * @param p The partition to check.
	 * @return Whether or not the specified partition is one of the partitions used in 
	 * the graph.
	 */
	public boolean isPartition(PartitionType p);

	/**
	 * Adds a node to a specific partition in the graph.
	 * @param node The node to be added to the graph.
	 * @param partition The partition the node should be added to.
	 * @return Whether the node was successfully added to the graph.
	 */
	public boolean addNode(NodeType node, PartitionType partition);

}
