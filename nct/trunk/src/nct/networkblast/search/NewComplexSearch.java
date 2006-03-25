
//============================================================================
// 
//  file: NewComplexSearch.java
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
import nct.graph.Edge;

/**
 * This class implements the SearchGraph interface.  It implements the
 * greedy search algorithm described in the supplemental material of
 * Sharan, et al. 2005. "Conserved patterns of protein interaction in
 * mulitple species." PNAS, 102(6),1974-1979.
 * Complexes are defined as  branched or unbranched pathways invovling 
 * minSeedSize or more nodes.
 */
public class NewComplexSearch<NodeType extends Comparable<? super NodeType>> implements SearchGraph<NodeType,Double> {

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

	// contains the cumulative scores of the potential nodes
	private Map<NodeType, Double> potentialNodeScores; 

	// contains the cumulative scores of the solution nodes
	private Map<NodeType, Double> solnNodeScores;

	// will contain the seed nodes (never remove)
	private Set<NodeType> seedNodes; 

	// will contain the solution nodes (can modify)
	private Set<NodeType> solnNodes;

	// will contain the solution graphs to remove 
	private List<Graph<NodeType,Double>> returnList; 

	// will contain the best node & score from the neighbors
	private NodeType maxNode;  
	private double maxScore;

	private NodeType minNode;  
	private double minScore;

	private ScoreModel<NodeType,Double> scoreObj;
	private Graph<NodeType,Double> graph;

	private boolean createSeeds;

	/**
	 * Sets the max and min sizes for complexes. 
	 * @param minSize Minimum complex size, also the size of the secondary seeds.
	 * @param maxSize Maximum allowed complex size.
	 */
	public NewComplexSearch(int minSize, int maxSize) {
		this(minSize,maxSize,true,null);
	}

