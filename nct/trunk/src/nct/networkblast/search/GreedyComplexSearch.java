
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
 * This class implements the SearchGraph interface.  It provides for a greedy 
 * search algorithm based off the original C code. Complexes are defined as 
 * branched or unbranched pathways invovling minSeedSize or more nodes.
 */
public class GreedyComplexSearch implements SearchGraph<String,Double> {

    /**
     * this List keeps track of the paths used as seeds
     */
    private List<Graph<String,Double>> listOfPaths;
    
    /**
     * Maximum size of the complexes allowed.
     */
    private int maxComplexSize;
    
    /**
     * Minimum seed size for second iteration growth
     */
    private int minSeedSize;

    /**
     * The logger object that we will log to.
     */
    private static Logger log = Logger.getLogger("networkblast");

    /**
     * Takes in the list of paths to be used as secondary seeds and sets
     * max and min sizes for complexes accordingly.
     * @param paths List of paths to use as secondary seeds
     * @param minSize size of secondary seeds and also smallest size of complexes returned
     * @param maxSize maximum size allowed for complexes
     */
    public GreedyComplexSearch(List<Graph<String,Double>> paths, int minSize, int maxSize) {
	assert(paths != null) : "paths was null!";
	listOfPaths = paths;
	maxComplexSize =  maxSize;
	minSeedSize = minSize;
    }

