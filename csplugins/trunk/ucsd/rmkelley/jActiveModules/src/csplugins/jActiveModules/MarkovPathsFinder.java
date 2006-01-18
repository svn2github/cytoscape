package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFrame;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.jet.stat.Descriptive;
import cern.jet.stat.Probability;
import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.CyNetwork;

/**
 * This class contains the main logic for finding Active Paths The important
 * function is findActivePaths() which calls the simulated annealing subroutine
 * to find the active paths
 */
public class MarkovPathsFinder {

	
	
	/**
	 * See constructor
	 */
	private String[] attrNames;
	/**
	 * See constructor
	 */
	private CyNetwork cyNetwork;
	/**
	 * parameters for path finding
	 */
	private ActivePathFinderParameters apfParams;
	/**
	 * an array containing all of the nodes initially in the graph
	 */
	private Node[] nodes;
	/**
	 * This is a hashmap which maps from nodes to an array of edges (Edge []).
	 * This is used to determine which edges belonged to which nodes before any
	 * changes were made to the graph. This hash map is then used to recover
	 * edges when reinserting nodes into a graph it is initialized in
	 * setupScoring() and used in toggleNode()
	 */
	// HashMap node2edges;
	// Global Variables for the Greedy Search
	/**
	 * Maps from a node to the best component found for that node
	 */
	HashMap node2BestComponent;
	/**
	 * The neighborhood for the current best component
	 */
	// HashSet bestNeighborhood;
	HashMap expressionMap;
	JFrame parentFrame;
	protected static int DISPLAY_STEP = 50;

	/**
	 * This is the only constructor for ActivePathsFinder. In order to find the
	 * paths, we need certain information.
	 * 
	 * @param attrNames
	 *            The names of hte attributes which correspond to significance
	 * @param cyNetwork
	 *            The cyNetwork which contains our graph, should divorce this
	 *            from the window before running
	 * @param apfp
	 *            The object specifying the parameters for this run
	 * @param parentFrame
	 *            The JFrame which is our parent window, if this is null, then
	 *            we won't display any progress information
	 */
	public MarkovPathsFinder(HashMap expressionMap, String[] attrNames,
			CyNetwork cyNetwork, ActivePathFinderParameters apfp,
			JFrame parentFrame) {
		this.expressionMap = expressionMap;
		this.parentFrame = parentFrame;
		this.attrNames = attrNames;
		this.cyNetwork = cyNetwork;
		apfParams = apfp;

	}





