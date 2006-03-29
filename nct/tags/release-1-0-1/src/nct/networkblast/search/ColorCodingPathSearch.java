
//============================================================================
// 
//  file: ColorCodingPathSearch.java
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
import java.lang.reflect.Array;
import nct.networkblast.score.*;
import nct.networkblast.graph.*;
import nct.graph.*;
import nct.graph.basic.*;

/**
 * This class implements a color coding algorithm to search for pathways 
 * of size n in a given graph using a scoring object. See: Scott, et al., 
 * 2005, Efficient Algorithms for Detecting Signaling Pathways in Protein 
 * Interaction Networks, Lecture Notes in Computer Science, vol. 3500.
 */
public class ColorCodingPathSearch<NodeType extends Comparable<? super NodeType>> implements SearchGraph<NodeType,Double> {

	/**
	 * The default epsilon value.
	 * Epsilon is probability that we will miss a best path if
	 * we iterate the correct number of times.
	 */
	 public static double DEFAULT_EPSILON = 0.0001;
   
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
	private double[][] W;

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
	private int numSolutions;

	/**
	 * The logging object we will write to. 
	 */
	private static Logger log = Logger.getLogger("networkblast");	

	/**
	 * @param size the integer size of paths to search for
	 */
	public ColorCodingPathSearch(int size) {
		this(size,0);
	}

	/**
	 * @param size The lengths of the paths to search for.
	 * @param  numSols The maximum number of solution paths to return.
	 */
	public ColorCodingPathSearch(int size,int numSols) {
		this(size,numSols,ColorCodingPathSearch.DEFAULT_EPSILON);
	}

