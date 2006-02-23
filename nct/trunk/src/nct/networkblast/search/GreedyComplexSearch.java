
//============================================================================
// 
//  file: GreedyComplexSearch.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
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
public class GreedyComplexSearch implements SearchGraph<String,Double> {

	/**
	 * This List keeps track of the paths used as seeds.
	 */
	private List<Graph<String,Double>> listOfPaths;
	
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
	 * Takes in the list of paths to be used as primary seeds and sets
	 * max and min sizes for complexes accordingly.
	 * @param paths List of paths to use as primary seeds.
	 * @param minSize Minimum complex size, also the size of the secondary seeds.
	 * @param maxSize Maximum allowed complex size.
	 */
	public GreedyComplexSearch(List<Graph<String,Double>> paths, int minSize, int maxSize) {
		assert(paths != null) : "paths was null!";
		listOfPaths = paths;
		maxComplexSize =  maxSize;
		minSeedSize = minSize;
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
	public List<Graph<String,Double>> searchGraph(Graph<String,Double>  graph, ScoreModel scoreObj) {

		if (graph == null || scoreObj == null) {
			return null;
		} else if (graph.numberOfNodes() == 0) {
			return new Vector<Graph<String,Double>>();
		}
	
		// Queue of seed graphs that are grown into complexes.
		List<Graph<String,Double>> queue = new LinkedList<Graph<String,Double>>();

		// Seed type 1
		// Copy whatever existing paths to the queue of seeds. 
		for (int i = 0; i < listOfPaths.size(); i++) 
			queue.add((Graph<String,Double>)listOfPaths.get(i).clone());
	
		// Seed type 2
		// Add minSeedSize node seeds to the queue.
		addSeeds( graph, queue, scoreObj);

		// contains the cumulative scores of the potential nodes
		Map<String, Double> potentialNodeScores = new Hashtable<String, Double>();  

		// contains the cumulative scores of the solution nodes
		Map<String, Double> solnNodeScores  = new Hashtable<String, Double>(); 

		// will contain the seed nodes (never remove)
		Set<String> seedNodes = new HashSet<String>(); 

		// will contain the solution nodes (can modify)
		Set<String> solnNodes = new HashSet<String>(); 

		// will contain the solution graphs to remove 
		Set<Graph<String,Double>> removalSet = new HashSet<Graph<String,Double>>();

		// will contain the best node & score from the neighbors
		String maxNode;  
		double maxScore;

		// begin growing each seed into a complex
		for(Graph<String,Double> soln: queue) {
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
			for (String seedNode : soln.getNodes()) {
				for (String testNode : graph.getNeighbors(seedNode)) {
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

			// Now do the extension (ie adding/subtracting nodes).
			// If maxSize hasn't been reached yet, removing a node shouldn't 
			// be worse than adding one.
			while (solnNodes.size() + seedNodes.size() <= maxComplexSize) {

				// If the best score is negative don't add the node to make the score worse!
				if (maxScore < 0) 
					break;
				
				// If we reach the max limit, check to see if adding a new node 
				// and removing an old node increases the score.  If it does, 
				// keep trying, else break out of the loop.
				if (solnNodes.size() + seedNodes.size() == maxComplexSize) {

					// Find the min scoring node.
					String minNode = null;
					double minScore = maxScore;
					for (String testNode : solnNodes) {
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
					
					for (String testNode : solnNodes) {
						isConnected = false;
						if (testNode.equals(minNode)) 
							continue;
						
						 for (String conNode : solnNodes) {
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
					for (String testNode : graph.getNeighbors(minNode)) {

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
				for (String testNode : graph.getNeighbors(maxNode)) {

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

				for (String testNode : potentialNodeScores.keySet()) {
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
			List<String> nodesList = new LinkedList<String>(solnNodes);

			// Now add all the solution nodes to the seed graph.
			for (int i = 0; i < nodesList.size(); i++) 
				soln.addNode(nodesList.get(i));

			// Add all solution nodes to the list.
			nodesList.addAll(seedNodes);
		
			log.fine("full Nodes List: " + nodesList);
			for (int i = 0; i < nodesList.size()-1; i++) {
				String nodei = nodesList.get(i);
				// Add solution nodes to the total score of the graph.
				if (!seedNodes.contains(nodei)) 
					soln.setScore( soln.getScore() + solnNodeScores.get(nodei) );

				// Add edges to soln graph.
				for (int j = i+1; j < nodesList.size(); j++) {
					String nodej = nodesList.get(j);
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
	private void addSeeds(Graph<String,Double> graph, List<Graph<String,Double>> queue, ScoreModel scoreObj) {

		// minSeedSize-1 because seed is already in the set
		String[] seedSet= new String[minSeedSize-1];  
		double[] seedScore = new double[minSeedSize-1];		
		double testScore;
		int pathSize;

		for (String seedNode : graph.getNodes()) {

			// Initialize seedSet and seedScore
			for (int i = 0; i < seedSet.length; i++) {
				seedSet[i] = null;
				seedScore[i] = -Double.MAX_VALUE;
			}

			pathSize = 0;
			// find 3 neighbor nodes
			// use a treeset? 
			for (String testNode : graph.getNeighbors(seedNode)) {
				testScore = scoreObj.scoreEdge(seedNode, testNode, graph);
				for (int i = 0; i < seedSet.length; i++) {
					// Assume more positive scores are better
					if (testScore > seedScore[i]) {
						// if not last element, shift all scores
						if (i != seedScore.length-1) {
							// shift all scores up
							for (int j = seedSet.length-1; j >= i; j--) {
								if (j == seedSet.length-1) {
									continue;
								} else {
									seedSet[j+1] = seedSet[j];
									seedScore[j+1] = seedScore[j];
								}
							}
						}
						seedSet[i] = testNode;
						seedScore[i] = testScore;
						pathSize++;
						break;
					}
				}
			}

			if (pathSize < minSeedSize - 1) 
				continue;

			// Now that we have our four node set, store them in a queue
			Graph<String,Double> soln = new BasicGraph<String,Double>(); 
			double tmpscore = 0;
			soln.addNode(seedNode);
			for (int i = 0; i < seedSet.length; i++) {
				soln.addNode(seedSet[i]);
				soln.addEdge(seedNode, seedSet[i], graph.getEdgeWeight(seedNode, seedSet[i]));
				tmpscore += seedScore[i];
			}
			soln.setScore(tmpscore);
			queue.add(soln);
		}
	}
}


