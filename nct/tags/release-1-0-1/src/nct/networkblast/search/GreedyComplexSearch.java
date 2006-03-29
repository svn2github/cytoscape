
//============================================================================
// 
//  file: GreedyComplexSearch.java
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



package nct.networkblast.search;

import java.util.*;
import java.util.logging.Logger;
import nct.networkblast.score.ScoreModel;
import nct.graph.basic.BasicGraph;
import nct.graph.Graph;

/**
 * This class implements the SearchGraph interface.  It implements the
 * greedy search algorithm described in the supplemental material of
 * Sharan, et al. 2005. "Conserved patterns of protein interaction in
 * mulitple species." PNAS, 102(6),1974-1979.
 * Complexes are defined as  branched or unbranched pathways invovling 
 * minSeedSize or more nodes.
 */
public class GreedyComplexSearch<NodeType extends Comparable<? super NodeType>> implements SearchGraph<NodeType,Double> {

	/**
	 * This List keeps track of the graphs (usually paths) used as seeds.
	 */
	private List<Graph<NodeType,Double>> listOfSeeds;
	
	/**
	 * Maximum size of the complexes allowed.
	 */
	private int maxComplexSize;
	
	/**
	 * Minimum size of the complexes allowed and secondary seed size.
	 */
	private int minSeedSize;

	/**
	 * The logger object that we will log to.
	 */
	private static Logger log = Logger.getLogger("networkblast");

	/**
	 * Sets the max and min sizes for complexes. 
	 * @param minSize Minimum complex size, also the size of the secondary seeds.
	 * @param maxSize Maximum allowed complex size.
	 */
	public GreedyComplexSearch(int minSize, int maxSize) {
		maxComplexSize =  maxSize;
		minSeedSize = minSize;
		listOfSeeds = null;
	}

