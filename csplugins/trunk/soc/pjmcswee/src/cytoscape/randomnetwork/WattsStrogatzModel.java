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
 *	small world property.  A regular lattics is formed, 
 *  and then randomly perturbed according to the variable beta.
 *  Creates a linear interpolation between an erdos-renyi graph and
 *  a regular lattics.
 *
 *
 *  References: Watts, D.J.; Strogatz, S.H. (1998). 
 *			   "Collective dynamics of 'small-world' networks.". 
 *			    Nature 393 (6684): 409Ð10. doi:10.1038/30918.
 */
public class WattsStrogatzModel extends RandomNetworkModel {

	/**
	 * Used to linearly interpolate between lattice and erdos-renyi graph
	 */
	private double beta;

	/**
	 * The number of edges to add 
	 */
	private double degree;

	/**
	 * Creates a model for constructing random graphs according to the
	 * watts-strogatz model.
	 * 
	 * @param pNumNodes The number of nodes in generated networks.
	 * @param pDirected Specifices if generated networks are directed(true) or undirected(false)
	 * @param pBeta  Interpolates between erdos-renyi graph and a regular lattice.
	 * 
	 */
	public WattsStrogatzModel(int pNumNodes, boolean pAllowSelfEdge,
			boolean pDirected, double pBeta, double pDegree) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);
		degree = pDegree;
		beta = pBeta;
	}
	
	/**
	 * Creats a copy of the RandomNetworkGenerator.  Used to give each thread their own
	 * copy of the generator.
	 *
	 * @return Returns a copy of this generator
	 */
	public WattsStrogatzModel copy()
	{
		return new WattsStrogatzModel( numNodes, allowSelfEdge, directed, beta, degree);
	}

	/**
	 *  Generates random networks according to the Watts-Strogatz Model. 
	 * <br>
	 *  The algorithm works in two phases:<br>
	 *  (1) Create a regular lattice, where each node is connected to its (degree)-many nearest neighbors.
	 *  (2) Perturb each edge created in step (1) with probability Beta.  Notices that we do not add any edges in this step.
	 * 
	 * @return The generated random network
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
			
			int start = 0;
			if(!directed)
			{
				start = i + 1;
			}
			
			//For every other node
			for (int j = 0; j < numNodes; j++) 
			{
				
				//get the lattice difference
				int value = Math.abs(i - j);
				
				//If we are within range with wrapping
				if((i < degree) && (j > numNodes - degree))
				{
					value = 0;
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

			//If the dart lands in beta, then shuffle this edge
			if (percent <= beta) 
			{
				//Choose a new node 
				int k = Math.abs(random.nextInt() % numNodes);

				//Do not choose the same node
				while (source == k) 
				{
					k = Math.abs(random.nextInt() % numNodes);
				}
				
				//save the target k
				target = k;
			}
			//DANGER WILL ROBINSON:  This may stomp out existing edges... we should check to see if it already exists
			//create this edge
			random_network.edgeCreate(nodes[source],nodes[target],directed);
		}

				 
		 return random_network;
	}


}
