/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A class with static methods to expand a graph.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org,
 *         iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */

// TODO: 
// 1. Progress monitors

package graphExpander.expander;

import cytoscape.*;
import java.util.*;
import graphConnectivity.ShortestConnectingPaths;
import giny.model.RootGraph;
import cern.colt.list.IntArrayList;

public class GraphExpander {

	public static final int NUM_ADDED_NODES_INDEX = 0;

	public static final int NUM_ADDED_EDGES_INDEX = 1;

	/**
	 * Adds edges between nodes in target_net from interactions in soure_net.
	 * 
	 * @param source_net
	 *            the network from which edges will be copied
	 * @param target_net
	 *            the network to which edges will be copied
	 * @return an array of size two, indicating number of added nodes (at index
	 *         NUM_ADDED_NODES_INDEX, note that this will be zero), and the
	 *         number of edges added (at index NUM_ADDED_EDGES_INDEX)
	 */
	public static int[] addInteractionsBetweenExistingNodes(
			CyNetwork source_net, CyNetwork target_net) {
		
		if(source_net.getRootGraph() != target_net.getRootGraph()){
			throw new IllegalStateException("target_net's RootGraph is not the same as source_net's RootGraph");
		}
		
		int[] numAddedGraphObjects = { 0, 0 };
		
		Iterator nodesIterator = target_net.nodesIterator();
		IntArrayList tnodesRootGraphIndices = new IntArrayList();
		while(nodesIterator.hasNext()){
			tnodesRootGraphIndices.add(((CyNode)nodesIterator.next()).getRootGraphIndex());
		}
		
		if(tnodesRootGraphIndices.size() == 0){
			return numAddedGraphObjects;
		}
		
		IntArrayList targetNodesInSource = new IntArrayList();
		nodesIterator = source_net.nodesIterator();
		while(nodesIterator.hasNext()){
			targetNodesInSource.add(((CyNode)nodesIterator.next()).getRootGraphIndex());
		}
		targetNodesInSource.retainAll(tnodesRootGraphIndices);
		targetNodesInSource.trimToSize();

		int [] connectingEdges = source_net.getConnectingEdgeIndicesArray(targetNodesInSource.elements());
		int[] restoredEdges = target_net.restoreEdges(connectingEdges);
		if(restoredEdges != null){
			numAddedGraphObjects[NUM_ADDED_EDGES_INDEX] = restoredEdges.length;
		}
		return numAddedGraphObjects;
	}//addIntearctionsBetweenExistingNodes

	/**
	 * If target_net is a disconnected graph (contains nodes that have no
	 * connecting paths between them), then this method finds paths (from
	 * source_net) between these disconnected nodes and adds them to target_net.
	 * 
	 * @param source_net
	 *            the network from which paths will be obtained
	 * @param target_net
	 *            the network to which paths will be copied
	 * @return an array of size two, indicating number of added nodes (at index
	 *         NUM_ADDED_NODES_INDEX), and the number of edges added (at index
	 *         NUM_ADDED_EDGES_INDEX)
	 */
	//TODO: Use Rowan's methods for graph components
	public static int[] addInteractionsToConnectGraph(CyNetwork source_net,
			CyNetwork target_net) {
		if(source_net.getRootGraph() != target_net.getRootGraph()){
			throw new IllegalStateException("target_net's RootGraph is not the same as source_net's RootGraph");
		}
		int[] numAddedGraphObjects = { 0, 0 };
		return numAddedGraphObjects;
	}//addInteractionToConnectGraph

