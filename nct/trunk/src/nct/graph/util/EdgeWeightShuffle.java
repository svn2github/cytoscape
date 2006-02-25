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