	public NewComplexSearch(int minSize, int maxSize, boolean create, List<Graph<NodeType,Double>> seeds) {
		maxComplexSize =  maxSize;
		minSeedSize = minSize;
		createSeeds = create;
		listOfSeeds = seeds;
		potentialNodeScores = new Hashtable<NodeType, Double>();  
		solnNodeScores  = new Hashtable<NodeType, Double>(); 
		seedNodes = new HashSet<NodeType>(); 
		solnNodes = new HashSet<NodeType>(); 
		returnList = new ArrayList<Graph<NodeType,Double>>();
		scoreObj = null;
		graph = null;
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

		this.scoreObj = scoreObj;
		this.graph = graph;

		returnList.clear();
	
		// Queue of seed graphs that are grown into complexes.
		List<Graph<NodeType,Double>> queue = new LinkedList<Graph<NodeType,Double>>();

		// Seed type 1
		// Copy whatever existing paths to the queue of seeds. 
		if ( listOfSeeds != null )
			for (int i = 0; i < listOfSeeds.size(); i++) 
				queue.add((Graph<NodeType,Double>)listOfSeeds.get(i).clone());
	
		// Seed type 2
		// Add minSeedSize node seeds to the queue.
		if ( createSeeds )
			addSeeds( queue );

//		System.out.println("queue size: " + queue.size());

		// Begin growing each seed into a complex.
		for(Graph<NodeType,Double> soln: queue) {
			//log.fine("Beginning Soln: " + soln);
//			System.out.println("Beginning Soln: " + soln);
//			for ( NodeType nn : soln.getNodes() )
//				System.out.println( nn );

			seedNodes.clear();
			seedNodes.addAll(soln.getNodes());
			potentialNodeScores.clear();
			solnNodeScores.clear();
			solnNodes.clear();
			solnNodes.addAll(soln.getNodes());
			updateSolnNodeScores();

			// First generate all the potential nodes & scores 
			// that could extend the seed graph. 
			// For every node in the solution so far (ie seeds):
			// get neighbors and scores.
			for (NodeType seedNode : soln.getNodes()) 
				for (NodeType testNode : graph.getNeighbors(seedNode)) 
					updatePotentialScores( testNode, seedNode );

//			System.out.println("initial solState");
//			printSolState();

			// This set is used because the following while loop can reach a
			// state where it cycles between a small number of alternative
			// solutions without ever breaking the loop.  We add hashcodes
			// of the solutions to this set and if we come across a solution
			// we've already hit, we break.
			Set<Integer> solnDesc = new HashSet<Integer>();

			// Now do the extension. 
			while (solnNodes.size() <= maxComplexSize && !potentialNodeScores.isEmpty() ) {
//				System.out.println("current solnNodes");
//				System.out.println(solnNodes);

				// If we've seen this combination of nodes before, break.
				Integer code = new Integer(solnNodes.hashCode());
				if ( solnDesc.contains( code ) )
					break;
				else
					solnDesc.add( code );


				findMaxPotentialNode();

				// If the best score is negative don't add the node to 
				// make the score worse!
				if (maxScore <= 0 || maxNode == null ) 
					break;
			
				// If we reach the max limit, check to see if adding a new node 
				// and removing an old node increases the score.  If it does, 
				// keep trying, else break out of the loop.
				if (solnNodes.size() == maxComplexSize) {
				
					// Find the min scoring node.
					if ( !findMinSolutionNode() )
						break;
			
					// Remove the minimum node from the solution.
//					System.out.println("removing node: " + minNode + " " + minScore);
					solnNodes.remove(minNode);

					// add minNode scores to potential scores. 
					for (NodeType solNode: solnNodes)
						updatePotentialScores(minNode,solNode);

				}

				// Add the node to the set and modify the list of scores 
				// to reflect the new network.
				solnNodes.add(maxNode);
				updateSolnNodeScores();
				potentialNodeScores.remove(maxNode);
//				System.out.println("adding node: " + maxNode + " " + maxScore);

				// Check neighbor nodes of maxNode. 
				// Update any existing potential nodes.
				// Add new potential nodes that are neighbors to maxNode, 
				// but not existing solution nodes.
				for (NodeType testNode : graph.getNeighbors(maxNode)) 
					updatePotentialScores( testNode, maxNode );

			}
			
//			System.out.println( "seed soln" );
//			System.out.println( soln.toString() );
//			printSolState();

			Graph<NodeType,Double> solnGraph = new BasicGraph<NodeType,Double>();

			// add solution nodes and edges to soln graph
			double solnGraphScore = 0.0;
			for ( NodeType solNode : solnNodes ) {
				solnGraph.addNode( solNode );
				solnGraphScore += scoreObj.scoreNode( solNode, graph );
				for ( NodeType solNeighbor : graph.getNeighbors( solNode ) ) {
					if ( !solnNodes.contains(solNeighbor) )
						continue;

					if ( solnGraph.addEdge(solNode,solNeighbor,scoreObj.scoreEdge(solNode,solNeighbor,graph)) )
						solnGraphScore += scoreObj.scoreEdge( solNode, solNeighbor, graph );
				}
			}
			solnGraph.setScore(solnGraphScore);

//			System.out.println("final soln ");
//			System.out.println(solnGraph.toString());
//			System.out.println();

			if (solnGraph.numberOfNodes() >= minSeedSize) 
				returnList.add(solnGraph);
//			else
//				System.out.println("minSeedSize " + minSeedSize + "  sol size: " + solnGraph.numberOfNodes());
		}

		return returnList;
	}

	public void setSeeds(List<Graph<NodeType,Double>> seeds) {
		listOfSeeds = seeds;
	}

