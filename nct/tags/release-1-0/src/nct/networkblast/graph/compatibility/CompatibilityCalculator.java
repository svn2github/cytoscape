
//============================================================================
// 
//  file: CompatibilityCalculator.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.networkblast.graph.compatibility;

import java.lang.*;
import java.util.*;

import nct.graph.Graph;
import nct.graph.DistanceGraph;

/**
 * Provides an interface for alternative methods of determining
 * which potential compatibility nodes should be included in the
 * compatibility graph and how the edge scores between the nodes
 * are calculated.
 */
public interface CompatibilityCalculator {

	/**
	 * The method that determines which nodes to add, adds them
	 * if appropriate, and calculates the edge score.
	 * @param compatGraph The compatibility graph that appropriate 
	 * compatibility nodes and edges are added to.
	 * @param partitionGraphs Possibly used to 
	 * @param nodeBase An array of nodes from the respective 
	 * partition graphs that form a potential compatibility node.
	 * @param nodeBranch An array of nodes from the respective 
	 * partition graphs that form a potential compatibility node.
	 */
	boolean calculate( Graph<String,Double> compatGraph, List<? extends DistanceGraph<String,Double>> partitionGraphs, String[] nodeBase, String[] nodeBranch );
	
}


