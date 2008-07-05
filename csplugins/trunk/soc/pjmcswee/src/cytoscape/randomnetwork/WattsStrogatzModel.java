/* File: WattsStrogatzModel.java
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
import cytoscape.graph.dynamic.util.*;
import cytoscape.graph.dynamic.*;

import java.util.*;


/**
*	This model is used to create random networks with the 
*	small world property.  
*
*
*  References: Watts, D.J.; Strogatz, S.H. (1998). 
*			   "Collective dynamics of 'small-world' networks.". 
*			   Nature 393 (6684): 409Ð10. doi:10.1038/30918.
*/

public class WattsStrogatzModel extends RandomNetworkModel {

	//Used to linearly interpolate between lattice and erdos-renyi graph
	private double beta;

	//The number of edges to add 
	private double degree;

	/**
	 * Creates a model for constructing random graphs according to the
	 * watts-strogatz model.
	 * 
	 * @param pNumNodes
	 *            <int> : # of nodes in Network
	 * @param pDirected
	 *            <boolean> : Network is directed(TRUE) or undirected(FALSE)
	 * @param pBeta:
	 *            <double> : interpolates between erdos-renyi graph and 
	 *						 lattice
	 * 
	 */
	public WattsStrogatzModel(int pNumNodes, boolean pAllowSelfEdge,
			boolean pDirected, double pBeta, double pDegree) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);


		degree = pDegree;
		beta = pBeta;
	}

	/*
	 * Generates the random graph
	 */
	public DynamicGraph generate() 
	{

		//Create the random graph
		DynamicGraph random_network =  DynamicGraphFactory.instantiateDynamicGraph();

		//Keep track of the number 
		numEdges = 0;

		// Create N nodes
		int[] nodes = new int[numNodes];

		// For each edge
		for (int i = 0; i < numNodes; i++) {
			// Save node in array
			nodes[i] = random_network.nodeCreate();
		}
		
		//Create a linked list to store edges
		//as we create edges and then change them 
		//with probability beta
		LinkedList edges = new LinkedList();
		
		//for all pairs of nodes
		for (int i = 0; i < numNodes; i++) 
		{
			for (int j = i + 1; j < numNodes; j++) 
			{
				//get the lattice difference
				int value = i - j;
				if (value < 0) 
				{
					value = (value + numNodes) % numNodes;
				}
				
				//no relfexive edges here
				if ((i != j) && (value <= degree)) 
				{
					//Create a single number which represents this edge
					int index = i * numNodes + j;

					//store this edge
					edges.add(new Integer(index));

					//increment our count of edges
					numEdges++;
				}
			}
		}

		//Iterate through all of our edges
		while (edges.size() != 0) 
		{
			
			//Get the edge index
			int e = ((Integer) edges.remove()).intValue();

			//Extract the source and target from the edge
			int source = e / numNodes;
			int target = e % numNodes;

			//Throw a random dart
			double percent = random.nextDouble();

			//If the dart lands in beta
			if (percent <= beta) 
			{
				//Choose a new node 
				int k = Math.abs(random.nextInt() % numNodes);
				while (source == k) 
				{
					k = Math.abs(random.nextInt() % numNodes);
				}
				target = k;

			}

			random_network.edgeCreate(nodes[source],nodes[target],directed);
		}

				 
		 return random_network;
	}


}
