/* File: MeanShortestPath.java
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
*  Compute the avearge shortest path between all pairs of nodes (i,j), where
*  i != j.
*/
public  class MeanShortestPathMetric implements NetworkMetric {
	
	public Comparable analyze(CyNetwork network, boolean directed)
	{
		//Accumlate the distances between all nodes
		double averageShortestPath = 0;
		
		//Get the number of nodes
		int N = network.getNodeCount();
		
		//Create the adjacency matrix
		int adjacencyMatrix[][] = new int[N][N];
		
		//Create a node place to hold the nodes, assigns
		//them a location between 0 and N - 1.
		Node nodes[] = new Node[N];

		//Keep track of the number of nodes seen so far
		int count = 0;
		
		//Iterate through all of the ndes
		Iterator iter = network.nodesIterator();
		while(iter.hasNext())
		{
			//get the enxt node
			Node next = (Node)iter.next();
			//Save a pointer to that node in the array
			nodes[count] = next;
			//increment the count
			count++;
		}	
		

		//Iterate through all pairs of nodes
		for(int i = 0; i < N; i++)
		{
			for(int j = 0; j < N; j++)
			{
				//Check to see if the edge exists
				if(network.edgeExists(nodes[i],nodes[j]))
				{
					//Update the value in the Adjacency matrix
					adjacencyMatrix[i][j] = 1;
					
					//if undirected 
					if(!directed)
					{
						adjacencyMatrix[j][i] = 1;
					}
				}
			}
		}
		

	
		int invalidPaths = 0;
		//Below is an implementation of Dijkstra's algorithm
		//Everything above creates the adjacency matrix
		for(int i = 0; i < N; i ++)
		{
			//determine the distance from i to every other node
			int distance[] = new int[N];
			//Keep track of which nodes have been used so far
			boolean used[] = new boolean[N];
			
			//Initialize the variables for every node
			for(int j = 0; j < N; j++)
			{
				//Pretened the distanc is infinite
				distance[j] = Integer.MAX_VALUE;
				
				//Mark all nodes as unused
				used[j] = false;
				
				//If this node is connected
				if(adjacencyMatrix[i][j] == 1)
				{
					//set its distance as 1
					distance[j] = 1; 
				}
			}
			
			//Nodes that can be used on a path from i to j
			//can only be used if the distance from i to k is
			//less than or equal to allowed
			for(int allowed = 1; allowed < N; allowed++)
			{
				//Find the closest node
				int min = Integer.MAX_VALUE;
				int index = 0;
				for(int j = 0; j < N; j++)
				{
					if((min > distance[j]) && (!used[j]))
					{
						min = distance[j];
						index = j;
					}
				}
				
				//Mark the closest node as used
				used[index] = true;
				
				//Update the distances for all nodes
				for(int k = 0; k < N; k++)
				{
					//If this node is not yet used
					if((!used[k]) && (adjacencyMatrix[index][k] == 1))
					{
						int sum = distance[index] + 1;
						if(sum < distance[k])
						{
							distance[k] = sum;
						}
					}
				}
			}
			
			
			//Add the distances to the total sum
			for(int j = 0; j < N; j++)
			{
				//Don't add the distance from a node to another node
				if( i != j)
				{
					if(distance[j] < Integer.MAX_VALUE)
					{	
						averageShortestPath += distance[j];
					}
					else
					{
						invalidPaths++;
					}
				}
			}
		}
		
		
		//return the average distance between nodes, 
		return new Double(averageShortestPath/((double)( N * (N - 1.0d) - invalidPaths)));
	}
	
}





