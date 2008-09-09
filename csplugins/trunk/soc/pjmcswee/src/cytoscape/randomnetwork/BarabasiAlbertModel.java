/* File: BarabasiAlbertModel.java

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

import cytoscape.graph.dynamic.*;
import cytoscape.graph.dynamic.util.*;


/**
 *  Barabasi-Albert Model: Creates random networks with the scale-free property.  There are many
 *  nodes with few edges, and only few nodes with many edges.
 *  The algorithm uses the preferential attachment property.  
 * 
 * References: Albert-L‡szl— Barab‡si & RŽka Albert (October 1999). 
 *			  "Emergence of scaling in random networks". 
 *			   Science 286: 509Ð512. doi:10.1126/science.286.5439.509.
 *
 *
 */
public class BarabasiAlbertModel extends RandomNetworkModel {

	/**
	 * The number of initial nodes.
	 */
	private int init_num_nodes;
	
	/**
	 * The number edges to add at each time step.
	 */
	private int edgesToAdd;

	/**
	 * Creates a model for constructing random graphs according to the
	 * Barabasi-Albert model.
	 * 
	 * @param pNumNodes The number of nodes in generated networks
	 * @param pDirected Specifice if the generated networks are directed(true) or undirected(false)
	 * @param pInit The number of nodes in the seed network.
	 * @param pEdgesToAdd The number of edges to add at each time step.
	 */
	public BarabasiAlbertModel(int pNumNodes, boolean pAllowSelfEdge,
			boolean pDirected, int pInit, int pEdgesToAdd) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);
		
		
		init_num_nodes = pInit;
		
		if(init_num_nodes > pNumNodes)
		{
			init_num_nodes = pNumNodes;
		}
		edgesToAdd = pEdgesToAdd;
	}


	/**
	 * Creates a copy of the RandomNetworkModel.  Required to give each thread its own copy of the generator.
	 * @return A copy of the BarabasiAlbertModel
	 */
	public BarabasiAlbertModel copy()
	{
		return new BarabasiAlbertModel(numNodes, allowSelfEdge, directed, init_num_nodes, edgesToAdd);
	}
	
	
	/**
	 * @return Gets the display name for this generator.
	 */
	public String getName()
	{
		return new String("Barabasi-Albert Model");
	}


	/**
	 *  Generate a network according to the BarabasiAlbertModel.
	 *
	 *  An intial complete network is composed of (init_num_nodes) many nodes.  At each time step afterwards
	 *  a single node is added to the network, until (num)nodes) many nodes have been added.  When a node is added,
	 *  it is given (edgeToAdd)-many initial edges, the endpoint of each edge is chosen using preferential attachment.
	 *  The probability of an existing node <i> u </i> being selected is;  degree(<i>u</i>)/ 2*|E|, where |E| is the 
	 *  current number of edges in the network, not what it will be when all edges have been added.  In this way 
	 *  nodes with more edges get more edges and nodes with few edges, remain with low degree.
	 *
	 *
	 *
	 * @return The generated random network. 
	 */
	public RandomNetwork generate() {

		RandomNetwork random_network = new RandomNetwork(directed);

		random_network.setTitle(getName());

		//Get the current time
		long time = System.currentTimeMillis();

		// Create N nodes
		int[] nodes = new int[numNodes];

		//Keep track of the degree of each node
		int degrees[] = new int[numNodes];

		// For each node
		for (int i = 0; i < numNodes; i++) 
		{
			// Save node in array
			nodes[i] = random_network.nodeCreate();
		}

		//set the number of edges to zero
		numEdges = 0;
		
		//Set up the initial  complete seed network
		for (int i = 0; i < init_num_nodes; i++) 
		{
			for (int j = (i + 1); j < init_num_nodes; j++) 
			{
				//Create the new edge
				random_network.edgeCreate(nodes[i],nodes[j],directed);	
				
				//increment the degrees for each nodes
				degrees[i]++;
				degrees[j]++;

				//Increment the edges
				numEdges++;
			}
		}


		//Add each node one at a time
		for (int i = init_num_nodes; i < numNodes; i++) 
		{
		
			int added = 0;
			double degreeIgnore = 0;
			//Add the appropriate number of edges
			for (int m = 0; m < edgesToAdd; m++) 
			{
				//keep a running talley of the probability
				double prob = 0;
				//Choose a random number
				double randNum = random.nextDouble();
				
				
				
				//Try to add this node to every existing node
				for (int j = 0; j < i; j++) 
				{
					 //Check for an existing connection between these two nodes
					 cytoscape.util.intr.IntIterator iter = random_network.edgesConnecting(nodes[i],nodes[j],directed,false,!directed);
				
					if(!iter.hasNext())
					{
						//Increment the talley by the jth node's probability
						prob += (double) ((double) degrees[j])
							/ ((double) (2.0d * numEdges) - degreeIgnore);
					}
					
					//System.out.println(m + "\t"  + j +"\t" + prob + " < " + randNum  + "-" + degreeIgnore );


					//If this pushes us past the the probability
					if (randNum <= prob) 
					{
						// Create and edge between node i and node j
						random_network.edgeCreate(nodes[i],nodes[j],directed);

						degreeIgnore += degrees[j];

						//increment the number of edges
						added++;
						//increment the degrees of each node
						degrees[i]++;
						degrees[j]++;
						

						
						//Stop iterating for this probability, once we have found a single edge
						break;
					}
				}
			}
			numEdges += added;
		}
		
		//return the resulting network
		return random_network;
	}
}
