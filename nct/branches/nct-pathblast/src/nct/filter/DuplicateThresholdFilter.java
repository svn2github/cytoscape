
//============================================================================
// 
//  file: DuplicateThresholdFilter.java
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



package nct.filter;

import java.util.*;
import nct.graph.Graph;

/**
 * This class will throw out any solutions with a certain percentage of duplicate nodes.
 * This filter is NOT synchronized! (HashSet)
 */
public class DuplicateThresholdFilter<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> implements Filter<NodeType,WeightType> {
    /**
     * Holds the threshold value for this filter.  Any two solutions with 
     * similarity >= this threshold will be thrown out.
     */
    private double threshold;
    
    /**
     * Sets the threshold according to the given value.
     * @param thresh The threshold to check against
     */
    public DuplicateThresholdFilter(double thresh) {
	threshold = thresh;
    }

    /**
     * Given the solutions, this filter compares the nodes contained in each graph,
     * calculates the percentage of identical nodes, and throws out any solutions
     * that have a percent identity greater than or equal to the specified threshold.
     * This function will not affect the solutions list but is not a deep copy either.
     * @param solutions The List of solutions to process.
     * @return Either a filtered solution set or null if solutions is null.
     */
    public List<Graph<NodeType,WeightType>> filter(List<Graph<NodeType,WeightType>> solutions) {
	if (solutions == null) 
	    return null;
	//System.out.println("unfiltered set size: " + solutions.size());
	List<Graph<NodeType,WeightType>> newSolns = new Vector<Graph<NodeType,WeightType>>();
	boolean skip = false;
	for ( Graph<NodeType,WeightType> seed: solutions ) {
	    HashSet<NodeType> nodesFromSeed = new HashSet<NodeType>(seed.getNodes());
	    for (Graph<NodeType,WeightType> branch: newSolns) {
		HashSet<NodeType> nodesFromBranch = new HashSet<NodeType>(branch.getNodes());
		int originalSize = nodesFromBranch.size();
		nodesFromBranch.retainAll(nodesFromSeed);
		// Omit solutions with percent identity greater than or equal to
		// the threshold.
		if (((double)nodesFromBranch.size())/originalSize >= threshold) {
		    skip = true;
		    break;  
		}		
	    }
	    if (skip) {
		skip = false;
		continue;
	    }
	    newSolns.add(seed);
	}
	//System.out.println("filtered set size: " + newSolns.size());
	return newSolns;
    }
}
		 
		
