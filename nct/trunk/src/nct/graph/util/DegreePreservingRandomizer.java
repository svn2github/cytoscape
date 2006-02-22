package graph.util;

import java.util.*;
import nct.graph.Graph;

public abstract class DegreePreservingRandomizer implements GraphRandomizer {

	protected Random rand;
	
	public DegreePreservingRandomizer(Random r) {
		rand = r;
	}

	public <N extends Comparable<? super N>,W extends Comparable<? super W>> 
		void randomize(Graph<N,W> g) {
		
		List<N> nodes = new ArrayList<N>(g.getNodes());
		
		for ( int e = 0; e < g.numberOfEdges(); e++ ) {
			N i;
			N j;
			N va;
			N vb;
			
			while (true) {
				i = nodes.get( rand.nextInt(nodes.size()) );
				j = nodes.get( rand.nextInt(nodes.size()) );
		
				List<N> iNeighbors = new ArrayList<N>(g.getNeighbors(i));
				List<N> jNeighbors = new ArrayList<N>(g.getNeighbors(j));

				
				int iDegree = iNeighbors.size();
				int jDegree = jNeighbors.size();

				if ( i.compareTo(j) == 0 || iDegree <= 0 || jDegree <= 0 )
				     continue;

				va = iNeighbors.get( rand.nextInt(iNeighbors.size()) );
				vb = jNeighbors.get( rand.nextInt(jNeighbors.size()) );					
				if ( va.compareTo(vb) == 0 || va.compareTo(j) == 0 || vb.compareTo(i) == 0 )
					continue;

				if (weightsSimilar(g.getEdgeWeight(i,va),g.getEdgeWeight(j,vb)))
					break;
			}
			
			W iWeight = g.getEdgeWeight(i,va);
			W jWeight = g.getEdgeWeight(j,vb);

			g.removeEdge(i,va);
			g.removeEdge(j,vb);

			g.addEdge(i,vb,jWeight);
			g.addEdge(j,va,iWeight);
		}
	}

	public abstract <W extends Comparable<? super W>> boolean weightsSimilar( W a, W b );
}
