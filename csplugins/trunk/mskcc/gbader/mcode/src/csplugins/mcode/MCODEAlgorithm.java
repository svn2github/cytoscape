package csplugins.mcode;

import cytoscape.GraphObjAttributes;
import giny.model.GraphPerspective;

import java.util.*;

/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Gary Bader
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 ** User: Gary Bader
 ** Date: Jan 20, 2004
 ** Time: 6:18:03 PM
 ** Description
 **/
public class MCODEAlgorithm {

	private class NodeInfo {
		double density;         //neighborhood density
		int numNodeNeighbors;   //number of node nieghbors
		int[] nodeNeighbors;    //stores node indices
		int coreLevel;          //e.g. 2 = a 2-core
		double coreDensity;     //density of the core neighborhood
		double score;           //node score

		public NodeInfo() {
			this.density = 0.0;
			this.numNodeNeighbors = 0;
			this.coreLevel = 0;
			this.coreDensity = 0.0;
		}
	}

	//data structures useful to have around for more than one complex finding iteration
	private HashMap nodeInfoHashMap; //key is the node index, value is a NodeInfo instance
	private TreeMap nodeScoreSortedMap; //key is node score, value is nodeIndex

	//parameters
	//used in scoring stage
	public boolean includeLoops;
	//used in complex finding stage
	public int maxDepthFromStart;
	public int degreeCutOff;
	public double nodeScoreCutOff;
	public boolean fluff;
	public boolean haircut;
	public double fluffNodeDensityCutOff;
	//used in directed mode
	public String directedNodeName;
	public boolean preprocessNetwork;

	public MCODEAlgorithm() {
		//default parameters
		includeLoops = false;
		maxDepthFromStart = 100;  //effectively unlimited
		degreeCutOff = 2; //don't count nodes of degree 1
		nodeScoreCutOff = 0.2;    //user should change this as the main parameter
		fluff = false;
		haircut = true;
		fluffNodeDensityCutOff = 0.1; //user should change this if fluffing

		//init class members
		nodeInfoHashMap = null;
		nodeScoreSortedMap = null;
	}

	//parameter setting
	public void setScoreParams(boolean includeLoops, int degreeCutOff) {
		this.includeLoops = includeLoops;
		this.degreeCutOff = degreeCutOff;
	}

	public void setFindComplexesParams(int depth, double nodeScoreCutOff, boolean fluff,
	                                   boolean haircut, double fluffNodeDensityCutOff) {
		this.maxDepthFromStart = depth;
		this.nodeScoreCutOff = nodeScoreCutOff;
		this.fluff = fluff;
		this.haircut = haircut;
		this.fluffNodeDensityCutOff = fluffNodeDensityCutOff;
	}

	//Step 1 - score the graph and save scores as node attributes
	public void scoreGraph(GraphPerspective gpInputGraph, GraphObjAttributes nodeAttributes) {
		String callerID = "MCODEAlgorithm.MCODEAlgorithm";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return;
		}
		if (nodeAttributes == null) {
			System.err.println("In " + callerID + ": nodeAttributes was null.");
			return;
		}

