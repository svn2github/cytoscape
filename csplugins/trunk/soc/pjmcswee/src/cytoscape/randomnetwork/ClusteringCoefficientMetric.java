/* File: ClusteringCoefficient.java
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.randomnetwork;


import cytoscape.*;
import java.util.*;
import giny.model.*;

/**
 *	Used to determine the clustering coefficient for a CyNetwork
 *  Really determines the average clustering coefficient for
 *  every node in the network.
 *  The clustering coefficient for a node is the number of 
 *  edges in its neighborhood divided by the number of possible
 *  edges in the neighborhood.  The neighborhood of a node u is
 *  the set of nodes which contains u and all of the nodes which
 *  are connected to u.  
 *  We Ignore self-reflexive loops in the neighborhood
 */
public class ClusteringCoefficientMetric implements NetworkMetric {
	
	public Comparable analyze(CyNetwork network, boolean directed)
	{
		//used to accumulate the clustering coefficient of each node
		double averageClusteringCoefficient = 0;
		
		
		//Iterate over all of the nodes in the network
		Iterator netIter = network.nodesIterator();
		while(netIter.hasNext())
		{
				//Get the next node
				Node current = (Node)netIter.next();
			
				//Iterate through the neighborhood
				List neighbors = network.neighborsList(current);
				double neighborhoodSize = neighbors.size();

				//Get the list of connecting edges in the neighborhood
				List edges = network.getConnectingEdges(neighbors);
			
				//Number of edges in the neighborhood
				double edgesFound = edges.size();
				
				
				//While there are still more edges to check
				while(neighbors.size() > 0)
				{
					//Get the next Node from the neighborhood
					Node neighbor = (Node)neighbors.remove(neighbors.size() - 1);
				
					//Check for self-reflective loops on this node
					List reflexive = network.edgesList(neighbor,neighbor);
					
					//if any are found then subtract from the number of edges
					edgesFound -= reflexive.size();

				}
				
				//local variable for this node
				double nodeClusteringCoefficient = 0;
				
				//Compute the maximum possible number of edges
				double divisor =  neighborhoodSize * (neighborhoodSize - 1);

				//If the network is directed
				nodeClusteringCoefficient = edgesFound / divisor;
				
				//Multiple by two if not directed
				if(!directed)
				{
					nodeClusteringCoefficient *= 2.0d;
				}
				
				//Neighborhoods with less than 1 node have no edges
				if((divisor > 0) && (edgesFound > 0))
				{
					averageClusteringCoefficient += nodeClusteringCoefficient;
				}
		}
		
		//The number of nodes
		double N = network.getNodeCount();

		//return the average clustering coefficient
		return new Double(averageClusteringCoefficient/N);
	}	
}





