
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

public class EdgeWeightShuffle<N extends Comparable<? super N>,W extends Comparable<? super W>> implements GraphRandomizer<N,W> {

	protected Random rand;
	
	public EdgeWeightShuffle(Random r) {
		rand = r;
	}

	/**
	 * Basic Fisher-Yates edge weight shuffle.
	 */
	public void randomize(Graph<N,W> g) {

		List<Edge<N,W>> edges = new ArrayList<Edge<N,W>>(g.getEdges());
		int size = edges.size();

		for ( int i = 0; i < size; i++ ) {
			int j = rand.nextInt(size);
			if ( i == j )
				continue;

			Edge<N,W> iEdge = edges.get(i);
			Edge<N,W> jEdge = edges.get(j);

			W tmpWeight = g.getEdgeWeight(iEdge.getSourceNode(),iEdge.getTargetNode());
			g.setEdgeWeight( iEdge.getSourceNode(), iEdge.getTargetNode(), g.getEdgeWeight(jEdge.getSourceNode(),jEdge.getTargetNode()));
			g.setEdgeWeight( jEdge.getSourceNode(), jEdge.getTargetNode(), tmpWeight );

		}
	}
}
