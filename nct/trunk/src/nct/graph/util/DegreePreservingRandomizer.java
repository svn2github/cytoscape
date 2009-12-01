
//============================================================================
// 
//  file: DegreePreservingRandomizer.java
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

/**
 * This class shuffles the edges in a graph while preserving 
 * the degree of each node in the class and only swapping edges
 * with "similar" edge weights. In the default implementation of
 * this class "similar" means equal weight.  To create an
 * alternative definition of "similar" simply extend this class
 * and re-implement the weightsSimilar() method. 
 */
public class DegreePreservingRandomizer<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> implements GraphRandomizer<NodeType,WeightType> {

	protected Random rand;
	protected boolean ignoreWeights;
	
	public DegreePreservingRandomizer(Random r, boolean ignoreWeights) {
		rand = r;
		this.ignoreWeights = ignoreWeights;
	}

	/**
	 * The method that performs the randomization.
	 * @param g The Graph whose edges are to be randomized.
	 */
	public void randomize(Graph<NodeType,WeightType> g) {
		
		List<NodeType> nodes = g.getNodeList();
		
		for ( int e = 0; e < g.numberOfEdges(); e++ ) {
			NodeType i;
			NodeType j;
			NodeType vi;
			NodeType vj;
			
			while (true) {
				i = nodes.get( rand.nextInt(nodes.size()) );
				j = nodes.get( rand.nextInt(nodes.size()) );
		
				List<NodeType> iNeighbors = g.getNeighborList(i);
				List<NodeType> jNeighbors = g.getNeighborList(j);

				
				int iDegree = iNeighbors.size();
				int jDegree = jNeighbors.size();

				if ( i.compareTo(j) == 0 || iDegree <= 0 || jDegree <= 0 )
				     continue;

				vi = iNeighbors.get( rand.nextInt(iNeighbors.size()) );
				vj = jNeighbors.get( rand.nextInt(jNeighbors.size()) );					
				if ( vi.compareTo(vj) == 0 || vi.compareTo(j) == 0 || vj.compareTo(i) == 0 )
					continue;

				// don't want to stomp on existing edges
				if ( g.isEdge(i,vj) || g.isEdge(j,vi) )
					continue;

				if (ignoreWeights || weightsSimilar(g.getEdgeWeight(i,vi),g.getEdgeWeight(j,vj)))
					break;

			}
			
			WeightType iWeight = g.getEdgeWeight(i,vi);
			WeightType jWeight = g.getEdgeWeight(j,vj);

			g.removeEdge(i,vi);
			g.removeEdge(j,vj);

			g.addEdge(i,vj,jWeight);
			g.addEdge(j,vi,iWeight);

			//System.out.println("interim randomized graph:");
			//System.out.println(g.toString());

		}
	}

	/**
	 * Checks to see if the given weights are "similar".  The default
	 * (this) implementation treats weights as similar if they are
	 * equal.  To get different behavior simply extend this class and
	 * override this method.
	 * @param a Weight a to be compared.
	 * @param b Weight b to be compared.
	 * @return true if if the weights are "similar", false otherwise.
	 */
	public boolean weightsSimilar( WeightType a, WeightType b ) {
		if ( a.compareTo( b ) == 0 )
			return true;
		else
			return false;
	}
}
