/* vim: set ts=2: */

//============================================================================
// 
//  file: CompatColorCodingPathSearch.java
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



package nct.networkblast;

import java.util.*;
import java.util.logging.Logger;
import java.lang.reflect.Array;
import nct.graph.*;
import nct.score.*;
import nct.search.*;

/**
 * This class implements a color coding algorithm to search for pathways 
 * of size n in a given graph using a scoring object. See: Scott, et al., 
 * 2005, Efficient Algorithms for Detecting Signaling Pathways in Protein 
 * Interaction Networks, Lecture Notes in Computer Science, vol. 3500.
 */
public class CompatColorCodingPathSearch<NodeType extends Comparable<? super NodeType>> 
	implements SearchGraph<NodeType,Double>/*, Monitorable*/ {

	/**
	 * The default epsilon value.
	 * Epsilon is probability that we will miss a best path if
	 * we iterate the correct number of times.
	 */
	 public static double DEFAULT_EPSILON = 0.0001;
	 public static int NUM_PATHS_PER_NODE = 3;
   
	/**
	 * The length of path that we're looking for.
	 */
	private int pathSize;

	/**
	 * The pseudo random number generator used to create
	 * colors.
	 */
	private Random rand;

	/**
	 * The probability that we will miss a best path if
	 * we iterate the correct number of times.
	 */
	private double eps; 

	/**
	 * A mapping of nodes to ints representing colors.
	 */
	private Map<NodeType,Integer> nodeColorMap; 

	/**
	 * A mapping of nodes to array indices (for W and path).
	 */
	private Map<NodeType,Integer> nodeIndexMap; 

	/**
	 * A matrix that stores the best score for the given node
	 * and color set to that point. The first  dimension indices 
	 * are the set of nodes and the second dimension 
	 * indices are the possible color sets.
	 */
	//private double[][] W;
	private List<List<PathGraph<NodeType>>> W;

	/**
	 * A matrix containing the last node used to calculate the score
	 * in the cell with the same index in the W matrix. The first
	 * dimension indices are the set of nodes and the second dimension 
	 * indices are the possible color sets.
	 */
	//private NodeType[][] path;
	private List<List<NodeType>> path;

	/**
	 * An int representation of the set of colors where each color is 
	 * represented by one bit. 
	 */
	private int colorSet;

	/**
	 * The current graph we're operating on.
	 */
	private Graph<NodeType,Double> graph;

	/**
	 * The ScoreModel used for calculating distances.
	 */
	private ScoreModel<NodeType,Double> scoreObj;

	/**
	 * The maximum number of solutions we will return.
	 */
	private int numSolutions = NUM_PATHS_PER_NODE;

	/**
	 * The logging object we will write to. 
	 */
	private static Logger log = Logger.getLogger("networkblast");	

	/**
	 * A set of nodes that can be used to constrain the contents of paths.
	 * If the set is not null, then at least minConstraintNodes and at
	 * most maxConstraintNodes from this set must be present in a path. If
	 * the set is null, the constraint is ignored. The constraint is null
	 * by default.
	 */
	private Set<NodeType> constraintSet; 

	/**
	 * The maximum number of nodes from the constraintSet allowed in a given path.
	 */
	private int maxConstraintNodes; 

	/**
	 * The minimum number of nodes from the constraintSet required in a given path.
	 */
	private int minConstraintNodes; 

	/**
	 * A map of nodes to the maximum allowed segment of the path that the node is
	 * allowed to be present in. 
	 */
	Map<NodeType,Integer> maxSegmentMap;

	/**
	 * A map of nodes to the minimum allowed segment of the path that the node is
	 * allowed to be present in. 
	 */
	Map<NodeType,Integer> minSegmentMap;

	/** 
	 *
	 */
	Set<NodeType> beginSet;

	/** 
	 *
	 */
	Set<NodeType> endSet;

	/**
	 * Used for tracking the progress of searchGraph()
	 */
	//protected Monitor monitor = null;

	/**
	 * @param size the integer size of paths to search for
	 */
	public CompatColorCodingPathSearch(int size) {
		this(size,0);
	}

	/**
	 * @param size The lengths of the paths to search for.
	 * @param  numSols The maximum number of solution paths PER NODE to return.
	 */
	public CompatColorCodingPathSearch(int size,int numSols) {
		this(size,numSols,CompatColorCodingPathSearch.DEFAULT_EPSILON);
	}

	/**
	 * @param size The lengths of the paths to search for.
	 * @param  numSols The maximum number of solution paths PER NODE to return.
	 * @param  epsilon The probability that we won't find a best path. The
	 * lower the probability, the longer this algorithm takes, however the
	 * length increases logarithmically, so .01 isn't orders of magnitude
	 * faster than .0000001.
	 * <i>SECRET FEATURE</i>:  If the value of epsilon is greater than 1, then we'll 
	 * instead use that (integer) number as the number of iterations for the 
	 * algorithm instead of rigorously calculating the number of iterations.
	 * This is an easy way of speeding up this search. Since the majority of 
	 * of best paths are found in the first several iterations, this is a simple
	 * heuristic.  Remember: you WILL miss best paths using this technique.
	 * Any value X where 1 &lt; X &lt; 20 will emit a warning indicating that
	 * the number of iterations you have selected is too small (although the
	 * algorithm will still run). <b>In summary, if you've got the time, be
	 * rigorous and use a small epsilon (e.g. .00001), if not, then use a
	 * reasonable number of iterations (e.g. 20).</b>
	 */
	public CompatColorCodingPathSearch(int size,int numSols, double epsilon) {
		pathSize = size;
		numSolutions = numSols;
		eps = epsilon;
		rand = new Random((long)pathSize); // arbitrarily seed the prng with the path size
		colorSet = (1 << pathSize) - 1; 
		nodeColorMap = new HashMap<NodeType,Integer>();
		nodeIndexMap = new HashMap<NodeType,Integer>(); 
		log.info("pathsize: " + size + " num solutions: " + numSols + " epsilon: " + epsilon);
		constraintSet = null;
		minConstraintNodes = 0;
		maxConstraintNodes = 0;

		maxSegmentMap = null;
		minSegmentMap = null;

		beginSet = null;
		endSet = null;
	}

	/**
	 * Sets a constraint on a path such that the path must contain a node (or nodes) in 
	 * the specified constraint set. The first (smallest) segment begins at 0 and
	 * increments by 1 from there. The last segment is the path size - 1. 
	 * @param constraintSet The set of nodes that constrain the paths.
	 * @param minNodes The minimum number of nodes from the constraint set that must
	 * exist in the path.
	 * @param maxNodes The maximum number of nodes from the constraint set that may
	 * exist in the path.
	 */
	public void setConstraint(Set<NodeType> constraintSet, int minNodes, int maxNodes) { 
		this.constraintSet = constraintSet;
		minConstraintNodes = minNodes;
		maxConstraintNodes = maxNodes;
	}

	/**
	 * Sets the segments used for producing ordered paths.
	 * @param maxSegmentMap A map of nodes to the maximum segment number the node 
	 * is allowed in.
	 * @param minSegmentMap A map of nodes to the minimum segment number the node 
	 * is allowed in.
	 */
	public void setSegments(Map<NodeType,Integer> maxSegmentMap, Map<NodeType,Integer> minSegmentMap) {	
		this.maxSegmentMap = maxSegmentMap;
		this.minSegmentMap = minSegmentMap;
	}

	/**
	 * This methods defines a set of nodes from which one must BEGIN every path.
	 * @param beginSet The set containing the beginning nodes.
	 */
	public void setBeginNodes(Set<NodeType> beginSet) {
		this.beginSet = beginSet;
	}

	/**
	 * This methods defines a set of nodes from which one must END every path.
	 * @param endSet The set containing the ending nodes.
	 */
	public void setEndNodes(Set<NodeType> endSet) {
		this.endSet = endSet;
	}

	/**
	 * This function searches for pathways in the given graph of size pathSize
	 * using scoreObj and the color coding algorithm described by Scott, et al,
	 * 2005, Efficient Algorithms for Detecting Signaling  Pathways in Protein 
	 * Interaction Networks, Lecture Notes in Computer Science, vol 3500.
	 * @param graph the Graph object to search
	 * @param scoreObj the scoring object (ie algorithm) to use to determine the 
	 * the edgeweight between two nodes.
	 * @return a List of Graph objects that represent the found paths, or null 
	 * if the given arguments are invalid.
	 */
	 @SuppressWarnings("unchecked") // for clone call below
	public List<Graph<NodeType,Double>> searchGraph(Graph<NodeType,Double> graph, ScoreModel<NodeType,Double> scoreObj) {
		Long beginTime = System.currentTimeMillis();

		this.graph = graph;
		this.scoreObj = scoreObj;

		List<Graph<NodeType,Double>> graphList = new ArrayList<Graph<NodeType,Double>>();

		if (graph == null || scoreObj == null) 
			return null;
		else if (graph.numberOfEdges() == 0 || graph.numberOfEdges() < pathSize ) 
			return graphList; 

		log.info("searching graph: " + graph.toString());

		int numNodes = graph.numberOfNodes();

		int numTrials = 0;
		if ( eps < 1.0 ) 
			numTrials = (int) (Math.exp(pathSize)*Math.log((double)(numNodes)/eps));
		else 
			numTrials = (int)eps; 

		// just a safety valve
		if ( numTrials < 20 )
			log.warning("The specified number of trials (" + numTrials + ") for the color coding path search algorithm is very small and risks missing a significant number of high scoring paths.  Consider increasing the value to something greater than 20.");

		log.info("num trials: " + numTrials);

		int[] orderedColorSetList = getOrderedColorSetList();

		W = new ArrayList<List<PathGraph<NodeType>>>();
		for ( int i = 0; i < numNodes; i++ ) {
			List<PathGraph<NodeType>> tempw = new ArrayList<PathGraph<NodeType>>();
			for ( int j = 0; j <= colorSet; j++ ) 
				tempw.add(null);
			W.add( tempw );
		}

		double currentMinScore = Double.NEGATIVE_INFINITY;

		if ( numSolutions == 0 )
			numSolutions = NUM_PATHS_PER_NODE;
		System.out.println("expected number of solutions: " + numSolutions*numNodes);

		List<SortedSet<Graph<NodeType,Double>>> resultSets = new ArrayList<SortedSet<Graph<NodeType,Double>>>();
		for ( int i = 0; i < numNodes; i++ )	
			resultSets.add(  new TreeSet<Graph<NodeType,Double>>() );

		initNodeIndices(graph);

		Set<NodeType> nodeSet = graph.getNodes();

		for ( int x = 0; x < numTrials; x++ ) {
			
			log.config("trial " + x);

			// re-initialize storage
			for ( int i = 0; i < numNodes; i++ )	
				for ( int j = 0; j <= colorSet; j++ )
					W.get(i).set(j,null); 

			initNodeColors(graph);	

			// dynamic programming over all color combinations
			for ( int i = 0; i < orderedColorSetList.length; i++ ) {

				int colorCombo = orderedColorSetList[i];

				for (NodeType node: nodeSet) { 
					int nodeColor = getNodeColor(node);

					// only consider nodes in this particular color combination
					if ( (nodeColor & colorCombo) != nodeColor ) 
						continue;

					int prevCombo = colorCombo - nodeColor;

					// for the first color combinations initialize the score
					// and skip the rest of the processing
					if ( prevCombo == 0 ) {
						if ( beginSet == null || beginSet.contains(node) ) 
							W.get(nodeIndex(node)).set(colorCombo, new PathGraph<NodeType>(node));
						continue;
					}

					// we're beyond the initialization bits, so compare 
					// the node to all of its neighbors that are within 
					// the color combination that excludes the color of 
					// the node itself
					for ( NodeType neighbor: graph.getNeighbors(node) ) {
						int neighborColor = getNodeColor(neighbor);

						// check if the neighbor is ok for this color combo
						if ( (neighborColor & prevCombo) == neighborColor ) {

							// get the old graph
							PathGraph<NodeType> bfg = W.get(nodeIndex(neighbor)).get(prevCombo);
							if ( bfg == null ) 
								continue;

							// clone the old graph and add the new node/edge
							PathGraph<NodeType> fg = (PathGraph<NodeType>)bfg.clone();
							fg.addNode( node ); // implicit edge

							// score the new graph
							double score = scoreObj.scoreGraph( fg );
							fg.setScore( score ); 

							int nodeInd = nodeIndex(node);
							
							// if the we're at the end of dp, just add the graph to the results
							// because this is a sorted set we automatically get the top results
							if ( colorCombo == colorSet ) { 
								SortedSet<Graph<NodeType,Double>> res = resultSets.get(nodeInd);
								res.add( fg );	
								if ( res.size() > numSolutions ) 
									res.remove(res.first());
							// otherwise update the dp matrix with the new graph if appropriate
							} else {
								PathGraph<NodeType> cfg = W.get(nodeInd).get(colorCombo);
								if ( cfg == null || score > cfg.getScore().doubleValue() ) 
									W.get(nodeInd).set(colorCombo,fg);
								else
									fg = null;
							}
						}
					}
				}
			}
		} 

		for ( SortedSet<Graph<NodeType,Double>> s : resultSets ) {
			for ( Graph<NodeType,Double> tg : s ) {
				// create new graphs so that we're not using the specialized PathGraphs
				Graph<NodeType,Double> xg = new BasicGraph<NodeType,Double>(tg); 
				graphList.add( xg );
			}
		}

		Collections.sort(graphList);

		Long totalTime = System.currentTimeMillis() - beginTime;
		System.out.println("path search elapsed time: " + totalTime);
		System.out.println("resultSets size: " + resultSets.size());
		
		return  graphList;
	}

	/**
	 * Returns a random color. The color will be 1 bit within
	 * a bitmask the length of pathSize.
	 */
	private int getRandomColor() {
		return 1 << ((int)(Math.abs((double)(rand.nextInt()%pathSize))));
	}

	/** 
	 * Returns the color of a node. This assumes that initColors has been run.
	 */
	private int getNodeColor(NodeType node) {
		return nodeColorMap.get(node).intValue();
	}


	/**
	 * Returns the index of the node used to access the dynamic programming storage matrices.
	 */
	private int nodeIndex(NodeType node) {
		return nodeIndexMap.get(node).intValue();
	}

	/**
	 * This method returns and array of ints that represent all one color combinations, 
	 * all two color combinations
	 * followed by all 3 color combinations, through to all (pathSize - 1) color combinations.
	 * This method returns an array of ints ordered such that when considered as a 
	 * bit mask, the first values only contain 1 true bit (1,2,4,8) , the second values
	 * all contain 2 true bits (e.g. 3,5,6,9,10,12), followed
	 * by all ints that contain only 3 true bits (e.g. 7,11,13,14).  This repeats until
	 * pathSize number of bits is found (an array of size pathSize^2 - 1).  
	 */
	private int[] getOrderedColorSetList() {
		int twoPath = 1 << pathSize;
		int[] colors = new int[twoPath - 1];
		int index = 0;
		for ( int i = 1; i <= pathSize; i++ ) 
			// Check each int between 1 and 2^pathSize - 1.  
			for ( int j = 1; j < twoPath; j++ ) {
				int x = j;
				if ( countBits(x) == i )
					colors[index++] = j;
			}

		return colors;
	}

	/**
	 * Counts the bits set to true (1) in an int.
	 * @param x The integer whose bits are to be counted.
	 * @return The number of bits found in the input integer.
	 */
	private int countBits(int x) {
		int count = 0;
		while ( x != 0 ) {
			count += x & 1;
			x >>>= 1;
		}
		return count;
	}

	/**
	 * Initializes the colors for each node in the specified graph.
	 * @param g The graph whose nodes need coloring.
	 */
	private void initNodeColors(Graph<NodeType,Double> g) {
		nodeColorMap.clear();
		for ( NodeType node: g.getNodes() ) 
			nodeColorMap.put(node,getRandomColor());
	}

	/**
	 * Initializes the mapping of node names to index numbers. 
	 * @param g The graph whose nodes need indices.
	 */
	private void initNodeIndices(Graph<NodeType,Double> g) {
		int nodeCount = 0;
		nodeIndexMap.clear();
		for ( NodeType node: g.getNodes() ) 
			nodeIndexMap.put(node,nodeCount++);
	}

	/**
	 * Checks if a given path contains at least one of the nodes
	 * in the constraint set.
	 * @param count The number of constrained nodes in the path. 
	 * @return Whether or not the path contains enough constrained nodes. 
	 */
	protected boolean checkConstraint(int count) {
		if ( constraintSet == null )
			return true;

		if ( count >= minConstraintNodes && count <= maxConstraintNodes )
			return true;
		else
			return false;
	}

	/**
	 * Checks that the node is consistent with the segement indicated by 
	 * the color combination (ie the number of bits).
	 * @param node The node used whose segement is to be checked.
	 * @param colorCombination The bitmap that defines a color combination and
	 * segment.
	 * @return Whether or not the node is in the appropriate segment.
	 */
	protected boolean consistentSegmentation(NodeType node, int colorCombination) {
		if ( maxSegmentMap == null || minSegmentMap == null) 
			return true;
	
		int segment = pathSize - countBits(colorCombination);
		System.out.println ("node: " + node + "  color: " + colorCombination + "  segment: " + segment);
		if ( segment >= minSegmentMap.get(node).intValue() &&
		     segment <= maxSegmentMap.get(node).intValue() )
			return true;
		else
			return false;
	}
}