    /**
     * Search the graph for a complex between minSeedSize and maxSeedSize big.
     * It utilizes the list of paths already found as another seed set and 
     * appends the results from that search to the list found using its own 
     * heuristic.
     * This greedy search works in the following manner:  given a list of nodes
     * N, iterate through each node and find its best (minSeedSize-1) 
     * neighbors.  "Best" is defined as the sum of the scores returned from 
     * the scoreObj (which will currently be a Loglikelihood score).  Given 
     * every combination based on a list of nodes N, one should have at most N
     * groups of four nodes.  Define this list of four node seeds as M, a seed 
     * set.  Given each seedset M, see if adding or removing 1 *new* node will 
     * increase the score, and then do the corresponding action.  Let this 
     * occur until the network cannot add/delete any nodes or grows until
     * maxComplexSize.  Then, try using each SubGraph path in listOfPaths as a 
     * seed.  Store all unique results in a LinkedList to be returned at the 
     * conclusion of this function.
     * @param graph The Graph object to search and score
     * @param scoreObj The scoreObj(/algorithm) to use
     * @return null if an invalid graph is passed in, otherwise the list of SubGraph complexes
     */
    public List<Graph<String,Double>> searchGraph(Graph<String,Double>  graph, ScoreModel scoreObj) {

	if (graph == null || scoreObj == null) {
	    return null;
	} else if (graph.numberOfNodes() == 0) {
	    return new Vector<Graph<String,Double>>();
	}
	
	String[] seedSet= new String[minSeedSize-1];  // minSeedSize-1 because seed is already in the set
	double[] seedScore = new double[minSeedSize-1];	
	double testScore;
	int i;
	int j;

	LinkedList<Graph<String,Double>> queue = new LinkedList<Graph<String,Double>>();

	int pathSize;
      
	// Copy graphs (paths) from listOfPaths
	for (i = 0; i < listOfPaths.size(); i++) 
	    queue.add((Graph<String,Double>)listOfPaths.get(i).clone());
	
	// get 4 node sets
	for (String seedNode : graph.getNodes()) {

	    // Initialize seedSet and seedScore
	    for (i = 0; i < seedSet.length; i++) {
		seedSet[i] = null;
		seedScore[i] = -Double.MAX_VALUE;
	    }

	    pathSize = 0;
	    // find 3 nieghbor nodes
	    // use a treeset? 
	    for (String testNode : graph.getNeighbors(seedNode)) {
		testScore = scoreObj.scoreEdge(seedNode, testNode, graph);
		for (i = 0; i < seedSet.length; i++) {
		    // Assume more positive scores are better
		    if (testScore > seedScore[i]) {
			// if not last element, shift all scores
			if (i != seedScore.length-1) {
			    // shift all scores up
			    for (j = seedSet.length-1; j >= i; j--) {
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
	    for (i = 0; i < seedSet.length; i++) {
		soln.addNode(seedSet[i]);
		soln.addEdge(seedNode, seedSet[i], graph.getEdgeWeight(seedNode, seedSet[i]));
		tmpscore += seedScore[i];
	    }
	    soln.setScore(tmpscore);
	    queue.add(soln);
	}

	// free memory
	seedSet = null;
	seedScore = null;

	// Now extend all the solutions

	// contains the cumulative scores of the potential nodes
	Hashtable<String, Double> potentialNodeScores = new Hashtable<String, Double>();  

	// contains the cumulative scores of the solution nodes
	Hashtable<String, Double> solnNodeScores  = new Hashtable<String, Double>(); 

	// will contain the seed nodes (never remove)
	HashSet<String> seedNodes = new HashSet<String>(); 

	// will contain the solution nodes (can modify)
	HashSet<String> solnNodes = new HashSet<String>(); 

	String maxNode;  // will contain the best node & score from the neighbors
	double maxScore;
	Set<Graph<String,Double>> removalSet = new HashSet<Graph<String,Double>>();
	for(Graph<String,Double> soln: queue) {
 	    log.fine("Beginning Soln: " + soln);

	    maxNode = null;
	    maxScore = -Double.MAX_VALUE;

	    // first generate all the potential nodes & scores to add
	    seedNodes.clear();
	    seedNodes.addAll(soln.getNodes());
	    potentialNodeScores.clear();
	    solnNodeScores.clear();
	    solnNodes.clear();
	    // for every node in the solution so far (ie seeds):
	    // get neighbors and scores
	    for (String seedNode : soln.getNodes()) {
		for (String testNode : graph.getNeighbors(seedNode)) {
		    if (seedNodes.contains(testNode)) 
			continue; // skip seed nodes
		    
		    testScore = scoreObj.scoreEdge(seedNode, testNode, graph);
		    if (testScore > maxScore) {
			maxNode = testNode;
			maxScore = testScore;
		    }
		    if (potentialNodeScores.contains(testNode)) {
			testScore += potentialNodeScores.get(testNode).doubleValue();
		    }
		    potentialNodeScores.put(testNode, new Double(testScore));
		}
	    }

	    // Now do the extension (ie adding/subtracting nodes)
	    // if maxSize hasn't been reached yet, removing a node shouldn't 
	    // be worse than adding one
	    while (solnNodes.size() + seedNodes.size() <= this.maxComplexSize) {

		// if the best score is negative don't add the node to make the score worse!
		if (maxScore < 0) 
		    break;
		
		// if we reach the max limit, check to see if adding a node 
		// and removing one node increases the score.  if it does, 
		// keep trying, else break out of the loop
		if (solnNodes.size() + seedNodes.size() == this.maxComplexSize) {

		    // ensure the minimum node removed is not the one linking the maximum node
		    boolean isConnected = false;  

		    String minNode = null;
		    double minScore = maxScore;
		    for (String testNode : solnNodes) {
			testScore = solnNodeScores.get(testNode).doubleValue();
			if (testScore < minScore) {
			    minNode = testNode;
			    minScore = testScore;

			}
		    }

		    // stop searching if removing/adding 1 node doesn't increase the score
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
			
		    // check to see if minimum node removed will break the graph
		    if (!isConnected) 
			break;
		    
		    // else remove the minimum node from the solution, subtract it 
		    // from the total score here and add it back to the potential scores
		    solnNodes.remove(minNode);

		    for (String testNode : graph.getNeighbors(minNode)) {
			// modify total score here (ie add to total score, subtract from potential score)
			if (seedNodes.contains(testNode)) 
			    continue;  // seed nodes are constant
			else {
			    testScore = scoreObj.scoreEdge(minNode, testNode, graph);
			    if (solnNodeScores.contains(testNode)) {
				testScore -= solnNodeScores.get(testNode).doubleValue();
				solnNodeScores.put(testNode, new Double(testScore));
			    } else {
				if ( potentialNodeScores.contains(testNode) )
					testScore += potentialNodeScores.get(testNode).doubleValue();
				potentialNodeScores.put(testNode, new Double(testScore));
			    }
			}
		    }

		    // transfer score from solution scores to potential scores
		    potentialNodeScores.put(minNode, solnNodeScores.get(minNode));
		    solnNodeScores.remove(minNode);
		}
 
		// otherwise add the node to the set and modify the list of scores 
		// to reflect the new network
 		log.fine("Adding node: " + maxNode);
		solnNodes.add(maxNode);
		solnNodeScores.put(maxNode, new Double(maxScore));
		potentialNodeScores.remove(maxNode);
		// check neighbor nodes of node just added, add score to solution scores 
		// and remove from potential scores
		for (String testNode : graph.getNeighbors(maxNode)) {
		    // modify total score here (ie add to total score, subtract from potential score)
		    if (seedNodes.contains(testNode)) 
			continue;
		    else {
			testScore = scoreObj.scoreEdge(maxNode, testNode, graph);
			if (solnNodes.contains(testNode)) {
			    testScore += solnNodeScores.get(testNode).doubleValue();
			    solnNodeScores.put(testNode, new Double(testScore));
			} else if (potentialNodeScores.contains(testNode)) {
			    testScore -= potentialNodeScores.get(testNode).doubleValue();
			    potentialNodeScores.put(testNode, new Double(testScore));
			} else { // not in potentialNodeScores, so add it
			    potentialNodeScores.put(testNode, new Double(testScore));
			}
		    }
		}
		// Now loop through and find the next best node from the potential nodes
		maxNode = null;
		maxScore = -Double.MAX_VALUE;

		// if all potential nodes in the graph have been searched added
		if (potentialNodeScores.keySet().isEmpty()) 
			break;
		
		for (String testNode : potentialNodeScores.keySet()) {
		    testScore = potentialNodeScores.get(testNode).doubleValue();
		    if (testScore > maxScore) {
			maxNode = testNode;
			maxScore = testScore;
		    }
		}
	    }
	    
	    // Now add all the solutions to the graph with edges
 	    log.fine("soln Nodes: " + solnNodes);
	    List<String> nodesList = new LinkedList<String>(solnNodes);

	    for (i = 0; i < nodesList.size(); i++) 
		soln.addNode(nodesList.get(i));

	    // Add all solution nodes to the list
	    // now add edges
	    nodesList.addAll(seedNodes);
	
 	    log.fine("full Nodes List: " + nodesList);
	    for (i = 0; i < nodesList.size()-1; i++) {
		if (!seedNodes.contains(nodesList.get(i))) 
		    soln.setScore( soln.getScore() + solnNodeScores.get(nodesList.get(i)));

		for (j = i+1; j < nodesList.size(); j++) {
		    if (graph.isEdge(nodesList.get(i), nodesList.get(j))) {
		    	soln.addNode(nodesList.get(i));
		    	soln.addNode(nodesList.get(j));
			soln.addEdge(nodesList.get(i), nodesList.get(j), 
			             scoreObj.scoreEdge(nodesList.get(i), nodesList.get(j), graph));
		    }
		}
	    }

	    if (soln.numberOfNodes() < minSeedSize) 
		removalSet.add(soln);
	}
	log.info("removing " + removalSet.size() + " complexes that are deemed too small");	
	queue.removeAll( removalSet );
		
	return queue;
    }
}


