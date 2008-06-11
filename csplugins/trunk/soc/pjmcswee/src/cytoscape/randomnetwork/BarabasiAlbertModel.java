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

import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import giny.view.*;


/*
 * Barabasi-Albert Model x
 *
 *
 */

public class BarabasiAlbertModel extends RandomNetworkModel {

	//The number of initial nodes 
	private int init_num_nodes;
	
	//The number edges to add at each time step
	private int edgesToAdd;

	/**
	 * Creates a model for constructing random graphs according to the
	 * Barabasi-Albert model.
	 * 
	 * @param pNumNodes
	 *            <int> : # of nodes in Network
	 * @param pDirected
	 *            <boolean> : Network is directed(TRUE) or undirected(FALSE)
	 * @param pInit
	 *			   <int> : number of nodes in the seed network
	 * @param pEdgesToAdd: 
	 *			   <int> : number of edges to add at each time step
	 */
	public BarabasiAlbertModel(int pNumNodes, boolean pAllowSelfEdge,
			boolean pDirected, int pInit, int pEdgesToAdd) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);
		init_num_nodes = pInit;
		edgesToAdd = pEdgesToAdd;
	}

	/*
	 *  Generate a network according to the model
	 * 
	 */
	public CyNetwork Generate() {

		CyNetwork random_network = Cytoscape
				.createNetwork("Barabasi-Albert Network");

		//Get the current time
		long time = System.currentTimeMillis();

		// Create N nodes
		CyNode[] nodes = new CyNode[numNodes];

		//Keep track of the degree of each node
		int degrees[] = new int[numNodes];

		// For each node
		for (int i = 0; i < numNodes; i++) {
			// Create a new node nodeID = i, create = true
			CyNode node = Cytoscape.getCyNode(time + "(" + i + ")", true);

			// Add this node to the network
			random_network.addNode(node);

			// Save node in array
			nodes[i] = node;
		}

		//set the number of edges to zero
		numEdges = 0;
		
		//Set up the initial network
		for (int i = 0; i < init_num_nodes; i++) {

			for (int j = (i + 1); j < init_num_nodes; j++) {

				if (random.nextDouble() <= 1) {
					// Create and edge between node i and node j
					CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j],
							Semantics.INTERACTION, new String("("
									+ Math.min(i, j) + "," + Math.max(i, j)
									+ ")"), true, directed);

					// Add this edge to the network
					random_network.addEdge(edge);
					
					//increment the degrees for each nodes
					degrees[i]++;
					degrees[j]++;

					//Increment the edges
					numEdges++;
				}

			}
		}


		//Add each node once
		for (int i = init_num_nodes; i < numNodes; i++) {

			//For each edge to add
			for (int m = 0; m < edgesToAdd; m++) {
			
				//keep a running talley of the probability
				double prob = 0;
				//Choose a random number
				double randNum = random.nextDouble();
				
				//Try to add this node to every existing node
				for (int j = 0; j < i; j++) {
				
					//Increment the talley by the jth node's probability
					prob += (double) ((double) degrees[j])
							/ ((double) (2.0d * (double) numEdges));

					//If this pushes us past the the probability
					if (randNum <= prob) {
					
						//Store the pointer for the edge
						CyEdge edge = null;

						if (!directed) {
							// Create and edge between node i and node j
							// that is undirected
							edge = Cytoscape.getCyEdge(nodes[i], nodes[j],
									Semantics.INTERACTION, new String("("
											+ Math.min(i, j) + ","
											+ Math.max(i, j) + ")"), true,
									directed);
						} else {
							//If the network is directed, randomly pick
							//a direction.
						
							if (random.nextDouble() < .5) {
								// Create and edge between node i and node j
								edge = Cytoscape.getCyEdge(nodes[i], nodes[j],
										Semantics.INTERACTION, new String("("
												+ Math.min(i, j) + ","
												+ Math.max(i, j) + ")"), true,
										directed);
							} else {
								// Create and edge between node i and node j
								edge = Cytoscape.getCyEdge(nodes[j], nodes[i],
										Semantics.INTERACTION, new String("("
												+ Math.min(i, j) + ","
												+ Math.max(i, j) + ")"), true,
										directed);
							}
						}

						// Add this edge to the network
						random_network.addEdge(edge);

						//increment the number of edges
						numEdges++;
						
						//increment the degrees of each node
						degrees[i]++;
						degrees[j]++;
						
						//Stop iterating 
						break;
					}
				}
			}
		}

		

	  return random_network;

	}

	/*
	 *
	 */
	public void Compare() {
		//Do nothing for now
	}

}