	/**
	 * Adds to the target network nodes in the shortest paths between its nodes;
	 * the shortest paths are obtained from interactions in the source network;
	 * the added paths are of length of at most maxPathLength; after all nodes
	 * in shortest paths are added, edges between those nodes and existing nodes
	 * are also added (the edges are obtained from the source network)
	 * 
	 * @param source_net
	 *            the network from which shortest paths are obtained
	 * @param target_net
	 *            the network to which shortest paths will be added
	 * @param maxPathLength
	 *            the maximum path length of a shortest path to be added to
	 *            target_net
	 * @return an array of size two indicating number of added nodes (at index
	 *         NUM_ADDED_NODES_INDEX), and the number of added edges (at index
	 *         NUM_ADDED_EDGES_INDEX)
	 */
	public static int[] addShortestPaths(CyNetwork source_net,
			CyNetwork target_net, int maxPathLength) {
		
		if(source_net.getRootGraph() != target_net.getRootGraph()){
			throw new IllegalStateException("target_net's RootGraph is not the same as source_net's RootGraph");
		}
		
		int[] numAddedGraphObjects = { 0, 0 };

		Iterator nodesIterator = target_net.nodesIterator();
		List tnodes = new ArrayList();
		while(nodesIterator.hasNext()){
			tnodes.add(nodesIterator.next());
		}

		List targetNodesInSource = new ArrayList();
		nodesIterator = source_net.nodesIterator();
		while(nodesIterator.hasNext()){
			targetNodesInSource.add(nodesIterator.next());
		}
		targetNodesInSource.retainAll(tnodes);

		long startTime = System.currentTimeMillis();
		
		Set connectingNodesSet = ShortestConnectingPaths
				.findShortestConnectingPaths(source_net, maxPathLength,
						targetNodesInSource);
		System.out.println("Found shortest connecting paths, time = " + ((System.currentTimeMillis() - startTime)/1000) );

		if (connectingNodesSet == null || connectingNodesSet.size() == 0) {
			return numAddedGraphObjects;
		}

		startTime = System.currentTimeMillis();
		nodesIterator = connectingNodesSet.iterator();
		IntArrayList restoredNodesRootIndices = new IntArrayList();
		while(nodesIterator.hasNext()){
			CyNode restoredNode = (CyNode)target_net.restoreNode((CyNode)nodesIterator.next());
			if( restoredNode != null){
				restoredNodesRootIndices.add(restoredNode.getRootGraphIndex());
			}
		}
		System.out.println("Done restoring nodes in shortest paths, time = " + ((System.currentTimeMillis() -  startTime)/1000) );
		
		restoredNodesRootIndices.trimToSize();
		
		numAddedGraphObjects[NUM_ADDED_NODES_INDEX] = restoredNodesRootIndices.size();
		
		startTime = System.currentTimeMillis();
		int [] edges = null;
		if(restoredNodesRootIndices.size() == 0 && connectingNodesSet.size() > 0){
			// This means that all of the nodes in the connecting paths are in
			// the target network, so just add the edges
			IntArrayList connectingNodesRootIndices = new IntArrayList();
			Iterator it = connectingNodesSet.iterator();
			while(it.hasNext()){
				connectingNodesRootIndices.add(  ((CyNode)it.next()).getRootGraphIndex()  );
			}
			connectingNodesRootIndices.trimToSize();
			edges = source_net.getConnectingEdgeIndicesArray(connectingNodesRootIndices.elements());
		}else if(restoredNodesRootIndices.size() > 0){
			edges = source_net.getConnectingEdgeIndicesArray(restoredNodesRootIndices.elements());
		}
		System.out.println("Done getting edges in shortest paths, time = " + ( (System.currentTimeMillis() - startTime)/1000) );
		
		startTime = System.currentTimeMillis();
		int[] restoredEdges = target_net.restoreEdges(edges);
		System.out.println("Done restoring edges in shortest paths, time = " + ( (System.currentTimeMillis() - startTime)/1000) );
		if(restoredEdges != null){
			numAddedGraphObjects[NUM_ADDED_EDGES_INDEX] = restoredEdges.length;
		}
		
		return numAddedGraphObjects;
	}//addShortestPaths