		//initialize
		nodeInfoHashMap = new HashMap(gpInputGraph.getNodeCount());
		nodeScoreSortedMap = new TreeMap(new Comparator() { //will store Doubles
			//sort Doubles in descending order
			public int compare(Object o1, Object o2) {
				double d1 = ((Double) o1).doubleValue();
				double d2 = ((Double) o2).doubleValue();
				if (d1 == d2) {
					return 0;
				} else if (d1 < d2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		//iterate over all nodes and calculate MCODE score
		NodeInfo nodeInfo = null;
		double nodeScore;
		for (int i = 1; i <= gpInputGraph.getNodeCount(); i++) {
			nodeInfo = calcNodeInfo(gpInputGraph, i);
			nodeInfoHashMap.put(new Integer(i), nodeInfo);
			//score node TODO: add support for other scoring functions (low priority)
			nodeScore = scoreNode(nodeInfo);
			//record score as a nodeAttribute
			nodeAttributes.set("MCODE_SCORE", gpInputGraph.getNode(i).getIdentifier(), nodeScore);
			//save score for later use in TreeMap
			nodeScoreSortedMap.put(new Double(nodeScore), new Integer(i));
		}
	}

	//Step 2: find all complexes given a scored graph
	public ArrayList findComplexes(GraphPerspective gpInputGraph) {
		String callerID = "MCODEAlgorithm.findComplexes";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return (null);
		}
		if ((nodeInfoHashMap == null) || (nodeScoreSortedMap == null)) {
			System.err.println("In " + callerID + ": nodeInfoHashMap or nodeScoreSortedMap was null.");
			return (null);
		}

		//initialization
		boolean[] nodeSeenArray = new boolean[gpInputGraph.getNodeCount() + 1]; //+1 since node indices start at 1
		Arrays.fill(nodeSeenArray, false);
		int currentNode = 0;
		Collection values = nodeScoreSortedMap.values();
		//stores the list of complexes as ArrayLists of node indices in the input GraphPerspective
		ArrayList alComplexes = new ArrayList();
		//iterate over node indices sorted descending by their score
		for (Iterator iterator = values.iterator(); iterator.hasNext();) {
			currentNode = ((Integer) iterator.next()).intValue();
			if (nodeSeenArray[currentNode] == false) {
				ArrayList complex = getComplexCore(currentNode, nodeSeenArray);
				if (fluff) {
					fluffComplexBoundary(complex, nodeSeenArray);
				}
				if (complex.size() > 0) {
					//make sure spawning node is part of complex
					complex.add(new Integer(currentNode));
					//filterComplex
					if (!shaveAndAHaircut(gpInputGraph, complex)) {
						//store detected complex for later
						alComplexes.add(complex);
					}
				}
			}
		}

		return (alComplexes);
	}

	//score node using formula from original MCODE paper
	//This formula selects for larger, denser cores
	private double scoreNode(NodeInfo nodeInfo) {
		if (nodeInfo.numNodeNeighbors > degreeCutOff) {
			nodeInfo.score = nodeInfo.coreDensity * (double) nodeInfo.coreLevel;
		} else {
			nodeInfo.score = 0.0;
		}
		return (nodeInfo.score);
	}

	//Score a complex - currently ranks larger, denser complexes higher
	public double scoreComplex(GraphPerspective gpComplex) {
		int numNodes = 0;
		double density = 0.0, score = 0.0;

		numNodes = gpComplex.getNodeCount();
		density = calcDensity(gpComplex, true);
		score = density * numNodes;

		return (score);
	}

	//Calculates node information for each node according to the original MCODE publication
	//This information is used to score the nodes
	private NodeInfo calcNodeInfo(GraphPerspective gpInputGraph, int nodeIndex) {
		int[] neighborhood;

		String callerID = "MCODEAlgorithm.calcNodeInfo";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return null;
		}

		//get neighborhood of this node (including the node)
		int[] neighbors = gpInputGraph.neighborsArray(nodeIndex);
		if (neighbors.length < 2) {
			//if there are no neighbors or just one neighbor, nodeInfo calculation is trivial
			NodeInfo nodeInfo = new NodeInfo();
			if (neighbors.length == 1) {
				nodeInfo.coreLevel = 1;
				nodeInfo.coreDensity = 1.0;
				nodeInfo.density = 1.0;
			}
			return (nodeInfo);
		}
		//add original node to extract complete neighborhood
		Arrays.sort(neighbors);
		if (Arrays.binarySearch(neighbors, nodeIndex) < 0) {
			neighborhood = new int[neighbors.length + 1];
			System.arraycopy(neighbors, 0, neighborhood, 1, neighbors.length);
			neighborhood[0] = nodeIndex;
		} else {
			neighborhood = neighbors;
		}

		//extract neighborhood subgraph
		GraphPerspective gpNodeNeighborhood = gpInputGraph.createGraphPerspective(neighborhood);
		if (gpNodeNeighborhood == null) {
			//this shouldn't happen
			System.err.println("In " + callerID + ": gpNodeNeighborhood was null.");
			return null;
		}

		//calculate the node information for each node
		NodeInfo nodeInfo = new NodeInfo();
		//density
		if (gpNodeNeighborhood != null) {
			nodeInfo.density = calcDensity(gpNodeNeighborhood, includeLoops);
		}
		nodeInfo.numNodeNeighbors = neighborhood.length;
		//calculate the highest k-core
		GraphPerspective gpCore = null;
		Integer k = null;
		Object[] returnArray = getHighestKCore(gpNodeNeighborhood);
		k = (Integer) returnArray[0];
		gpCore = (GraphPerspective) returnArray[1];
		nodeInfo.coreLevel = k.intValue();
		/*calculate the core density - amplifies the density of heavily interconnected regions and attenuates
		that of less connected regions*/
		if (gpCore != null) {
			nodeInfo.coreDensity = calcDensity(gpCore, includeLoops);
		}
		//record neighbor array for later use in complex detection step
		nodeInfo.nodeNeighbors = neighborhood;

		return (nodeInfo);
	}

	//Find the high-scoring central region of the complex
	private ArrayList getComplexCore(int startNode, boolean[] nodeSeenArray) {
		ArrayList complex = new ArrayList(); //stores Integer nodeIndices
		getComplexCoreInternal(startNode, nodeSeenArray, ((NodeInfo) nodeInfoHashMap.get(new Integer(startNode))).score, 1, complex);
		return (complex);
	}

	private boolean getComplexCoreInternal(int startNode, boolean[] nodeSeenArray, double startNodeScore, int currentDepth, ArrayList complex) {
		//base cases for recursion
		if (nodeSeenArray[startNode] == true) {
			return (true);  //don't recheck a node
		}
		if (currentDepth > maxDepthFromStart) {
			return (true);  //don't exceed given depth from start node
		}

		//Initialization
		int currentNeighbor = 0, i = 0;

		nodeSeenArray[startNode] = true;
		for (i = 0; i < (((NodeInfo) nodeInfoHashMap.get(new Integer(startNode))).numNodeNeighbors); i++) {
			//go through all currentNode neighbors to check their core density for complex inclusion
			currentNeighbor = ((NodeInfo) nodeInfoHashMap.get(new Integer(startNode))).nodeNeighbors[i];
			if ((nodeSeenArray[currentNeighbor] == false) &&
			        (((NodeInfo) nodeInfoHashMap.get(new Integer(currentNeighbor))).score >= (startNodeScore - startNodeScore * nodeScoreCutOff))) {
				//add current neighbor
				complex.add(new Integer(currentNeighbor));
				//try to extend complex at this node
				getComplexCoreInternal(currentNeighbor, nodeSeenArray, startNodeScore, currentDepth + 1, complex);
			}
		}

		return (true);
	}

	//Fluff up the complex at the boundary by adding lower scoring, non complex-core neighbors
	private boolean fluffComplexBoundary(ArrayList complex, boolean[] nodeSeenArray) {
		int currentNode = 0, nodeNeighbor = 0;
		//create a temp list of nodes to add to avoid concurrently modifying 'complex'
		ArrayList nodesToAdd = new ArrayList();

		//Copy the nodeSeeArray because nodes seen during a fluffing should not be marked as permanently seen,
		//they can be included in another complex's fluffing step.
		boolean[] nodeSeenArrayInternal = new boolean[nodeSeenArray.length];
		System.arraycopy(nodeSeenArray, 0, nodeSeenArrayInternal, 0, nodeSeenArray.length);

		//add all current neighbour's neighbours into complex (if they have high enough clustering coefficients) and mark them all as seen
		for (int i = 0; i < complex.size(); i++) {
			currentNode = ((Integer) complex.get(i)).intValue();
			for (int j = 0; j < ((NodeInfo) nodeInfoHashMap.get(new Integer(currentNode))).numNodeNeighbors; j++) {
				nodeNeighbor = ((NodeInfo) nodeInfoHashMap.get(new Integer(currentNode))).nodeNeighbors[j];
				if ((nodeSeenArrayInternal[nodeNeighbor] == false) &&
				        ((((NodeInfo) nodeInfoHashMap.get(new Integer(nodeNeighbor))).density) > fluffNodeDensityCutOff)) {
					nodesToAdd.add(new Integer(nodeNeighbor));
					nodeSeenArrayInternal[nodeNeighbor] = true;
				}
			}
		}

		//Add fluffed nodes to complex
		if (nodesToAdd.size() > 0) {
			complex.addAll(nodesToAdd.subList(0, nodesToAdd.size()));
		}

		return (true);
	}

	/*Checks if the complex needs to be filtered according to heuristics in this method
	Optionally gives the complex a haircut (returns the 2-core as 'complex')*/
	private boolean shaveAndAHaircut(GraphPerspective gpInputGraph, ArrayList complex) {
		if (complex == null) {
			return (true);
		}

		//create an input graph for the core function
		int[] complexArray = new int[complex.size()];
		for (int i = 0; i < complex.size(); i++) {
			int nodeIndex = ((Integer) complex.get(i)).intValue();
			complexArray[i] = nodeIndex;
		}
		GraphPerspective gpComplexGraph = gpInputGraph.createGraphPerspective(complexArray);
		//filter if not a 2-core
		GraphPerspective gpCore = getKCore(gpComplexGraph, 2);
		if (gpCore == null) {
			return (true);
		}

		if (haircut) {
			//clear the complex and add all 2-core nodes back into it
			complex.clear();
			//must add back the nodes in a way that preserves gpInputGraph node indices
			int[] rootGraphIndices = gpComplexGraph.getNodeIndicesArray();
			for (int i = 0; i < rootGraphIndices.length; i++) {
				complex.add(new Integer(gpInputGraph.getNodeIndex(rootGraphIndices[i])));
			}
		}

		return (false);
	}

	//TODO: move the following methods into GINY, since they are general for graphs
	public double calcDensity(GraphPerspective gpInputGraph, boolean includeLoops) {
		int possibleEdgeNum = 0, actualEdgeNum = 0, loopCount = 0;
		double density = 0;

		String callerID = "MCODEAlgorithm.calcDensity";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return (-1.0);
		}

		if (includeLoops) {
			//count loops
			for (int i = 1; i <= gpInputGraph.getNodeCount(); i++) {
				if (gpInputGraph.isNeighbor(i, i)) {
					loopCount++;
				}
			}
			possibleEdgeNum = gpInputGraph.getNodeCount() * gpInputGraph.getNodeCount();
			actualEdgeNum = gpInputGraph.getEdgeCount() - loopCount;
		} else {
			possibleEdgeNum = gpInputGraph.getNodeCount() * gpInputGraph.getNodeCount();
			actualEdgeNum = gpInputGraph.getEdgeCount();
		}

		density = (double) actualEdgeNum / (double) possibleEdgeNum;
		return (density);
	}