	public Component [] findActivePaths(){
		setupScoring();
		/*
		 * Number of nodes to be selected as the initial seed size
		 */
		int seedSize = 20;
		double alpha = 30;
		double beta = -100;
		
		/*
		 * Values to calculate hte value of the t-test
		 */
		double activeSum = 0;
		double activeSquaredSum = 0;
		double backgroundSum = 0;
		double backgroundSquaredSum = 0;
		
		int nodeCount = cyNetwork.getNodeCount();
		int edgeCount = cyNetwork.getEdgeCount();
		double networkFactor = (double)edgeCount;
		
		int backgroundCount = nodeCount - seedSize;
		int activeCount = seedSize;
		
		/*
		 * Map from each node to a sequential ID
		 */
		OpenIntIntHashMap id2SeqId = new OpenIntIntHashMap(nodeCount);
		{
			int idx = 0;
			for(Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();idx += 1){
				Node current = (Node)nodeIt.next();
				id2SeqId.put(current.getRootGraphIndex(),idx);
			}
		}

		/*
		 * Initialize an array which keeps track of class membership
		 */
		boolean [] node2Active = new boolean[nodeCount];
		
		
		/*
		 * Set an initial group of nodes to be
		 * the "active nodes". Initially just choose the
		 * highest x nodes to be the highest nodes
		 */
		OpenIntDoubleHashMap id2Expression = new OpenIntDoubleHashMap(nodeCount);
		for(Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();){
			Node current = (Node)nodeIt.next();
			double expressionValue = ((double[])expressionMap.get(current))[0];
			backgroundSum += expressionValue;
			backgroundSquaredSum += expressionValue*expressionValue;
			id2Expression.put(current.getRootGraphIndex(),expressionValue);
			
		}

		IntArrayList sortedKeys = new IntArrayList();
		id2Expression.keysSortedByValue(sortedKeys);
		int [] activeNodes = new int[seedSize];
		
		for(int idy = 0; idy < seedSize; idy += 1){
			int nodeId = sortedKeys.get(nodeCount-idy-1);
			int nodeSeqId = id2SeqId.get(nodeId);
			node2Active[nodeSeqId] = true;
			double expressionValue = id2Expression.get(nodeId);
			backgroundSum -= expressionValue;
			backgroundSquaredSum -= expressionValue*expressionValue;
			activeSum += expressionValue;
			activeSquaredSum += expressionValue*expressionValue;
			activeNodes[idy] = nodeId;
		}
		
		int [] connectingEdges = cyNetwork.getConnectingEdgeIndicesArray(activeNodes);
		networkFactor += alpha*connectingEdges.length;
		int allEdges = 0;
		for(int idx = 0;idx< activeNodes.length; idx++){
			allEdges += cyNetwork.getAdjacentEdgeIndicesArray(activeNodes[idx],true,true,true).length;
		}
		int nonActiveEdges = allEdges - 2*connectingEdges.length;
		networkFactor += beta*nonActiveEdges;
		System.err.println("active edges: "+connectingEdges.length);
		System.err.println("non active edges: "+nonActiveEdges);
		
		double score = networkFactor*calculateScore(activeSum,activeSquaredSum,backgroundSum,backgroundSquaredSum,activeCount,backgroundCount);
		
//		for(int idy = seedSize;idy<nodeCount;idy += 1){
//			int nodeId = sortedKeys.get(nodeCount-idy-1);
//			int nodeSeqId = id2SeqId.get(nodeId);
//			double expressionValue = id2Expression.get(nodeId);
//			
//			double new_score = calculateScore(activeSum+expressionValue,activeSquaredSum+expressionValue*expressionValue,backgroundSum-expressionValue,backgroundSquaredSum-expressionValue*expressionValue,activeCount+1,backgroundCount-1);
//			if(new_score > score){
//				backgroundSum -= expressionValue;
//				backgroundSquaredSum -= expressionValue*expressionValue;
//				activeSum += expressionValue;
//				activeSquaredSum += expressionValue*expressionValue;
//				activeCount += 1;
//				backgroundCount -= 1;
//				score = new_score;
//				node2Active[nodeSeqId] = true;
//			}
//			else{
//				break;
//			}
//		}
		
		
		
		while(true){
			/*
			 * Find best move
			 */
			double bestScore = Double.NEGATIVE_INFINITY;
			double bestDelta = Double.NEGATIVE_INFINITY;
			int bestNode = Integer.MAX_VALUE;
			for(Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();){
				Node current = (Node)nodeIt.next();
				int nodeId = current.getRootGraphIndex();
				int nodeSeqId = id2SeqId.get(nodeId);
				double expressionValue = id2Expression.get(nodeId);
				double currentScore = Double.NEGATIVE_INFINITY;
				double currentDelta = Double.NEGATIVE_INFINITY;
				if(node2Active[nodeSeqId]){
					if(activeCount > 3){
						/*
						 * Calculate the change in network factor by switching this node
						 * to not be active. Every edge to an active neighbor is currently influencing
						 * the network factor, so that influence will have to be removed
						 */
						currentDelta = 0;
						int [] adjacentEdges = cyNetwork.getAdjacentEdgeIndicesArray(nodeId,true,true,true);
						for(int idx = 0; idx < adjacentEdges.length; idx++){
							int adjacentEdge = adjacentEdges[idx];
							int adjacentNode = cyNetwork.getEdgeSourceIndex(adjacentEdge) == nodeId ? cyNetwork.getEdgeTargetIndex(adjacentEdge) : cyNetwork.getEdgeSourceIndex(adjacentEdge);
							int adjacentSeqId = id2SeqId.get(adjacentNode);
							if(node2Active[adjacentSeqId]){
								currentDelta -= (alpha-beta);
							}
							else{
								currentDelta -= beta;
							}
						}
						currentScore = (networkFactor+currentDelta)*calculateScore(activeSum-expressionValue,activeSquaredSum-expressionValue*expressionValue,backgroundSum+expressionValue,backgroundSquaredSum+expressionValue*expressionValue,activeCount-1,backgroundCount+1);
					}
				}
				else{ 
					if(backgroundCount > 3){
						currentDelta = 0;
						int [] adjacentEdges = cyNetwork.getAdjacentEdgeIndicesArray(nodeId,true,true,true);
						for(int idx = 0; idx < adjacentEdges.length; idx++){
							int adjacentEdge = adjacentEdges[idx];
							int adjacentNode = cyNetwork.getEdgeSourceIndex(adjacentEdge) == nodeId ? cyNetwork.getEdgeTargetIndex(adjacentEdge) : cyNetwork.getEdgeSourceIndex(adjacentEdge);
							int adjacentSeqId = id2SeqId.get(adjacentNode);
							if(node2Active[adjacentSeqId]){
								currentDelta += (alpha-beta);
							}
							else{
								currentDelta += beta;
							}
						}
						currentScore = (networkFactor+currentDelta)*calculateScore(activeSum+expressionValue,activeSquaredSum+expressionValue*expressionValue,backgroundSum-expressionValue,backgroundSquaredSum-expressionValue*expressionValue,activeCount+1,backgroundCount-1);
					}
				}
				if(currentScore > bestScore){
					bestNode = nodeId;
					bestScore = currentScore;
					bestDelta = currentDelta;
				}
				
			}
			
			/*
			 * If best move isn't that good, break
			 */
			if(bestScore <= score){
				break;
			}
			
			/*
			 * Otherwise, apply best move and update base-values
			 */
			int bestSeqId = id2SeqId.get(bestNode);
			double expressionValue = id2Expression.get(bestNode);
			if(node2Active[bestSeqId]){
				activeSum -= expressionValue;
				activeSquaredSum -= expressionValue*expressionValue;
				backgroundSum += expressionValue;
				backgroundSquaredSum += expressionValue*expressionValue;
				activeCount -= 1;
				backgroundCount += 1;
			}else{
				activeSum += expressionValue;
				activeSquaredSum += expressionValue*expressionValue;
				backgroundSum -= expressionValue;
				backgroundSquaredSum -= expressionValue*expressionValue;
				activeCount += 1;
				backgroundCount -= 1;
			}
			node2Active[bestSeqId] = !node2Active[bestSeqId];
			score = bestScore;
			networkFactor += bestDelta;
		}
		
		/*
		 * Put every active node into an active component
		 */
		Component [] components = new Component[1];
		components[0] = new Component();
		for(Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();){
			Node current = (Node)nodeIt.next();
			int nodeSeqId = id2SeqId.get(current.getRootGraphIndex());
			if(node2Active[nodeSeqId]){
				components[0].addNode(current);
			}
		}
		components[0].finalizeDisplay();
		return components;
	}
	