	/**
	 * Creates secondary seeds and adds them to the seed queue.
	 */
	private void addSeeds(List<Graph<NodeType,Double>> queue) {

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
//			System.out.println(soln.toString());
			queue.add(soln);
		}
	}


	// Find the potential node with the highest score. 
	private void findMaxPotentialNode() {
		maxNode = null;
		maxScore = Double.MIN_VALUE;
		for (NodeType testNode: potentialNodeScores.keySet()) {
			double testScore = potentialNodeScores.get(testNode).doubleValue();
			if ( testScore > maxScore ) {
				maxNode = testNode;
				maxScore = testScore; 
			}
		}
	}

	// Find the solution node with the lowest score. 
	private boolean findMinSolutionNode() {
		minNode = null;
		minScore = Double.MAX_VALUE;
		for (NodeType testNode: solnNodeScores.keySet()) {
//			System.out.println("min checking " + testNode.toString());
			if ( seedNodes.contains(testNode) ) {
//				System.out.println("\tseed node");
				continue;
			}

			double testScore = solnNodeScores.get(testNode).doubleValue();

			if ( !removeMinPossible(testNode,testScore) ) 
				continue;

			if ( testScore < minScore ) { 
				minNode = testNode;
				minScore = testScore; 
			}
		}
		if ( minNode == null )
			return false;
		else
			return true;
	}

	private void updatePotentialScores( NodeType testNode, NodeType solNode ) {
		// ignore solution nodes 
		if (solnNodes.contains(testNode)) 
			return;

		if (!graph.isEdge(testNode,solNode)) 
			return;

		double testScore = scoreObj.scoreEdge(testNode, solNode, graph);

		// add new score to existing score for test node 
		if (potentialNodeScores.containsKey(testNode)) 
			testScore += potentialNodeScores.get(testNode).doubleValue();

		// add the node score the first time the node is checked
		else
			testScore += scoreObj.scoreNode(testNode,graph);

		potentialNodeScores.put(testNode, new Double(testScore));
	}

	private boolean removeMinPossible(NodeType minN, double minS) {

		// Stop searching if removing the min node doesn't increase the score.
		if (minS >= maxScore) {
//			System.out.println("\tmax(" + maxScore + ") less than min(" + minS + ")" );
			return false;  
		}

		// Check that the graph remains connected if we
		// remove the min.
		boolean isConnected = false;  
		for (NodeType testNode : solnNodes) {
			if (testNode.equals(minN)) 
				continue;
			isConnected = false;
			for (NodeType conNode : solnNodes) {
				if (conNode.equals(minN)) 
					continue;
				
				if (graph.isEdge(testNode, conNode)) {
					isConnected = true;
					break;
				}
			}
			// No node was found connecting testNode so
			// we can't remove the min.
			if (!isConnected) 
				break;
		}

		// Removing the min node will disconnect the existing
		// solution nodes.
		if ( !isConnected ) {
//			System.out.println("\tnot connected");
			return false;
		}

		// Check that the max node is still a neighbor
		// of a solution node besides the min.
		boolean maxConnected = false;
		for (NodeType testNode : solnNodes) {
			if (testNode.equals(minN)) 
				continue;
			if (graph.isEdge(testNode, maxNode)) {
				maxConnected = true;
				break;
			}
		}
				
			
		// The max node is only connected to the min node
		// among the solution nodes, therefore we can't
		// remove the min.
		if (!maxConnected) { 
//			System.out.println("\tmax connected");
			return false;
		}

//		System.out.println("\tlooks good!");

		return true;
	}

	private void subtractSolutionScore(NodeType removeNode, NodeType solNode) {
		if ( solNode.equals(removeNode) )
			return;
		if ( graph.isEdge( solNode, removeNode ) ) {
			double removeScore = scoreObj.scoreEdge(solNode,removeNode,graph);
			double solScore = solnNodeScores.get(solNode).doubleValue();
			solScore -= removeScore;
			solnNodeScores.put(solNode,new Double(solScore));
		}
	}

	private void updateSolnNodeScores() {
//		System.out.println("solNodes about to be updated " + solnNodes);
		for ( NodeType testNode : solnNodes ) {
//			System.out.println("updating solNode: " + testNode);
			double testScore = scoreObj.scoreNode(testNode,graph);
			for ( NodeType conNode : solnNodes ) {

				if ( testNode.equals(conNode) )
					continue;

				if ( graph.isEdge(testNode,conNode) )
					testScore += scoreObj.scoreEdge(testNode, conNode, graph);

			}
			solnNodeScores.put(testNode, new Double(testScore));
		}
	}

	private void printSolState() {
			System.out.println( "\nsolnNodes" );
			for ( NodeType solNode : solnNodes ) 
				System.out.println( "	" + solNode.toString() );
			System.out.println( "solnNodeScores" );
			for ( NodeType solNode : solnNodeScores.keySet() ) 
				System.out.println( "	" + solNode.toString() + " " + solnNodeScores.get(solNode).toString());
			System.out.println( "potentialNodeScores" );
			for ( NodeType solNode : potentialNodeScores.keySet() ) 
				System.out.println( "	" + solNode.toString() + " " + potentialNodeScores.get(solNode).toString());
			System.out.println();
	}
}


