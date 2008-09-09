/* File: ClusteringCoefficientMetric.java
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
 *	Used to determine the clustering coefficient for a CyNetwork.
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
	
	/**
	 * Returns the name of this metric.
	 *
	 * @return The name of this metric.
	 */
	public String getDisplayName()
	{
		return new String("Clustering Coefficient");
	}
	
		
	/**
	 * @return A new ClusteringCoefficientMetric instace.
	 */
	public NetworkMetric copy()
	{
		return new ClusteringCoefficientMetric();
	}

	
	
	/**
	 * The clustering coefficient for a network measures how close to complete each node's neighborhood is.
	 * The neighborhood of a node is the set of nodes that it is connected to by edges.  The clustering coefficent
	 * is a measure of how close the sub-network is to being complete.  The clustering coefficent of a node, is 1
	 * if its neighborhood is a complete graph.
	 * <p><b>Note: </b> The neighborhood of a node <i>u</i> does not include the node itself <i>u</i>.
	 * <p>
	 * The clustering coefficent of a graph (network) is the average clustering coefficient of all nodes.
	 * 
	 *
	 *
	 * @param pNetwork The network to analyze.
	 * @param pDirected Specifices how to treat the network. This matters for this metric.
	 * @return The clustering coeffcient for this network.
	 */
	public double analyze(RandomNetwork pNetwork, boolean pDirected)
	{
		//used to accumulate the clustering coefficient of each node
		double averageClusteringCoefficient = 0;
				
		//Iterate through all of thie nodes in this network
		IntEnumerator nodeIterator = pNetwork.nodes();
		
		//Use as the number of nodes in the network
		int N  = nodeIterator.numRemaining();
		
		//Use this to temo
		LinkedList nodeInRep[] = new LinkedList[N];
		LinkedList nodeOutRep[] = new LinkedList[N];		

		//Keep track of the node index
		int nodeCount = 0;
		
	    //Do pre-processing for each node
		while(nodeIterator.numRemaining() > 0)
		{
			
			//Get the next node
			int nodeIndex = nodeIterator.nextInt();
			
			//Create a new linked list to store these edges
			nodeOutRep[nodeIndex] = new LinkedList<Integer>();
			
			//Only create the in lists if the network is directed
			if(pDirected)
			{
				//create a list of all of the nodes that point to this node
				nodeInRep[nodeIndex] = new LinkedList<Integer>();		
			}
			
			//Iterate through all of this node's incoming edges if directed
			//or all edges if the network is undirected.
			IntEnumerator edgeIterator = pNetwork.edgesAdjacent(nodeIndex,pDirected,false,!pDirected);
			while(edgeIterator.numRemaining() > 0)
			{
				//Get the next edge
				int edgeIndex = edgeIterator.nextInt();
				
				//Find the other side of this edge
				int neighborIndex = pNetwork.edgeTarget(edgeIndex);
				
				//If we got the wrong side
				if(neighborIndex == nodeIndex)
				{
					//grab the other side
					neighborIndex = pNetwork.edgeSource(edgeIndex);
				}
				
				//Add this node to the list of neighboring nodes
				nodeOutRep[nodeIndex].add(new Integer(neighborIndex));
			}
		
		
			//If this network is directed
			if(pDirected)
			{
				//Iterate through all of the incoming edges
				edgeIterator = pNetwork.edgesAdjacent(nodeIndex,false,true,false);
				while(edgeIterator.numRemaining() > 0)
				{
					//Get the next edge
					int edgeIndex = edgeIterator.nextInt();
					
					//Find the other side of this edge
					int neighborIndex = pNetwork.edgeTarget(edgeIndex);
					
					//If we got the wrong side
					if(neighborIndex == nodeIndex)
					{
						//grab the other side
						neighborIndex = pNetwork.edgeSource(edgeIndex);
					}
					
					//Add this node to the list of neighboring nodes
					nodeInRep[nodeIndex].add(new Integer(neighborIndex));
				}
			}
			
		}
		
		
		//Compute the clustering coefficent for each node
		for(int i = 0; i < N; i++)
		{
			
			//Count the number of edges shared amongst its neighbors
			double edgeCount = 0;
						
			//Below combines in and out edges without overlap of directed nodes
			LinkedList neighborhood = (LinkedList)nodeOutRep[i].clone();
			
			//If this is a directed network
			if(pDirected)
			{
				//iterate through all of the incoming edges
				ListIterator iter = nodeInRep[i].listIterator();
				while(iter.hasNext())
				{
					//get the next node
					Integer next = (Integer)iter.next();
					
					//Comparison of Integer class is done by value
					if(!neighborhood.contains(next))
					{
						neighborhood.add(next);
					}
				}
			}
			
			
			//If the node degree is less than 2
			if(neighborhood.size() < 2)
			{
				//Skip it since edgeCount must be 0
				continue;
			}
			
			
			//How big is this nodes neighborhood
			double size = neighborhood.size() * (neighborhood.size()  - 1);

			//Iterate through this nodes edges
			while(neighborhood.size() > 0)  
			{			
				//Get the next neighbor
				int neighbor1 = (Integer)neighborhood.removeFirst();   

				//Iterate through all of the other neighbors
				ListIterator outer_iter = neighborhood.listIterator();
				while(outer_iter.hasNext())
				{
					//Get the next neighbor
					int neighbor2 = (Integer) outer_iter.next();
					
					//If these are the same then skip it
					//We do not count reflexive loops
					//Check to see if this node is connected  to the other
					if(nodeOutRep[neighbor1].contains(neighbor2))
					{
						//Increment the edge count
						edgeCount++;
					}
					//Check to see if this node is connected  to the other
					if((pDirected) && (nodeOutRep[neighbor2].contains(neighbor1)))
					{
						//Increment the edge count
						edgeCount++;
					}
				}
			}
			
			//If this network is not directed, double the edge count
			if(!pDirected)
			{
				edgeCount *= 2.0;	
			}
			
			//Increment the global count by this edges clustering coefficient
			averageClusteringCoefficient += edgeCount / size;
		}
		
		//Remove this datastructure to make sure the memory is released
		nodeInRep = null;
		nodeOutRep = null;		
		
		//return the average over all of the nodes
		return averageClusteringCoefficient/N;	
		
	}
}
		