	/**
	 * In order to score the components, we need to set up certain data
	 * structures first. Mainly this includes seting up the z scores table and
	 * the monte carlo correction. Chris sez it shouldn't be called a monte
	 * carlo correction, and I tend to agree, but this has some historical
	 * inertia behind it (ie, it would involve changing maybe 6 lines of code,
	 * which is simply unthinkable) These data structures are initialized as
	 * static data structures in the Component class, where the scoring is
	 * actually done
	 */
	private void setupScoring() {
		GraphPerspective perspective = cyNetwork.getGraphPerspective();
		// Here we initialize the z table. We use this data structure when we
		// want to get an adjusted z score
		// based on how many conditions we are looking at.
		System.err.println("Initializing Z Table");
		Component.zStats = new ZStatistics(attrNames.length);
		System.err.println("Done initializing Z Table");

		nodes = new Node[1];
		nodes = (Node[]) (perspective.nodesList().toArray(nodes));

		// Edge [] e_array;
		// This has is used to store all the edges that were connected
		// to a particular node
		// node2edges = new HashMap();
		// for(int i = 0;i<nodes.length;i++){
		// Edge [] temp = new Edge[0];
		// node2edges.put(nodes[i],perspective.getAdjacentEdgesList(nodes[i],true,true,true).toArray(temp));
		// }

		// Component.node2edges = node2edges;
		Component.graph = perspective;
		// Component needs the condition names to return which conditions
		// yield significant scores
		Component.attrNames = attrNames;
		// Determine whether or not we want to correct for the size
		// of active paths
		Component.monteCorrection = apfParams.getMCboolean();
		Component.regionScoring = apfParams.getRegionalBoolean();
		Component.exHash = expressionMap;
		// Initialize the param statistics object. The pStats object uses
		// randomized methods ot determine the
		// mean and standard deviation for networks of size 1 through n.
		Component.pStats = new ParamStatistics(new Random(apfParams
				.getRandomSeed()), Component.zStats);
		// The statistics object is required fro the component scoring function
		// We want to use a monte carlo correction
		if (apfParams.getMCboolean()) {
			boolean failed = false;
			// and we want to load the state from a file
			if (apfParams.getToUseMCFile()) {
				// read in the monte carlo file, it is stored as a serialized
				// ParamStatistics object
				System.err.println("Trying to read monte carlo file");
				try {
					FileInputStream fis = new FileInputStream(apfParams
							.getMcFileName());
					ObjectInputStream ois = new ObjectInputStream(fis);
					Component.pStats = (ParamStatistics) ois.readObject();
					ois.close();
					if (Component.pStats.getNodeNumber() != nodes.length) {
						// whoops, the file we loaded doesn't look like it
						// contains the correct information for the set
						// of nodes we are dealing with, user specified a bad
						// file, I hope he feels shame
						System.err
								.println("Monte Carlo file calculated for incorrect number of nodes. Using correct file?");
						failed = true;
						throw new Exception("wrong number of nodes");
					}
				} catch (Exception e) {
					System.err.println("Loading monte carlo file failed" + e);
					failed = true;
				}
			}

			if (failed || !apfParams.getToUseMCFile()) {
				System.err.println("Initializing monte carlo state");
				MyProgressMonitor progressMonitor = null;
				if (parentFrame != null) {
					progressMonitor = new MyProgressMonitor(parentFrame,
							"Sampling Mean and Standard Deviation", "", 0,
							ParamStatistics.DEFAULT_ITERATIONS);
				} // end of if ()

				// Component.pStats.calculateMeanAndStd(nodes,ParamStatistics.DEFAULT_ITERATIONS,apfParams.getMaxThreads(),
				// new MyProgressMonitor(cytoscapeWindow, "Sampling Mean and
				// Standard
				// Deviation","",0,ParamStatistics.DEFAULT_ITERATIONS));
				Component.pStats.calculateMeanAndStd(nodes,
						ParamStatistics.DEFAULT_ITERATIONS, apfParams
								.getMaxThreads(), progressMonitor);
				System.err.println("Finished initializing monte carlo state");

				System.err.println("Trying to save monte carlo state");
				try {
					FileOutputStream fos = new FileOutputStream("last.mc");
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(Component.pStats);
					oos.close();
					System.err.println("Saved monte carlo state to last.mc");
				} catch (Exception e) {
					System.err.println("Failed to save monte carlo state" + e);
				}
			}
		}

	}
	