	/**
	 * @param size The lengths of the paths to search for.
	 * @param  numSols The maximum number of solution paths to return.
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
	public ColorCodingPathSearch(int size,int numSols, double epsilon) {
		pathSize = size;
		numSolutions = numSols;
		eps = epsilon;
		rand = new Random((long)pathSize); // arbitrarily seed the prng with the path size
		colorSet = twoToTheN(pathSize) - 1; 
		nodeColorMap = new HashMap<NodeType,Integer>();
		nodeIndexMap = new HashMap<NodeType,Integer>(); 
		log.info("pathsize: " + size + " num solutions: " + numSols + " epsilon: " + epsilon);
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

		W = new double[numNodes][colorSet+1];
		path = new ArrayList<List<NodeType>>();
		for ( int i = 0; i < numNodes; i++ ) {
			List<NodeType> templ = new ArrayList<NodeType>();
			for ( int j = 0; j <= colorSet; j++ )
				templ.add(null);
			path.add( templ ); 
		}

		double currentMinScore = Double.MIN_VALUE;

		if ( numSolutions == 0 )
			numSolutions = numNodes;

		SortedSet<Graph<NodeType,Double>> resultSet = new TreeSet<Graph<NodeType,Double>>();
		initNodeIndices(graph);

		Set<NodeType> nodeSet = graph.getNodes();

		for ( int x = 0; x < numTrials; x++ ) {
			log.config("trial " + x);
			//System.out.println("trial " + x);

			// re-initialize storage
			for ( int i = 0; i < numNodes; i++ )	{
				for ( int j = 0; j <= colorSet; j++ ) {
					W[i][j] = Double.MIN_VALUE; 
					path.get(i).set(j,null); 
				}
			}

			initNodeColors(graph);	

			// dynamic programming over all color combinations
			for ( int i = 0; i < orderedColorSetList.length; i++ ) {
				int colorCombo = orderedColorSetList[i];
				if ( colorCombo == 0 )
					continue;
				
				for (NodeType node: nodeSet) { 
					int nodeColor = getNodeColor(node);
					//System.out.println("node " + nodeColor);

					// only consider nodes in this particular color combination
					if ( (nodeColor & colorCombo) != nodeColor )
						continue;

					int prevCombo = colorCombo - nodeColor;
					//System.out.println("prevcolor combo " + prevCombo);

					// now compare the node to all of its neighbors that are 
					// within the color combination excluding the node color
					for (NodeType neighbor: graph.getNeighbors(node) ) {
						int neighborColor = getNodeColor(neighbor);
						//System.out.println("neigh " + neighborColor);
						
						if ( (neighborColor & prevCombo) == neighborColor ) {
							double score = graph.getEdgeWeight(node,neighbor) + W[nodeIndex(neighbor)][prevCombo];	
							int nodeInd = nodeIndex(node);
							//System.out.println("score " + score);

							if ( score > W[nodeInd][colorCombo] ) {
								W[nodeInd][colorCombo] = score; 
								path.get(nodeInd).set(colorCombo,neighbor);
							}	
						}	
					}						
				}	
			}	

			// traceback through the W matrix to extract the actual path
			for (NodeType node: nodeSet) {
				int color = colorSet;
				int nodeInd = nodeIndex(node); 
				if ( W[nodeInd][color] > currentMinScore ) { 
					//System.out.println("trial " + x + " curr min: " + currentMinScore + "  w: " + W[nodeInd][color]);
					Graph<NodeType,Double> sg = new BasicGraph<NodeType,Double>();
					sg.setScore( W[nodeInd][color] );
					while ( path.get(nodeInd).get(color) != null ) {
						NodeType next = path.get(nodeInd).get(color);
						sg.addNode(node);
						sg.addNode(next);
						sg.addEdge(node,next,graph.getEdgeWeight(node,next));
						sg.setEdgeDescription(node,next,graph.getEdgeDescription(node,next));
						color -= getNodeColor(node);
						node = next;
						nodeInd = nodeIndex(node);
					}

					// Add the path to the result set if the score is high enough
					// avoid adding to the set promiscuously because each add
					// requires a sort.
					//
					// However, this bit of code only takes a fraction (~5%) of the
					// overall time of this method.
					//System.out.println("evaluating: " + sg.toString());
					if ( sg.numberOfNodes() == pathSize &&  
					     ( resultSet.size() == 0 || 
					       resultSet.size() < numSolutions || 
					       sg.compareTo(resultSet.first()) > 0 ) ) {
						resultSet.add(sg);
						//System.out.println("Adding!");
						if ( resultSet.size() > numSolutions )
							resultSet.remove(resultSet.first());
					} else 
						sg = null; // space concerns
				} 
			}
			if ( resultSet.size() > 0 ) 
				currentMinScore = resultSet.first().getScore();
		} 

		graphList.addAll( resultSet );	

		Long totalTime = System.currentTimeMillis() - beginTime;
		log.info("path search elapsed time: " + totalTime);
		log.info("resultSet size: " + resultSet.size());
		
		return  graphList;
	}


	/**
	 * Calculates 2 to the Nth power. 
	 */
	private int twoToTheN( int N ) {
		return (int)(Math.pow(2.0,(double)N));
	}

	/**
	 * Returns a random color. The color will be 1 bit within
	 * a bitmask the length of pathSize.
	 */
	private int getRandomColor() {
		return twoToTheN((int)(Math.abs((double)(rand.nextInt()%pathSize))));
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
	 * This method returns and array of ints that represent all two color combinations
	 * followed by all 3 color combinations, through to all (pathSize - 1) color combinations.
	 * This method returns an array of ints ordered such that when considered as a 
	 * bit mask, the first values only contain 2 true bits (e.g. 3,5,6,9,10,12), followed
	 * by all ints that contain only 3 true bits (e.g. 7,11,13,14).  This repeats until
	 * pathSize number of bits is found (an array of size pathSize^2 - 1).  We ignore
	 * the int values with only 1 true bit (e.g. 1,2,4,8) because these represent a
	 * single color and not a combination.
	 */
	private int[] getOrderedColorSetList() {
		int twoPath = twoToTheN(pathSize);
		int[] colors = new int[twoPath - pathSize - 1];
		int index = 0;
		// We only care about combinations with more than
		// two bits through the single combination with pathSize bits.
		for ( int i = 2; i <= pathSize; i++ ) 
			// Check each int between 1 and 2^pathSize - 1.  
			for ( int j = 1; j < twoPath; j++ ) {
				int x = j;
				int count = 0;
				// count the actual bits
				while ( x != 0 ) {
					count += x & 1;
					x >>>= 1;
				}
				if ( count == i )
					colors[index++] = j;
			}

		return colors;
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
}
	
