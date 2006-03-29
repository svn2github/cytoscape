
//============================================================================
// 
//  file: BasicKPartiteGraph.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================



package nct.graph.basic;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

import nct.graph.KPartiteGraph;
import nct.graph.Graph;

/**
 * A generic implementation of the KPartiteGraph interface.
 */
public class BasicKPartiteGraph<NodeType extends Comparable<? super NodeType>,
                                WeightType extends Comparable<? super WeightType>,
				PartitionType extends Comparable<? super PartitionType>>
	extends BasicGraph<NodeType,WeightType>
	implements KPartiteGraph<NodeType,WeightType,PartitionType>, 
	           Comparable<Graph<NodeType,WeightType>>, 
		   Cloneable {

	/**
	 * A mapping of nodes to partitions.
	 */
	protected Map<NodeType,PartitionType> partitionMap; 

	/**
	 * The number of partitions allowed in the graph, not necessarily the number
	 * currently contained in the graph.
	 */
	protected int K;

	/**
	 * This value indicates that an unlimited number of partitions is allowed in
	 * This graph.
	 */
	public static int UNLIMITED_PARTITIONS = -1;

	/**
	 * Constructor that names the graph "none" and allows unlimited partitions.
	 */
	public BasicKPartiteGraph() {
		this("none",UNLIMITED_PARTITIONS);
	}

	/**
	 * Constructor that names the graph as specified and allows unlimited partitions.
	 * @param name The name of the graph.
	 */
	public BasicKPartiteGraph(String name) {
		this(name,UNLIMITED_PARTITIONS);
	}

	/**
	 * Constructor that names the graph as specified and limits the number of partitions
	 * allowed in this graph.
	 * @param name The name of the graph.
	 * @param K The number of allowed partitions in this graph. 
	 */
	public BasicKPartiteGraph(String name, int K) {
		super(name);

		this.K = K;
		partitionMap = new HashMap<NodeType,PartitionType>();
	}

	/**
	 * This method overrides the BasicGraph implementation of addNode and always
	 * returns false because it is impossible to add a node to a k-paritite graph without
	 * also specifying the partition.
	 * @param node The node that will NOT be added.
	 * @return Always returns false. 
	 */
	public boolean addNode(NodeType node) {
		return false;
	}

	/**
	 * Adds a node to the graph. This implementation assumes that nodes are unique,
	 * independent of their partitions. For instance it would NOT be allowed to have
	 * a node (of type String) "homer" in partition one and a node "homer" in partition
	 * two. The partition the node is added to is also checked.  If the partition is
	 * not currently in the graph, but the graph already contains K partitions, then
	 * the node and partition will not be added. The node and partition will be added
	 * if unlimited partitions are allowed or if the number of current partitions is
	 * less than K. 
	 * @param node The node to be added to the graph. 
	 * @return Whether or not the node was successfully added. 
	 */
	public boolean addNode(NodeType node, PartitionType partition) {

		if ( node == null || partition == null )
			return false;

		if ( partitionMap.containsKey( node ) )
			return false;

		else if ( K == UNLIMITED_PARTITIONS || isPartition(partition) || getNumPartitions() < K ) {
			partitionMap.put(node,partition);
			return super.addNode(node);
		} else
			return false;
	}

	/**
	 * Adds an edge to the graph.  Does not add the edge (returns false) if the nodes 
	 * are contained in the same partition.  
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @return Whether or not the edge was successfully added.
	 */
	public boolean addEdge(NodeType nodeA, NodeType nodeB, WeightType weight) {

		if ( nodeA == null || nodeB == null )
			return false;

		if ( !partitionMap.containsKey( nodeA ) ) { 
			System.out.println("partA doesn't contain nodeA " + nodeA);
			return false;
		}

		if ( !partitionMap.containsKey( nodeB ) ) {
			System.out.println("partB doesn't contain nodeB " + nodeB);
			return false;
		}

		if ( partitionMap.get(nodeA).equals( partitionMap.get(nodeB) ) ){
			System.out.println("nodeA equals nodeB");
			return false;
		}
		
		return super.addEdge( nodeA, nodeB, weight );
	}

	
        /**
         * Returns a list of the partitions currently contained in the graph
         * (not necessarily K if K partitions haven't yet been added).
         * @return A List of the current partitions in the graph.
         */
	public List<PartitionType> getPartitions() {
		 ArrayList<PartitionType> l = new ArrayList<PartitionType>(); 
		 l.addAll(new HashSet<PartitionType>(partitionMap.values()) );
		 if ( l.size() > 1 )
		 	Collections.sort(l);
		 return l; 
	}

	/**
	 * This method returns the number of existing partitions. It is possible that
	 * more partitions could be added to the graph.
	 */
	public int getNumPartitions() {
		return (new HashSet<PartitionType>(partitionMap.values())).size(); 
	}

	/**
	 * This method returns the number of possible partitions. The "K" in K-partite.
	 */
	public int getK() {
		return K; 
	}

        /**
         * Checks whether a specified partition is one of the partitions used in the graph.
         * @param p The partition to check.
         * @return Whether or not the specified partition is one of the partitions used in
         * the graph.
         */
	public boolean isPartition(PartitionType p) {
		if ( p == null )
			return false;

		List<PartitionType> parts = getPartitions();
		if ( parts.contains(p) )
			return true;
		else
			return false;
	}

	/**
	 * Returns a list of all k-cliques that exist in the graph. Currently not 
	 * implemented and returns null.
	 * @return A list of lists of nodes that define a k-clique.
	 */
	public List<List<NodeType>> getAllKCliques() {
		return null;		//TODO
	}
}
