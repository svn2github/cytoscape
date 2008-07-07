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


import java.util.*;
import cytoscape.graph.dynamic.*;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

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
	
	public String getDisplayName()
	{
		return new String("Clustering Coefficient");
	}
	
	
	
	/*---------------------------------------------------------------
	 *
	 * @param Network network the network to analyze s
	 *
	 *---------------------------------------------------------------*/
	public double analyze(DynamicGraph network, boolean directed)
	{
		//used to accumulate the clustering coefficient of each node
		double averageClusteringCoefficient = 0;
		
		
		//Iterate through all of thie nodes in this network
		IntEnumerator nodeIterator = network.nodes();
		
		//Use as the number of nodes in the network
		int N  = nodeIterator.numRemaining();
		
		//Use this to temo
		LinkedList nodeRep[] = new LinkedList[N];
		
		//Keep track of the node index
		int nodeCount = 0;
		
	
		while(nodeIterator.numRemaining() > 0)
		{
			//Get the next node
			int nodeIndex = nodeIterator.nextInt();
			
			//Create a new linked list to store these edges
			nodeRep[nodeCount] = new LinkedList<Integer>();
		
			//Iterate through all of this nodes edges
			//Only outgoing edges should be counted if directed
			//Second false does not include incoming edges
			IntEnumerator edgeIterator = network.edgesAdjacent(nodeIndex,true,false,true);
			while(edgeIterator.numRemaining() > 0)
			{
				//Get the next edge
				int edgeIndex = edgeIterator.nextInt();
				
				//Find the other side of this edge
				int neighborIndex = network.edgeSource(edgeIndex);
				
				//If we got the wrong side
				if(neighborIndex == nodeIndex)
				{
					//grab the other side
					neighborIndex = network.edgeTarget(edgeIndex);
				}
				
				//Add this node to the list of neighboring nodes
				nodeRep[nodeCount].add(neighborIndex);
			}
		
			//Increment the number of nodes
			nodeCount++;	
		}
		
		
		//For each node
		for(int i = 0; i < N; i++)
		{
			//If the node degree is less than 2
			if(nodeRep[i].size() < 2)
			{
				//Skip it since edgeCount must be 0
				continue;
			}

			//Count the number of edges shared amongst its neighbors
			double edgeCount = 0;
			
			//How big is this nodes neighborhood
			double size = nodeRep[i].size() * (nodeRep[i].size()  -1 );

			//Iterate through this nodes edges
			ListIterator inner_iter = nodeRep[i].listIterator();
			while(inner_iter.hasNext())
			{			
				//Get the next neighbor
				int neighbor1 = (Integer)inner_iter.next(); 

				//Iterate through all of the other neighbors
				ListIterator outer_iter = nodeRep[i].listIterator();
				while(outer_iter.hasNext())
				{
					//Get the next neighbor
					int neighbor2 = (Integer) outer_iter.next();
					
					//If these are the same then skip it
					//We do not count reflexive loops
					if(neighbor1 != neighbor2)
					{
						//Check to see if this node is connected  to the other
						if(nodeRep[neighbor1].contains(neighbor2))
						{
							//Increment the edge count
							edgeCount++;
						}
					}
				}
			}
			
			//If this network is not directed, double the edge count
			if(!directed)
			{
				edgeCount *= 2;
			}
			
			//Increment the global count by this edges clustering coefficient
			averageClusteringCoefficient += edgeCount / size;
		}
		
		//Remove this datastructure
		nodeRep = null;
		
		//return the average over all of the nodes
		return averageClusteringCoefficient/N;	
		
	}
}
		