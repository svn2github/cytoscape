
//============================================================================
// 
//  file: SearchGraph.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.networkblast.search;

import java.util.*;
import nct.networkblast.score.*;
import nct.graph.Graph;

/**
 * This interface will provide an interface for all searching algorithms to be 
 * used on a Graph object.  Consequently, all searching algorithms should 
 * implement this interface.
 */
public interface SearchGraph< NodeType extends Comparable<NodeType>,
                              WeightType extends Comparable<WeightType>>  {
    /**
     * This method will be used to call a search algorithm on the given graph 
     * object.  
     * @param graph The Graph object to search
     * @param scoreObj The ScoreModel object to use to score pathways in the graph
     * @return a List of SubGraphs which are the solutions to this search
     */
    public List<Graph<NodeType,WeightType>> searchGraph(Graph<NodeType,WeightType> graph, 
                                                        ScoreModel scoreObj);
}

