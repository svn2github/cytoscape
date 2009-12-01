
//============================================================================
// 
//  file: BasicGraph.java
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
import java.lang.reflect.*;
import java.io.*;

import nct.graph.Graph;
import nct.graph.Edge;

/**
 * A generic implementation of the Graph interface.
 */
public class BasicGraph<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> 
	implements Graph<NodeType,WeightType>, Comparable<Graph<NodeType,WeightType>>, Cloneable {

	/**
	 * A mapping of nodes to nodes to edge weights.
	 */
	protected Map<NodeType,Map<NodeType,WeightType>> weightMap; 

	/**
	 * A mapping of nodes to nodes to edge descriptions.  This is not populated by default.
	 */
	protected Map<NodeType,Map<NodeType,String>> descMap; 

	/**
	 * The id of the graph.
	 */
	protected String id;

	/**
	 * The score of the graph.
	 */
	protected WeightType score;

	/**
	 * Used to indicate whether the graph has been updated (ie node or edge added)
	 * since the last time a value based on the graph (eg numEdges) was calculated. 
	 */
	protected boolean assumeGraphFinished;

	/**
	 * The number of edges in the graph.
	 */
	protected int numEdges;

	/**
	 * Constructor.
	 */
	public BasicGraph() {
		this("none");
	}

	/**
	 * Constructor.
	 * @param id The graph id.
	 */
	public BasicGraph(String id) {
		this.id = id;
		weightMap = new HashMap<NodeType,Map<NodeType,WeightType>>();
		descMap = new HashMap<NodeType,Map<NodeType,String>>();
		score = null;
		assumeGraphFinished = false;
		numEdges = 0;
	}


        /**
         * Copy Constructor.
         * @param g The graph that should be copied into this graph.
         */
        public BasicGraph(Graph<NodeType,WeightType> g) {

                this(g.getId());
                setScore(g.getScore());

                for ( NodeType node: g.getNodes() )
                        addNode(node);

                for ( Edge<NodeType,WeightType> edge: g.getEdges() )
			addEdge(edge.getSourceNode(),edge.getTargetNode(),edge.getWeight(),edge.getDescription());
        }


        /**
         * Adds a node to the graph.
         * @param node The node to add.
         * @return true if node successfully added, false otherwise.
         */
	public boolean addNode(NodeType node) {

		assumeGraphFinished = false;

		if ( node != null && !weightMap.containsKey( node ) ) {
			weightMap.put(node, new HashMap<NodeType,WeightType>());
			return true;
		} else {
			//System.out.println("attempting to add duplicate node: " + node);
			return false;
		}

	}
        /**
         * Adds an edge to the graph. Returns false if the nodes are bad or if the
	 * the nodes have not already been added to the graph.
         * @param nodeA The beginning node of the edge to add.
         * @param nodeB The ending node of the edge to add.
         * @param weight The edge weight.
         * @return true if edge successfully added, false otherwise.
         */
	public boolean addEdge(NodeType nodeA, NodeType nodeB, WeightType weight) {
		return addEdge(nodeA,nodeB,weight,null);
	}

        /**
         * Adds an edge to the graph. Returns false if the nodes are bad or if the
	 * the nodes have not already been added to the graph.
         * @param nodeA The beginning node of the edge to add.
         * @param nodeB The ending node of the edge to add.
         * @param weight The edge weight.
         * @param desc The edge description.
         * @return true if edge successfully added, false otherwise.
         */
	public boolean addEdge(NodeType nodeA, NodeType nodeB, WeightType weight, String desc) {

		if ( nodeA == null || nodeB == null )
			return false;

		if ( !weightMap.containsKey( nodeA ) || !weightMap.containsKey( nodeB ) )
			return false;

		assumeGraphFinished = false;

		weightMap.get(nodeA).put(nodeB,weight);
		weightMap.get(nodeB).put(nodeA,weight);
		numEdges++;

		if ( desc != null )
			actuallySetEdgeDesc(nodeA,nodeB,desc);
		
		return true;
	}

        /**
         * Indicates whether the specified nodes refer to a valid edge in the graph.
         * @param nodeA The beginning node of the edge to check.
         * @param nodeB The ending node of the edge to check.
         * @return true if edge exists, false otherwise.
         */
	public boolean isEdge(NodeType nodeA, NodeType nodeB) {
		if ( weightMap.containsKey( nodeA ) ) 
			return weightMap.get(nodeA).containsKey( nodeB );
		else 
			return false;
	}

        /**
         * Indicates whether the specified node is contained in the graph.
         * @param node The node to check.
         * @return true if node exists, false otherwise.
         */
	public boolean isNode(NodeType node) {
		return weightMap.containsKey(node);
	}

        /**
         * Returns the weight of the edge implied by the two specified nodes.
         * @param nodeA The source node of the edge.
         * @param nodeB The target node of the edge.
         * @return The weight of the edge.
         */
	public WeightType getEdgeWeight(NodeType nodeA, NodeType nodeB) {
		Map<NodeType,WeightType> mef = weightMap.get(nodeA);
		if ( mef != null )
			return mef.get(nodeB);
		else
			return null;
	}

	/**
	 * Returns the specified edge. Returns null if the edge doesn't exist.
         * @param nodeA The source node of the edge.
         * @param nodeB The target node of the edge.
	 * @return The edge specified.
	 */
	public Edge<NodeType,WeightType> getEdge(NodeType nodeA, NodeType nodeB) {
		if ( isEdge(nodeA,nodeB) )
			return new BasicEdge<NodeType,WeightType>(nodeA,nodeB,weightMap.get(nodeA).get(nodeB));
		else
			return null;
	}


        /**
         * Returns a set containing all nodes in the graph.
         * @return A set of all nodes in the graph.
         */
	public Set<NodeType> getNodes() {
		return weightMap.keySet();
	}

	public List<NodeType> getNodeList() {
		List<NodeType> l = new ArrayList<NodeType>( weightMap.keySet());
		Collections.sort(l);
		return l;
	}

        /**
         * Returns a set all nodes adjacent to the specified node.
         * @param node The node whose neighbors we're requesting.
         * @return A set of all neighbor nodes.
         */
	public Set<NodeType> getNeighbors(NodeType node) {
		Map<NodeType,WeightType> wm = weightMap.get(node);
		if ( wm == null )
			return null;
		else
			return wm.keySet();
	}

        /**
         * Returns the id of the graph, in this case the filename of the input
	 * file that specifies the graph.
         * @return The id of the graph.
         */
	public String getId() {
		return id;
	}

        /**
         * Returns the number of nodes in the graph.
         * @return The number of nodes in the graph.
         */
        public int numberOfNodes() {
		return weightMap.size();
	}

        /**
         * Returns the number of edges in the graph. 
         * @return The number of edges in the graph.
         */
        public int numberOfEdges() {
		return numEdges; 
	}

        /**
         * Returns the degree (number of neighbors) of the node.
         * @param node The node whose degree we're requesting.
         * @return The degree of the node.
         */
	public int degreeOfNode(NodeType node) {
		if ( isNode(node) )
			return weightMap.get(node).size();
		else
			return 0;
	}

	public List<Edge<NodeType,WeightType>> getEdgeList() { 
		List<Edge<NodeType,WeightType>> l =  new ArrayList<Edge<NodeType,WeightType>>( getEdges() );
		Collections.sort(l);
		return l; 
	}

        /**
         * Returns a list of all edges in the graph.
         * @return A list of all edges in the graph.
         */
	public Set<Edge<NodeType,WeightType>> getEdges() {
		Set<Edge<NodeType,WeightType>> edgeSet = new HashSet<Edge<NodeType,WeightType>>();
		for(NodeType node1: weightMap.keySet()) {
			for(NodeType node2: weightMap.get(node1).keySet()) {
				if ( node1.compareTo( node2 ) <= 0 ) 
					edgeSet.add( new BasicEdge<NodeType,WeightType>(node1,node2,weightMap.get(node1).get(node2),getEdgeDescription(node1,node2)));
				else
					edgeSet.add( new BasicEdge<NodeType,WeightType>(node2,node1,weightMap.get(node2).get(node1),getEdgeDescription(node2,node1)));
			}
		}
		return edgeSet;
	}

	/**
	 * Returns the compareTo value of the score of this graph.
	 * @param g Graph to compare against this one.
	 * @return The compareTo() value of the score of this graph.
	 */
	public int compareTo(Graph<NodeType,WeightType> g) {
		int numNodes = numberOfNodes();
		int gnumNodes = g.numberOfNodes();

		if ( numNodes != gnumNodes ) {
			if ( numNodes > gnumNodes )
				return 1;
			if ( numNodes < gnumNodes )
				return -1;
		}

		// same number of nodes at this point

		int numEdges = numberOfEdges();
		int gnumEdges = g.numberOfEdges();

		if ( numEdges != gnumEdges ) {
			if ( numEdges > gnumEdges )
				return 1;
			if ( numEdges < gnumEdges )
				return -1;
		}

		// same number of nodes and edges at this point.

		// So the thing to do would be to compare the sets.
		// However, that is SLOW, so we won't.
		// Instead we'll just skip ahead to comparing scores. 
	
		if ( score != null )
			return score.compareTo(g.getScore());
		
		// hashCode should return something different for us if the
		// nodes and edges are different, despite being the same
		// in number.
		if ( hashCode() > g.hashCode() )
			return 1;
		else if ( hashCode() < g.hashCode() )
			return -1;

		// If we get here, then we're *probably* equal, depending on hashCode.
		else 
			return 0;  
	}

        /**
         * Sets the score of the graph.
         * @param score The score to set the graph to.
         */
	public void setScore(WeightType score) {
		this.score = score;
	}

	/**
         * Returns the score of the graph.
         * @return The score of the graph.
         */
	public WeightType getScore() {
		return score;
	}

	/**
	 * Returns a clone of this graph.
	 * @return A clone of this graph.
	 */
	public BasicGraph<NodeType,WeightType> clone() {
		return new BasicGraph<NodeType,WeightType>(this);
	} 


        /**
         * Returns a description of the edge implied by the two specified nodes.
	 * Because the edge description map is not populated by default, this method
	 * will return the edge weight as a description if the edge description has
	 * not been explicitly set.
         * @param nodeA The beginning node of the edge.
         * @param nodeB The ending node of the edge.
         * @return The description of the edge.
         */
	public String getEdgeDescription(NodeType nodeA, NodeType nodeB) {
		if ( !isEdge(nodeA,nodeB) ) 
			return null;

		if ( descMap.containsKey(nodeA) && descMap.get(nodeA).containsKey(nodeB) ) 
			return descMap.get(nodeA).get(nodeB);
		else 
			return weightMap.get(nodeA).get(nodeB).toString();
	}

	/**
	 * Sets the description for the specified edge.
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @param desc The description of the edge.
	 * @return Whether or not the edge description has been set.
	 */
	public boolean setEdgeDescription(NodeType nodeA, NodeType nodeB, String desc) {
		if ( nodeA == null || nodeB == null )
			return false;

		if ( !weightMap.containsKey( nodeA ) || !weightMap.containsKey( nodeB ) )
			return false;

		actuallySetEdgeDesc(nodeA,nodeB,desc);

		return true;
	}

	/**
	 * A private method used to actually set the description because this is
	 * done in multiple places. This method assumes that the specified nodes 
	 * are valid.
	 */
	private void actuallySetEdgeDesc(NodeType nodeA, NodeType nodeB, String desc) {

		if ( !descMap.containsKey( nodeA ) )
			descMap.put(nodeA,new HashMap<NodeType,String>());

		if ( !descMap.containsKey( nodeB ) )
			descMap.put(nodeB,new HashMap<NodeType,String>());

		descMap.get(nodeA).put(nodeB,desc);
		descMap.get(nodeB).put(nodeA,desc);
	}


	/**
	 * @return A string providing some basic identifying information about the graph.
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Graph id: ");
		s.append(id);
		if ( score != null ) {
			s.append("   score: ");
			s.append(score.toString());
		}
		s.append("   num nodes: ");
		s.append(Integer.toString(numberOfNodes()));
		s.append("   num edges: ");
		s.append(Integer.toString(numberOfEdges()));
		
		String newline = System.getProperty("line.separator");
		s.append(newline);

		// list the edges in alphabetical order
		List<String> edges = new ArrayList<String>();

		for (Edge<NodeType,WeightType> e: getEdges()) 
			edges.add( e.toString() );

		Collections.sort(edges);

		for (String e: edges)
			s.append( e + newline );

		return s.toString();
	}

	/**
	 * Removes the specified edge from the graph. The nodes connected to the edge
	 * will NOT be removed.
         * @param nodeA The beginning node of the edge to remove.
         * @param nodeB The ending node of the edge to remove.
	 * @return Whether or not the edge was successfully removed. 
	 */
	public boolean removeEdge(NodeType nodeA, NodeType nodeB) {

		if ( nodeA == null || nodeB == null )
			return false;

		if ( !weightMap.containsKey( nodeA ) || !weightMap.containsKey( nodeB ) )
			return false;

		assumeGraphFinished = false;

		weightMap.get( nodeA ).remove( nodeB );
		weightMap.get( nodeB ).remove( nodeA );

		if ( descMap.containsKey( nodeA ) && descMap.containsKey( nodeB ) ) {
			descMap.get( nodeA ).remove( nodeB );
			descMap.get( nodeB ).remove( nodeA );
		}

		numEdges--;

		return true;
	}

	/**
	 * Removes the specified node from the graph and all of its adjacent edges.
	 * @param node The node to be removed.
	 * @return Whether or not the node was successfully removed. 
	 */
	public boolean removeNode(NodeType node) {
		if ( !isNode( node ) )
			return false;

		assumeGraphFinished = false;

		for ( NodeType neighbor : getNeighbors( node ) ) {
			weightMap.get( neighbor ).remove( node );
			numEdges--;
			if ( descMap.containsKey( neighbor ) )
				descMap.get( neighbor ).remove( node );
		}

		weightMap.remove( node );
		if ( descMap.containsKey( node ) )
			descMap.remove( node );

		return true;
	}

	/**
	 * Sets the edge weight of an existing edge.
	 * @param nodeA The source node of the edge.
	 * @param nodeB The target node of the edge.
	 * @param weight The weight of the edge.
	 * @return Whether or not the edge description has been set.
	 */
	public boolean setEdgeWeight(NodeType nodeA, NodeType nodeB, WeightType weight) {
		if ( nodeA == null || nodeB == null || weight == null )
			return false;

		if ( !weightMap.containsKey( nodeA ) || !weightMap.containsKey( nodeB ) )
			return false;

		weightMap.get(nodeA).put(nodeB,weight);
		weightMap.get(nodeB).put(nodeA,weight);

		return true;
	}
}
