
//============================================================================
// 
//  file: AdditiveCompatibilityCalculator.java
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



package nct.networkblast.graph.compatibility;

import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import nct.networkblast.score.ScoreModel;
import nct.graph.Graph;
import nct.graph.DistanceGraph;

/**
 * This class creates the compatibility edge score by summing the edge 
 * weights of the interaction edges that comprise the compatibility node.
 */
public class AdditiveCompatibilityCalculator implements CompatibilityCalculator {
	

	protected double orthologyThreshold;
	protected ScoreModel<String,Double> scoreModel;
	protected boolean allowZero; 

	/**
	 * @param orthologyThreshold The orthology threshold for determining if 
	 * a compatibility edge weight is sufficient for inclusion.
	 * @param scoreModel The ScoreModel used to calculate edge weights.
	 * @param allowZero Whether or not edges of 0 distance (i.e. potential compat nodes
	 * where a constituent node is the same for both potential compat nodes).
	 */
	public AdditiveCompatibilityCalculator( double orthologyThreshold, ScoreModel<String,Double> scoreModel, boolean allowZero ) {
		this.orthologyThreshold = orthologyThreshold;
		this.scoreModel = scoreModel;
		this.allowZero = allowZero;
	}

	/**
	 * @param orthologyThreshold The orthology threshold for determining if 
	 * a compatibility edge weight is sufficient for inclusion.
	 * @param scoreModel The ScoreModel used to calculate edge weights.
	 */
	public AdditiveCompatibilityCalculator( double orthologyThreshold, ScoreModel<String,Double> scoreModel ) {
		this(orthologyThreshold, scoreModel, false);
	}

        /**
         * The method that determines which nodes to add, adds them
         * if appropriate, and calculates the edge score.
         * @param compatGraph The compatibility graph that appropriate
         * compatibility nodes and edges are added to.
         * @param partitionGraphs Possibly used to
         * @param nodeBase An array of nodes from the respective
         * partition graphs that form a potential compatibility node.
         * @param nodeBranch An array of nodes from the respective
         * partition graphs that form a potential compatibility node.
         */
	public boolean calculate( Graph<String,Double> compatGraph, List<? extends DistanceGraph<String,Double>> partitionGraphs, String[] nodeBase, String[] nodeBranch ) {
		
		int numGraphs = partitionGraphs.size();

		// first do the distances
		byte[] distance = new byte[numGraphs];

		boolean foundOne = false;
                boolean foundZero = false;
                boolean foundThree = false;
		for ( int z = 0; z < numGraphs; z++ ) {
			distance[z] = partitionGraphs.get(z).getDistance(nodeBase[z],nodeBranch[z]);
			if ( distance[z] == (byte)1 )
				foundOne = true;
                        if ( distance[z] == (byte)3 )
                      		foundThree = true;
                        if ( distance[z] == (byte)0 )
                      		foundZero = true;
		}

		if ( !foundOne )
			return false;
		if ( foundThree )
			return false;
                if ( foundZero && !allowZero )
			return false;

		// then the weights
		double edgeWeight = 0;
		for ( int z = 0; z < numGraphs; z++ )
			edgeWeight += scoreModel.scoreEdge(nodeBase[z],nodeBranch[z],partitionGraphs.get(z));
		if ( edgeWeight < orthologyThreshold )
			return false;

		String node1 = createNode( nodeBranch );
		String node2 = createNode( nodeBase );

		StringBuffer distDesc = new StringBuffer();
		for ( int z = 0; z < numGraphs; z++ )
			distDesc.append( Byte.toString(distance[z] ));

		compatGraph.addNode(node1);
		compatGraph.addNode(node2);
                boolean status = compatGraph.addEdge(node1,node2, new Double(edgeWeight), distDesc.toString());		
		return status;

	}

        private String createNode( String[] nodes ) {
                StringBuffer node1 = new StringBuffer();
                int numGraphs = nodes.length;
                for ( int z = 0; z < numGraphs-1; z++ ) {
                        node1.append(nodes[z]);
                        node1.append("|");
                }
                node1.append(nodes[numGraphs-1]);

                return node1.toString();
        }
}



