
//============================================================================
// 
//  file: ScoreModel.java
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



package nct.networkblast.score;

import nct.graph.Graph;

/**
 * This interface will provide the scoring interface for all scoring 
 * algorithms to be used on a Graph object.  Consequently, all scoring 
 * algorithms should implement this interface.
 */
public interface ScoreModel<NodeType extends Comparable<? super NodeType>,
                            WeightType extends Comparable<? super WeightType>> {
    /**
     * Given a sourceNode and destNode from a graph, this method will return 
     * the score of the edge.  
     * @param srcNode Source node of edge to score. 
     * @param destNode Dest node of edge to score. 
     * @param g The graph that contains the edge to score. 
     * @return The score for the given pair of nodes in the given graph.
     */
    public double scoreEdge(NodeType srcNode, NodeType destNode, Graph<NodeType,WeightType> g);

    /**
     * Given a node from graph, this function will return 
     * the score of the node. 
     * @param node Node to score.
     * @param g The graph that contains the node to score. 
     * @return The score for the node in the given graph.
     */
    public double scoreNode(NodeType node, Graph<NodeType,WeightType> g);
}
