
//============================================================================
// 
//  file: CompatibilityCalculator.java
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