	/**
	 * Search the graph for a complex between minSeedSize and maxComplexSize 
	 * nodes in size.
	 * <br/>
	 * The algorithm uses two sets of seeds.  First it utilizes the list of 
	 * paths (if any) already found in the path search.  
	 * <br/> 
	 * Second, it iterates over all nodes in the input graph and builds 
	 * seeds of size minSeedSize around each node by adding the node's 
	 * best neighbors to the seed. "Best" is defined as the sum of the scores 
	 * returned from the scoreObj.  Once the seed is constructed, it is
	 * appened to the overall list of seeds.
	 * <br/> 
	 * Now begin the greedy construction of complexes.
	 * Given each seed, see if adding or removing 1 *new* node (seed
	 * nodes are invariant and can't be removed) will increase the score, 
	 * and then do the corresponding action. Continue until the network cannot 
	 * add/delete any nodes that increase the score or the complex grows until
	 * maxComplexSize.  
	 *
	 * @param graph The Graph object to search for complexes.
	 * @param scoreObj The score algorithm to use.
	 * @return The list of complexes found. 
	 */
	@SuppressWarnings("unchecked") // for a call to Graph<N,W>.clone() below...
	public List<Graph<NodeType,Double>> searchGraph(Graph<NodeType,Double>  graph, ScoreModel<NodeType,Double> scoreObj) {

		if (graph == null || scoreObj == null) {
			return null;
		} else if (graph.numberOfNodes() == 0) {
			return new Vector<Graph<NodeType,Double>>();
		}
	
		// Queue of seed graphs that are grown into complexes.
		List<Graph<NodeType,Double>> queue = new LinkedList<Graph<NodeType,Double>>();

		// Seed type 1
		// Copy whatever existing paths to the queue of seeds. 
		if ( listOfSeeds != null )
			for (int i = 0; i < listOfSeeds.size(); i++) 
				queue.add((Graph<NodeType,Double>)listOfSeeds.get(i).clone());
	
		// Seed type 2
		// Add minSeedSize node seeds to the queue.
		addSeeds( graph, queue, scoreObj);

		// contains the cumulative scores of the potential nodes
		Map<NodeType, Double> potentialNodeScores = new Hashtable<NodeType, Double>();  

		// contains the cumulative scores of the solution nodes
		Map<NodeType, Double> solnNodeScores  = new Hashtable<NodeType, Double>(); 

		// will contain the seed nodes (never remove)
		Set<NodeType> seedNodes = new HashSet<NodeType>(); 

		// will contain the solution nodes (can modify)
		Set<NodeType> solnNodes = new HashSet<NodeType>(); 

		// will contain the solution graphs to remove 
		Set<Graph<NodeType,Double>> removalSet = new HashSet<Graph<NodeType,Double>>();

		// will contain the best node & score from the neighbors
		NodeType maxNode;  
		double maxScore;

		// begin growing each seed into a complex
		for(Graph<NodeType,Double> soln: queue) {
			log.fine("Beginning Soln: " + soln);

			maxNode = null;
			maxScore = -Double.MAX_VALUE;
			seedNodes.clear();
			seedNodes.addAll(soln.getNodes());
			potentialNodeScores.clear();
			solnNodeScores.clear();
			solnNodes.clear();

			// First generate all the potential nodes & scores 
			// that could extend the seed graph. 
			// For every node in the solution so far (ie seeds):
			// get neighbors and scores.
			for (NodeType seedNode : soln.getNodes()) {
				for (NodeType testNode : graph.getNeighbors(seedNode)) {
					if (seedNodes.contains(testNode)) 
						continue; // skip seed nodes
					
					double testScore = scoreObj.scoreEdge(seedNode, testNode, graph);
					if (testScore > maxScore) {
						maxNode = testNode;
						maxScore = testScore;
					}

					// for when a test node is a neighbor of more than one seed node
					if (potentialNodeScores.containsKey(testNode)) 
						testScore += potentialNodeScores.get(testNode).doubleValue();
					
					potentialNodeScores.put(testNode, new Double(testScore));
				}
			}

			// This set is used because the following while loop can reach a
			// state where it cycles between a small number of alternative
			// solutions without ever breaking the loop.  We add hashcodes
			// of the solutions to this set and if we come across a solution
			// we've already hit, we break.
			Set<String> nodeDesc = new HashSet<String>();

			// Now do the extension (ie adding/subtracting nodes).
			// If maxSize hasn't been reached yet, removing a node shouldn't 
			// be worse than adding one.
			while (solnNodes.size() + seedNodes.size() <= maxComplexSize) {

				// See if this solution has already been tried. 
				String code = Integer.toString( solnNodes.hashCode() ) + 
				              Integer.toString( seedNodes.hashCode() );
				if ( nodeDesc.contains( code ) ) {
					log.fine("breaking on code " + code.toString() );
					break;
				}
				else
					nodeDesc.add( code );

				// If the best score is negative don't add the node to make the score worse!
				if (maxScore < 0) 
					break;
				
				// If we reach the max limit, check to see if adding a new node 
				// and removing an old node increases the score.  If it does, 
				// keep trying, else break out of the loop.
				if (solnNodes.size() + seedNodes.size() == maxComplexSize) {

					// Find the min scoring node.
					NodeType minNode = null;
					double minScore = maxScore;
					for (NodeType testNode : solnNodes) {
						double testScore = solnNodeScores.get(testNode).doubleValue();
						if (testScore < minScore) {
							minNode = testNode;
							minScore = testScore;

						}
					}
					
					// To ensure that the minimum node removed is not the one 
					// linking the maximum node.
					boolean isConnected = false;  

					// Stop searching if removing/adding 1 node doesn't increase the score.
					if (minNode == null) 
						break;  
					
					for (NodeType testNode : solnNodes) {
						isConnected = false;
						if (testNode.equals(minNode)) 
							continue;
						
						 for (NodeType conNode : solnNodes) {
							if (conNode.equals(minNode)) 
								continue;
							
							if (graph.isEdge(testNode, conNode)) {
								isConnected = true;
								break;
							}
						}
						if (!isConnected) 
							break;
					}
						
					// Check to see if minimum node removed will break the graph.
					if (!isConnected) 
						break;
					
					// Remove the minimum node from the solution.
					solnNodes.remove(minNode);

					// Subtract minimum node score from the total score here and add 
					// it back to the potentialk scores.
					for (NodeType testNode : graph.getNeighbors(minNode)) {

						// ignore seed nodes 
						if (seedNodes.contains(testNode)) 
							continue;  

						else {
							double testScore = scoreObj.scoreEdge(minNode, testNode, graph);
							if (solnNodeScores.containsKey(testNode)) {
								// remove from total score 
								testScore -= solnNodeScores.get(testNode).doubleValue();
								solnNodeScores.put(testNode, new Double(testScore));
							} else {
								// add to potential score 
								if ( potentialNodeScores.containsKey(testNode) )
									testScore += potentialNodeScores.get(testNode).doubleValue();
								potentialNodeScores.put(testNode, new Double(testScore));
							}
						}
					}

					// Transfer score from solution scores to potential scores.
					potentialNodeScores.put(minNode, solnNodeScores.get(minNode));
					solnNodeScores.remove(minNode);

				}
 
				// Otherwise (we're not at the number of allowable nodes)
				// add the node to the set and modify the list of scores 
				// to reflect the new network.
				log.fine("Adding node: " + maxNode);
				solnNodes.add(maxNode);
				solnNodeScores.put(maxNode, new Double(maxScore));
				potentialNodeScores.remove(maxNode);

				// Check neighbor nodes of node just added, add score to solution scores 
				// and remove from potential scores.
				for (NodeType testNode : graph.getNeighbors(maxNode)) {

					// ignore seed nodes
					if (seedNodes.contains(testNode)) 
						continue;

					else {
						double testScore = scoreObj.scoreEdge(maxNode, testNode, graph);
						if (solnNodes.contains(testNode)) {
							// add to total score
							testScore += solnNodeScores.get(testNode).doubleValue();
							solnNodeScores.put(testNode, new Double(testScore));
						} else {
							// remove from potential score
							if (potentialNodeScores.containsKey(testNode)) 
								testScore -= potentialNodeScores.get(testNode).doubleValue();
							potentialNodeScores.put(testNode, new Double(testScore));
						}
					}
				}

				// If all potential nodes in the graph have been searched added.
				if (potentialNodeScores.keySet().isEmpty()) 
						break;
				
				// Now loop through and find the next best node from the potential nodes.
				maxNode = null;
				maxScore = -Double.MAX_VALUE;

				for (NodeType testNode : potentialNodeScores.keySet()) {
					double testScore = potentialNodeScores.get(testNode).doubleValue();
					if (testScore > maxScore) {
						maxNode = testNode;
						maxScore = testScore;
					}
				}

			}
			
			log.fine("soln Nodes: " + solnNodes);

			// Create a new list of nodes that will eventually contain
			// both the seed nodes and solution nodes.
			List<NodeType> nodesList = new LinkedList<NodeType>(solnNodes);

			// Now add all the solution nodes to the seed graph.
			for (int i = 0; i < nodesList.size(); i++) 
				soln.addNode(nodesList.get(i));

			// Add all solution nodes to the list.
			nodesList.addAll(seedNodes);
		
			log.fine("full Nodes List: " + nodesList);
			for (int i = 0; i < nodesList.size()-1; i++) {
				NodeType nodei = nodesList.get(i);
				// Add solution nodes to the total score of the graph.
				if (!seedNodes.contains(nodei)) 
					soln.setScore( soln.getScore() + solnNodeScores.get(nodei) );

				// Add edges to soln graph.
				for (int j = i+1; j < nodesList.size(); j++) {
					NodeType nodej = nodesList.get(j);
					if (graph.isEdge(nodei, nodej)) 
						soln.addEdge(nodei, nodej, scoreObj.scoreEdge(nodei, nodej, graph));
				}
			}

			if (soln.numberOfNodes() < minSeedSize) 
				removalSet.add(soln);
		}

		log.info("removing " + removalSet.size() + " complexes that are deemed too small");		
		queue.removeAll( removalSet );
				
		return queue;
	}

