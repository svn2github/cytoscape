
//============================================================================
// 
//  file: EdgeWeightShuffle.java
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

package nct.graph.util;

import java.util.*;
import nct.graph.Graph; 
import nct.graph.Edge; 

/**
 * This class performs a basic Fisher-Yates shuffle of the edge weights.  
 * It creates a list of edges and then shuffles the weights within
 * that list.
 */
public class EdgeWeightShuffle<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> implements GraphRandomizer<NodeType,WeightType> {

	protected Random rand;
	
	public EdgeWeightShuffle(Random r) {
		rand = r;
	}

	/**
	 * Basic Fisher-Yates edge weight shuffle.
	 * @param g The graph whose edge weights should be shuffled.
	 */
	public void randomize(Graph<NodeType,WeightType> g) {

		List<Edge<NodeType,WeightType>> edges = g.getEdgeList(); 
		int size = edges.size();

		for ( int i = 0; i < size; i++ ) {
			int j = rand.nextInt(size);
			if ( i == j )
				continue;

			Edge<NodeType,WeightType> iEdge = edges.get(i);
			Edge<NodeType,WeightType> jEdge = edges.get(j);

			WeightType tmpWeight = g.getEdgeWeight(iEdge.getSourceNode(),iEdge.getTargetNode());
			g.setEdgeWeight( iEdge.getSourceNode(), iEdge.getTargetNode(), g.getEdgeWeight(jEdge.getSourceNode(),jEdge.getTargetNode()));
			g.setEdgeWeight( jEdge.getSourceNode(), jEdge.getTargetNode(), tmpWeight );

		}
	}
}
