
//============================================================================
// 
//  file: SortFilter.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.filter;

import java.util.*;
import nct.graph.Graph;

/**
 * This filter sorts the given solutions from best score (most positive) to 
 * worst.
 */

public class SortFilter<NodeType extends Comparable<NodeType>, WeightType extends Comparable<WeightType>> implements Filter<NodeType,WeightType> {
    /**
     * Given a List of Graphs, it sorts the graphs based on the comparable implementation 
     * of the graph. 
     * @param solutions the List of Graphs to sort
     */
    public List<Graph<NodeType,WeightType>> filter(List<Graph<NodeType,WeightType>> solutions) {
	if (solutions == null) {
	    return null;
	} else if (solutions.size() == 0) {
	    return new ArrayList();
	}
	Collections.sort(solutions);
	return solutions;
    }
}
