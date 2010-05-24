
//============================================================================
// 
//  file: SimpleEdgeScoreModel.java
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

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import nct.graph.Graph;
import nct.graph.Edge;

/**
 * This class implements a simple score model that basically
 * just returns the weight associated with a particular object
 */
public class SimpleEdgeScoreModel<NodeType extends Comparable<? super NodeType>> 
	implements ScoreModel<NodeType,Double> {
    /**
     * For the two nodes from the specified graph graph, return the log
     * likelihood score between the complex model and the null model. 
     * @param srcNode node bewteen which it and destNode generates a score
     * @param destNode node between which it and srcNode generates a score
     * @param graph graph containing both srcNode and destNode
     * @return the score of the pathway between the nodes or 0 if the same node
     */
    public double scoreEdge(NodeType srcNode, NodeType destNode, Graph<NodeType,Double> graph) {
	return graph.getEdgeWeight(srcNode, destNode).doubleValue();
    }

    public double scoreNode(NodeType node, Graph<NodeType,Double> graph){
	return 0; 
    }
}	