	/**
	 * Creates secondary seeds and adds them to the seed queue.
	 */
	private void addSeeds(Graph<NodeType,Double> graph, List<Graph<NodeType,Double>> queue, ScoreModel<NodeType,Double> scoreObj) {

		// minSeedSize-1 because seed is already in the set
		Vector<NodeType> seedSet= new Vector<NodeType>();  
		Vector<Double> seedScore = new Vector<Double>();		

		for (NodeType seedNode : graph.getNodes()) {

			seedSet.clear();
			seedScore.clear();
			int numAdded = 0;

			// Check each neighbor's score and add the neighbor 
			// to the appropriate place in the list of scores. 
			for (NodeType testNode : graph.getNeighbors(seedNode)) {
				double testScore = scoreObj.scoreEdge(seedNode, testNode, graph);
				boolean added = false;
				for ( int i = 0; i < seedScore.size(); i++ ) {
					if ( testScore > seedScore.get(i).doubleValue() ) {
						seedScore.insertElementAt(testScore,i);
						seedSet.insertElementAt(testNode,i);
						added = true;
						break;
					}
				}

				if ( !added ) {
					seedScore.add(testScore);
					seedSet.add(testNode);
				}
				numAdded++;
			}

			if (numAdded < minSeedSize - 1) 
				continue;

			// Now that we have our (at least) minSeedSize node set, 
			// create a graph and store it in a queue.
			Graph<NodeType,Double> soln = new BasicGraph<NodeType,Double>(); 
			double tmpscore = 0;
			soln.addNode(seedNode);
			for (int i = 0; i < minSeedSize - 1; i++) {
				NodeType node = seedSet.get(i);
				soln.addNode(node);
				soln.addEdge(seedNode, node, graph.getEdgeWeight(seedNode, node));
				tmpscore += seedScore.get(i);
			}
			soln.setScore(tmpscore);
			System.out.println(soln.toString());
			queue.add(soln);
		}
	}


	public void setSeeds(List<Graph<NodeType,Double>> seeds) {
		listOfSeeds = seeds;
	}
}


