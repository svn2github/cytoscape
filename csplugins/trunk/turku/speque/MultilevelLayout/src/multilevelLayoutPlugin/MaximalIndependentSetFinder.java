/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package multilevelLayoutPlugin;

import giny.model.Edge;
import giny.model.Node;

import java.util.HashSet;
import java.util.Iterator;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * Class used to find a maximal independent set of edges in 
 * a graph. Due performance issues the algorithm used is not optimal. 
 * @author Pekka Salmela
 *
 */
public class MaximalIndependentSetFinder {
	
	/**
	 * Finds a maximal independent set of edges in a graph and
	 * creates a new graph by removing those edges and combining nodes.
	 * Node attributes are used to denote connections between nodes
	 * of the two graphs. 
	 * @param previous The graph used to find the maximal independent
	 * set from.
	 * @param level The graph level on the context of the multilevel
	 * layout algorithm. 
	 * @return A coarser graph created by combining the nodes in the 
	 * <code>previous</code> graph.
	 */
	@SuppressWarnings("unchecked")
	public static CyNetwork findMaximalIndependentSet(CyNetwork previous, double level){
		Iterator<CyNode> nodesIterator = previous.nodesIterator();
		Iterator<CyEdge> edgesIterator = previous.edgesIterator();
		HashSet<Node> nodesList = new HashSet<Node>(previous.getNodeCount());
		CyAttributes nodesAttributes = Cytoscape.getNodeAttributes();
		int[] empty = {}; 
		CyNetwork next = Cytoscape.getRootGraph().createNetwork(empty, empty);
		
		//add all the nodes to next and list
		while(nodesIterator.hasNext()){
			CyNode n = nodesIterator.next();
			next.addNode(n);
			nodesList.add(n);
		}
		
		//add all the edges to next (except those having the same target and source, they don't affect the layout)
		while(edgesIterator.hasNext()){
			CyEdge e = edgesIterator.next();
			if(e.getTarget() != e.getSource()) next.addEdge(e);
		}
		
		//while the list is not empty
		while(!nodesList.isEmpty()){
			//select random node pair1
			Iterator<Node> iteraattori = nodesList.iterator();
			Node pair1 = iteraattori.next();
			
			//find edges connected to pair1
			HashSet<Edge> edgesConnectedToPair1 = new HashSet<Edge>(); 
			int[] edgeIndices1 = next.getAdjacentEdgeIndicesArray(pair1.getRootGraphIndex(), true, true, true);
			for(int i = 0; i < edgeIndices1.length; i++){
				edgesConnectedToPair1.add(next.getEdge(edgeIndices1[i]));
			}
			
			//find those nodes that are neighbours of pair1 and exist in the list
			HashSet<Node> potentialPairsForPair1 = new HashSet<Node>();
			HashSet<Node> neighboringNodesOfPair1 = new HashSet<Node>();
			for(Edge e : edgesConnectedToPair1){
				if(e.getSource() != pair1 && nodesList.contains(e.getSource()) && !potentialPairsForPair1.contains(e.getSource())) potentialPairsForPair1.add(e.getSource());
				if(e.getSource() != pair1 && !neighboringNodesOfPair1.contains(e.getSource())) neighboringNodesOfPair1.add(e.getSource());
				if(e.getTarget() != pair1 && nodesList.contains(e.getTarget()) && !potentialPairsForPair1.contains(e.getTarget())) potentialPairsForPair1.add(e.getTarget());
				if(e.getTarget() != pair1 && !neighboringNodesOfPair1.contains(e.getTarget())) neighboringNodesOfPair1.add(e.getTarget());
			}
			
			//if no node was found, remove pair1 from the list and skip the rest of the loop
			if(potentialPairsForPair1.isEmpty()){
				CyNode newNode = Cytoscape.getCyNode(pair1.getIdentifier()+ "-" +(level+1.0), true);
				nodesAttributes.setAttribute(newNode.getIdentifier(), "ml_previous", new Integer(pair1.getRootGraphIndex()));
				next.addNode(newNode);
				nodesList.remove(pair1);
				next.removeNode(pair1.getRootGraphIndex(), false);
				//create edges from neighbors of pair1 to the new node
				for(Node n : neighboringNodesOfPair1){
					CyEdge e = Cytoscape.getCyEdge(n, newNode, Semantics.INTERACTION, "", true, false);
					next.addEdge(e);
				}
				//remove from next edges connected to pair1
				for(Edge e: edgesConnectedToPair1){
					next.removeEdge(e.getRootGraphIndex(), false);
				}
			}
			else{
				//select node pair2 with the lowest value of weight+degree
				//if many nodes share the same value, select random node amongst them
				Node pair2 = null;
				int nWeight = 1;
				int pair2Weight = 1;
				for(Node n : potentialPairsForPair1){
					if (pair2 == null) pair2 = n;
					else{
						if (nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_weight") != null)
						nWeight = nodesAttributes.getIntegerAttribute(n.getIdentifier(), "ml_weight");
						if (nodesAttributes.getIntegerAttribute(pair2.getIdentifier(), "ml_weight") != null)
						pair2Weight = nodesAttributes.getIntegerAttribute(pair2.getIdentifier(), "ml_weight");
						//orginal method by Walshaw:
						//if (nWeight < pair2Weight) pair2 = n;
						//Pekka's new method:
						if (nWeight + next.getDegree(n) < pair2Weight + next.getDegree(pair2)) pair2 = n;
						
					}
				}
				
				//find edges connected to pair2
				HashSet<Edge> edgesConnectedToPair2 = new HashSet<Edge>(); 
				int[] edgeIndices2 = next.getAdjacentEdgeIndicesArray(pair2.getRootGraphIndex(), true, true, true);
				for(int i = 0; i < edgeIndices2.length; i++){
					edgesConnectedToPair2.add(next.getEdge(edgeIndices2[i]));
				}
				
				//find neighbouring nodes of pair2
				HashSet<Node> neighboringNodesOfPair2 = new HashSet<Node>();
				for(Edge e : edgesConnectedToPair2){
					if(e.getSource() != pair1 & e.getSource() != pair2) neighboringNodesOfPair2.add(e.getSource());
					if(e.getTarget() != pair1 & e.getTarget() != pair2) neighboringNodesOfPair2.add(e.getTarget());
				}
				
				//remove from next edges connected to pair1 and pair2
				for(Edge e: edgesConnectedToPair1){
					next.removeEdge(e.getRootGraphIndex(), false);
				}
				for(Edge e: edgesConnectedToPair2){
					next.removeEdge(e.getRootGraphIndex(), false);
				}
				
				//remove pair1 and pair2 from the list and next
				nodesList.remove(pair1);
				nodesList.remove(pair2);
				next.removeNode(pair1.getRootGraphIndex(), false);
				next.removeNode(pair2.getRootGraphIndex(), false);
				
				//create a new node with weight of pair1.weight + pair2.weight and add it to next
				CyNode combinedNode = Cytoscape.getCyNode(pair1.getIdentifier()+ "+" +pair2.getIdentifier(), true);
				int weight1 = 1;
				int weight2 = 1;
				if(nodesAttributes.getIntegerAttribute(pair1.getIdentifier(), "ml_weight")!=null){
					weight1 = nodesAttributes.getIntegerAttribute(pair1.getIdentifier(), "ml_weight");
				}
				if(nodesAttributes.getIntegerAttribute(pair2.getIdentifier(), "ml_weight")!=null){
					weight2 = nodesAttributes.getIntegerAttribute(pair2.getIdentifier(), "ml_weight");
				}
				nodesAttributes.setAttribute(combinedNode.getIdentifier(), "ml_weight", new Integer(weight1+weight2));
				nodesAttributes.setAttribute(combinedNode.getIdentifier(), "ml_ancestor1", new Integer(pair1.getRootGraphIndex()));
				nodesAttributes.setAttribute(combinedNode.getIdentifier(), "ml_ancestor2", new Integer(pair2.getRootGraphIndex()));
				next.addNode(combinedNode);
				
				//create new edges from neigbouring nodes of pair1 and pair2 to the new node
				neighboringNodesOfPair1.remove(pair2);
				for(Node n : neighboringNodesOfPair1){
					CyEdge e = Cytoscape.getCyEdge(n, combinedNode, Semantics.INTERACTION, "", true, false);
					next.addEdge(e);
				}
				neighboringNodesOfPair2.remove(pair1);
				for(Node n : neighboringNodesOfPair2){
					CyEdge e = Cytoscape.getCyEdge(n, combinedNode, Semantics.INTERACTION, "", true, false);
					next.addEdge(e);
				}
			}//end if
		}//end while
		return next;
	}
}