	/**
	 * Adds to target_net nodes from source_net that have at least
	 * minNumNeighbors adjacent nodes in target_net; it also adds the edges
	 * between these new nodes and existing nodes in target_net
	 * 
	 * @param source_net
	 *            the network from which nodes that have at least
	 *            minNumNeighbors will be added to target_net
	 * @param target_net
	 *            the network to which nodes will be added
	 * @param minNumNeighbors
	 *            the least number of neighbors a nodes must have in target_net
	 *            for it to be added
	 * @return an array of size two indicating number of added nodes (at index
	 *         NUM_ADDED_NODES_INDEX), and the number of added edges (at index
	 *         NUM_ADDED_EDGES_INDEX)
	 */
	public static int[] addNodesWithKneighbors(CyNetwork source_net,
			CyNetwork target_net, int minNumNeighbors) {
		
		if(source_net.getRootGraph() != target_net.getRootGraph()){
			throw new IllegalStateException("target_net's RootGraph is not the same as source_net's RootGraph");
		}
		
		int[] numAddedGraphObjects = { 0, 0 };

		IntArrayList tnodesRootGraphIndices = new IntArrayList();
		Iterator nodesIterator = target_net.nodesIterator();
		while(nodesIterator.hasNext()){
			tnodesRootGraphIndices.add(  ((CyNode)nodesIterator.next()).getRootGraphIndex()  );
		}
		tnodesRootGraphIndices.trimToSize();
		
		IntArrayList probableNeighborsRootGraphIndices = new IntArrayList();
		nodesIterator = source_net.nodesIterator();
		while(nodesIterator.hasNext()){
			probableNeighborsRootGraphIndices.add( ((CyNode)nodesIterator.next()).getRootGraphIndex() );
		}
		probableNeighborsRootGraphIndices.removeAll(tnodesRootGraphIndices);
		probableNeighborsRootGraphIndices.trimToSize();
		
		if (tnodesRootGraphIndices.size() == 0 || probableNeighborsRootGraphIndices.size() == 0) {
			return numAddedGraphObjects;
		}

		int[] pneighborsRootGraphIndices = probableNeighborsRootGraphIndices.elements();
		RootGraph rootGraph = source_net.getRootGraph();
		
		//int[] targetNodeIndices = target_net.getNodeIndicesArray();
		//for (int i = 0; i < targetNodeIndices.length; i++) {
			//targetNodeIndices[i] = target_net
				//	.getRootGraphNodeIndex(targetNodeIndices[i]);
		//}//for i

		IntArrayList nodesToRestore = new IntArrayList();
		for (int i = 0; i < pneighborsRootGraphIndices.length; i++) {
			int[] nodeIndices = new int[tnodesRootGraphIndices.size() + 1];
			System.arraycopy(tnodesRootGraphIndices.elements(), 0, nodeIndices, 0,
					tnodesRootGraphIndices.size());
			nodeIndices[tnodesRootGraphIndices.size()] = pneighborsRootGraphIndices[i];
			int[] connectingEdgesIndices = rootGraph.getConnectingEdgeIndicesArray(nodeIndices);
			// the connecting edges may contain edges that are NOT in source graph
			int numNeighbors = connectingEdgesIndices.length;
			//for(int j = 0; j < connectingEdgesIndices.length; j++){
				//if(!source_net.containsEdge(rootGraph.getEdge(connectingEdgesIndices[j]))){
					//connectingEdgesIndices[j] = 0;
					//numNeighbors--;
				//}
			//}//for j
			if(numNeighbors < minNumNeighbors){
				// skip
				continue;
			}
			nodesToRestore.add(pneighborsRootGraphIndices[i]);
		}//for i
		
		nodesToRestore.trimToSize();
		int[] restoredNodes = target_net.restoreNodes(nodesToRestore.elements());
		if(restoredNodes != null){
			numAddedGraphObjects[NUM_ADDED_NODES_INDEX] = restoredNodes.length;
		}
		int [] nago = GraphExpander.addInteractionsBetweenExistingNodes(source_net,target_net);
		numAddedGraphObjects[NUM_ADDED_EDGES_INDEX] = nago[NUM_ADDED_EDGES_INDEX];
		
		return numAddedGraphObjects;
	}//addNodesWithKneighbors

}//GraphExpander
