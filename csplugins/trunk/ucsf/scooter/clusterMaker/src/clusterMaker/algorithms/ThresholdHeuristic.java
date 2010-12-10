package clusterMaker.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import clusterMaker.algorithms.DistanceMatrix;

public class ThresholdHeuristic{

    private List<CyNode> nodes;
    private List<CyEdge> edges;
    private double[] edgeWeights;

    //total minimum and maximum weights encoutered in edgeWeight array
    private int totalMin = 100000000;
    private int totalMax = -100000000;

    public ThresholdHeuristic(DistanceMatrix matrix){

	this.edgeWeights = matrix.getEdgeValues();
	this.nodes = matrix.getNodes();
	this.edges = matrix.getEdges();
    }


    //run threshold heuristic, returning -1000 if no threshold is found
    public int run(){

	int[] numConnectedNodes  = getNumConnectedNodes();
	int[] seArray = getSEarray();
	
	if(seArray.length == 0)
	    return -1000;

	return selectThreshold(numConnectedNodes,seArray);
    }

    //return the number of non-singleton nodes at each edge weight (rounded to the nearest integer)
    private int[] getNumConnectedNodes(){

	
	//maps id of each node to its maximum edge weight connection
	HashMap <Integer,Integer>  maxNodeConnection = new HashMap();
	
	//each index of array maps number of non-singleton nodes remaining at index (taking account a shift such that lowest index is zero)
	int[] numConnectedNodes = null;



	for(int i = 0; i < edgeWeights.length; i++){
	    
	    int edgeWeight = (int)edgeWeights[i];

	    //update totalMin and totalMax, if neccesary
	    if(totalMin > edgeWeight)
		totalMin = edgeWeight;

	    if(totalMax < edgeWeight)
		totalMax = edgeWeight;


	    //get edge corresponding to edgweight, with associated source and target nodes
	    CyEdge edge = edges.get(i);
	    Integer sourceIndex = new Integer(nodes.indexOf(edge.getSource()));
	    Integer targetIndex = new Integer(nodes.indexOf(edge.getTarget()));

	    
	    //update the maximum EdgeWeight associated with each source and target nodes
	    if(maxNodeConnection.get(sourceIndex) != null){

		int maxWeight = maxNodeConnection.get(sourceIndex).intValue();

		//update max EdgeWeight for node if neccesary
		if(maxWeight < edgeWeight)
		    maxNodeConnection.put(sourceIndex,new Integer(edgeWeight));
	    }

	    else
		maxNodeConnection.put(sourceIndex,new Integer(edgeWeight));

	    if(maxNodeConnection.get(targetIndex) != null){

		int maxWeight = maxNodeConnection.get(targetIndex).intValue();

		//update max EdgeWeight for node if neccesary
		if(maxWeight < edgeWeight)
		    maxNodeConnection.put(targetIndex,new Integer(edgeWeight));
	    }

	    else
		maxNodeConnection.put(targetIndex,new Integer(edgeWeight));
	}
	   


	//initialize array
	numConnectedNodes = initDistributionArray();

	//Iterate through maxNodeConnection values, updating the numConnectedNodes array
	Iterator itr = maxNodeConnection.values().iterator();

	while(itr.hasNext()){

	    int value = ((Integer)itr.next()).intValue();
	 
	    int index = shiftIndex(value);

	    //increment numConnected Nodes at index
	    numConnectedNodes[index] += 1;
	}

	return numConnectedNodes;
    }

    //initialize numConnectedNodes array or SEarray
    private int[] initDistributionArray(){

	int[] distribArray = new int[shiftIndex(totalMax + 1)];

	for(int i = 0; i < distribArray.length; i++)
	    distribArray[i] = 0;

	return distribArray;
    }

    //takes edge weight threshold value and adjusts it thus that the min threshold corresponds to Zero index in array
    private int shiftIndex(int value){return (value - totalMin);}

    //intializes array where each index represents the number of edges remaining after a shifted threshold is applied
    private int[] getSEarray(){

	int[] distribArray = initDistributionArray();
	int[] seArray = initDistributionArray();

	//loop through edge weights, and create the network distribution
	for(int i = 0; i < edgeWeights.length; i++){

	    int edgeWeight = (int)edgeWeights[i];
	    int index = shiftIndex(edgeWeight);
	    distribArray[index] += 1;
	}
	    
	
	//Create SE array from edgeweight distribution

	for(int i = 0; i < distribArray.length; i++)
	    for(int j = 0; j < distribArray.length; j++)
		seArray[i] += distribArray[j];

	return seArray;
    }

    //use change in NSV distribution to calculate threshold
    private int selectThreshold(int[] numConnectedNodes, int[] seArray){

      

	double oldNSV = (double)numConnectedNodes[0]/(double)seArray[0]; 
	double newNSV;
	double deltaNSV;
	
	for(int i = 1; i < seArray.length; i ++){

	    newNSV = (double)numConnectedNodes[i]/(double)seArray[i];
	    deltaNSV = newNSV - oldNSV;
	    
	    //dNSV/dTH is positive, shift index by minimum edge weight and return as threshold
	    if(deltaNSV > 0)
		return (i + totalMin);
	}

	//If no threshold found, retun -1000
	return -1000;

    }
}


