
//============================================================================
// 
//  file: SearchGraph.java
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
import nct.networkblast.score.*;
import nct.graph.Graph;

/**
 * This interface will provide an interface for all searching algorithms to be 
 * used on a Graph object.  Consequently, all searching algorithms should 
 * implement this interface.
 */
public interface SearchGraph< NodeType extends Comparable<? super NodeType>,
                              WeightType extends Comparable<? super WeightType>>  {
    /**
     * This method will be used to call a search algorithm on the given graph 
     * object.  
     * @param graph The Graph object to search
     * @param scoreObj The ScoreModel object to use to score pathways in the graph
     * @return a List of SubGraphs which are the solutions to this search
     */
    public List<Graph<NodeType,WeightType>> searchGraph(Graph<NodeType,WeightType> graph, 
                                                        ScoreModel<NodeType,WeightType> scoreObj);
}

