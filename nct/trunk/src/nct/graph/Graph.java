
//============================================================================
// 
//  file: Graph.java
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



package nct.graph;

import java.util.*;
import java.lang.*;


/**
 * A generic interface that defines an edge weighted, undirected graph.
 */
public interface Graph<NodeType extends Comparable<? super NodeType>,
                       WeightType extends Comparable<? super WeightType>> 
	extends Comparable<Graph<NodeType,WeightType>>, Cloneable {

	/**
	 * Adds a node to the graph.
	 * @param node The node to add.
	 * @return true if node successfully added, false otherwise. 
	 */
	public boolean addNode(NodeType node);

	/**
	 * Adds an edge to the graph.
	 * @param nodeA The beginning node of the edge to add.
	 * @param nodeB The ending node of the edge to add.
	 * @param weight The edge weight. 
	 * @return true if edge successfully added, false otherwise. 
	 */
	public boolean addEdge(NodeType nodeA, NodeType nodeB, WeightType weight); 

	/**
	 * Adds an edge to the graph.
	 * @param nodeA The beginning node of the edge to add.
	 * @param nodeB The ending node of the edge to add.
	 * @param weight The edge weight. 
	 * @param desc The edge description. 
	 * @return true if edge successfully added, false otherwise. 
	 */
	public boolean addEdge(NodeType nodeA, NodeType nodeB, WeightType weight, String desc); 

	/**
	 * Indicates whether the specified nodes refer to a valid edge in the graph.
	 * @param nodeA The beginning node of the edge to check.
	 * @param nodeB The ending node of the edge to check.
	 * @return true if edge exists, false otherwise. 
	 */
	public boolean isEdge(NodeType nodeA, NodeType nodeB);

	/**
	 * Indicates whether the specified nodes is contained in the graph. 
	 * @param node The node to check.
	 * @return true if node exists, false otherwise. 
	 */
	public boolean isNode(NodeType node); 

	/**
	 * Returns the weight of the edge implied by the two specified nodes. 
	 * @param nodeA The beginning node of the edge. 
	 * @param nodeB The ending node of the edge. 
	 * @return The weight of the edge. 
	 */
	public WeightType getEdgeWeight(NodeType nodeA, NodeType nodeB); 

	/**
	 * Returns a list of all edge weights in the graph. 
	 * @return A list of all edge weights in the graph.
	 */
	//public List<WeightType> getEdgeWeights(); 

	/**
	 * Returns a set containing all nodes in the graph.
	 * @return A set of all nodes in the graph. 
	 */
	public Set<NodeType> getNodes(); 
	public List<NodeType> getNodeList(); 

	/**
	 * Returns a set all nodes adjacent to the specified node. 
	 * @param node The node whose neighbors we're requesting.
	 * @return A set of all neighbor nodes. 
	 */
	public Set<NodeType> getNeighbors(NodeType node);

	/**
	 * Returns the id of the graph.
	 * @return The id of the graph.
	 */
	public String getId();

	/**
	 * Returns the number of nodes in the graph.
	 * @return The number of nodes in the graph.
	 */
	public int numberOfNodes();

	/**
	 * Returns the number of edges in the graph.
	 * @return The number of edges in the graph.
	 */
	public int numberOfEdges();

	/**
	 * Returns the degree (number of neighbors) of the node.
	 * @param node The node whose degree we're requesting.
	 * @return The degree of the node.
	 */
	public int degreeOfNode(NodeType node);

	/**
	 * Returns the score of the graph.
	 * @return The score of the graph.
	 */
	WeightType getScore(); // Perhaps this should be in a separate interface?

	/**
	 * Sets the score of the graph.
	 * @param f The score to set the graph to.
	 */
	void setScore(WeightType f); // Perhaps this should be in a separate interface?

	/**
	 * Compares graph g to this graph.
	 * @param g Graph to compare this graph to.
	 * @return -1 if less, 0 if equal, 1 if greater - what those mean 
	 * are left to the implementer.
	 */
	int compareTo(Graph<NodeType,WeightType> g); 

	/**
	 * Returns a description of the edge implied by the two specified nodes. 
	 * @param nodeA The beginning node of the edge. 
	 * @param nodeB The ending node of the edge. 
	 * @return The description of the edge. 
	 */
	public String getEdgeDescription(NodeType nodeA, NodeType nodeB);

	/**
	 * Sets a description for the edge implied by the two specified nodes.
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @param desc The description of the edge.
	 * @return Whether or not the descriptinon was successfully set.
	 */
	public boolean setEdgeDescription(NodeType nodeA, NodeType nodeB, String desc);

	/**
	 * Returns a specific edge.
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @return The edge object described by the two node parameters.
	 */
	public Edge<NodeType,WeightType> getEdge(NodeType nodeA, NodeType nodeB);

	/**
	 * Returns all edges in the graph. 
	 * @return A set of edge objects containing all edges in the graph. 
	 */
	public Set<Edge<NodeType,WeightType>> getEdges();
	public List<Edge<NodeType,WeightType>> getEdgeList();

	/**
	 * Returns a clone of this object.
	 * @return A clone of this object. 
	 */
	public Object clone();

	/**
	 * Returns a String representation of the graph format.
	 */
	public String toString();

	public boolean removeEdge(NodeType nodeA, NodeType nodeB);
	public boolean removeNode(NodeType node);

	public boolean setEdgeWeight(NodeType nodeA, NodeType nodeB, WeightType weight);

}
