
//============================================================================
// 
//  file: ScoreModel.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
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
     * Given a sourceNode and destNode from graph, this function will return 
     * the score of the edge.  Note that this method does not define what
     * occurs if no such edge exists between the two nodes (presumably it 
     * would return some background probability or minimal score).
     * @param srcNode Node to start score search from.
     * @param destNode Node to end score search at.
     * @return The score for the given pair of nodes in the given graph.
     */
    public double scoreEdge(String srcNode, String destNode, Graph<NodeType,WeightType> g);
}