	//find a k-core - returns a subgraph with the core, if any was found at given k
	public GraphPerspective getKCore(GraphPerspective gpInputGraph, int k) {
		String callerID = "MCODEAlgorithm.getKCore";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return (null);
		}

		//filter all nodes with degree less than k until convergence
		boolean firstLoop = true;
		int numDeleted;
		GraphPerspective gpOutputGraph = null;
		while (true) {
			numDeleted = 0;
			ArrayList alCoreNodeIndices = new ArrayList(gpInputGraph.getNodeCount());
			for (int i = 1; i <= gpInputGraph.getNodeCount(); i++) {
				if (gpInputGraph.getDegree(i) >= k) {
					alCoreNodeIndices.add(new Integer(i)); //contains all nodes with degree >= k
				} else {
					numDeleted++;
				}
			}
			if ((numDeleted > 0) || (firstLoop)) {
				//convert ArrayList to int[] for creation of a GraphPerspective for this core
				int[] outputNodeIndices = new int[alCoreNodeIndices.size()];
				int j = 0;
				for (Iterator i = alCoreNodeIndices.iterator(); i.hasNext(); j++) {
					outputNodeIndices[j] = ((Integer) i.next()).intValue();
				}
				gpOutputGraph = gpInputGraph.createGraphPerspective(outputNodeIndices);
				if (gpOutputGraph.getNodeCount() == 0) {
					return (null);
				}
				//iterate again, but with a new k-core input graph
				gpInputGraph = gpOutputGraph;
				if (firstLoop) {
					firstLoop = false;
				}
			} else {
				//stop the loop
				break;
			}
		}

		return (gpOutputGraph);
	}

	//find the highest k-core in the input graph.  Returns the k-value and the core as an Object array.
	public Object[] getHighestKCore(GraphPerspective gpInputGraph) {
		String callerID = "MCODEAlgorithm.getHighestKCore";
		if (gpInputGraph == null) {
			System.err.println("In " + callerID + ": gpInputGraph was null.");
			return (null);
		}

		int i = 1;
		GraphPerspective gpCurCore = null, gpPrevCore = null;

		while ((gpCurCore = getKCore(gpInputGraph, i)) != null) {
			gpInputGraph = gpCurCore;
			gpPrevCore = gpCurCore;
			i++;
		}

		Integer k = new Integer(i - 1);
		Object[] returnArray = new Object[2];
		returnArray[0] = k;
		returnArray[1] = gpPrevCore;    //in the last iteration, gpCurCore is null (loop termination condition)

		return (returnArray);
	}
}