	/*
	 * Right now, this is going to return the t-value or something, but eventually it should return
	 * the p-value.
	 */
	protected static double calculateScore(double activeSum, double activeSquaredSum, double backgroundSum, double backgroundSquaredSum, int activeCount, int backgroundCount){
		double backgroundSD = Math.sqrt(Descriptive.sampleVariance(backgroundCount,backgroundSum,backgroundSquaredSum));
		double backgroundT = (backgroundSum/backgroundCount)/(backgroundSD/Math.sqrt(backgroundCount));
		if(backgroundT < 0){
			backgroundT = -backgroundT;
		}
		double backgroundP = 0, activeP = 0;
		try{
			backgroundP = Probability.studentT(backgroundCount-1,backgroundT);
		}catch(Exception e){
			System.err.println("Problem with background P-value calculation");
			System.err.println("backgroundCount: "+backgroundCount);
			System.err.println("background T: "+backgroundT);
			System.err.println("activeCount: "+activeCount);
			System.exit(-1);
		}
			//System.out.println(""+(1-backgroundP)+","+backgroundT);
		//double betweenSD = Math.sqrt((Descriptive.sampleVariance(activeCount,activeSum,activeSquaredSum)/activeCount)+(Descriptive.sampleVariance(backgroundCount,backgroundSum,backgroundSquaredSum)/backgroundCount));
		//double betweenT = (activeSum/activeCount - backgroundSum/backgroundCount)/betweenSD;
		//double betweenP = Probability.studentT(activeCount+backgroundCount-2,betweenT);
		double activeSD = Math.sqrt(Descriptive.sampleVariance(activeCount,activeSum,activeSquaredSum));
		double activeT = (activeSum/activeCount)/(activeSD/Math.sqrt(activeCount));
		try{
			activeP = Probability.studentT(activeCount-1,activeT);
		}catch(Exception e){
			System.err.println("Problem with active P-value calculation");
			System.err.println("activeCount: "+activeCount);
			System.err.println("active T: "+activeT);
			System.err.println("backgroundCount: "+backgroundCount);
			System.exit(-1);
		}
		//System.err.println("Between");
		//System.err.println(betweenSD);
		//System.err.println(betweenT);
		//System.err.println(""+betweenP+"\n----");
		return (1-backgroundP)*(activeP);
	}
	
	public static void main(String [] args){
		DoubleArrayList background = new DoubleArrayList(new double[]{2,3,4,5});
		DoubleArrayList active = new DoubleArrayList(new double[]{4,5,6});
		System.out.println(calculateScore(Descriptive.sum(active),Descriptive.sumOfSquares(active),Descriptive.sum(background),Descriptive.sumOfSquares(background),active.size(),background.size()));
	}

}
