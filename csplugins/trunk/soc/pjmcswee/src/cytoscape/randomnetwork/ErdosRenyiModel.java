 /* File: ErdosRenyiModel.java
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
 * This class generates random networks according to the two Erdos-Renyi models.
 * G(n,m) which generates nodes with a specific number of edges <br>
 * G(n,p) which generates a random network where each edge has the probability p of existing<br>
 *
 * References: <br>
 * Erdos, P.; Renyi A. (1959). "On Random Graphs. I.".  Publications Matheaticae 6: 290-297.<br>
 * Erdos, P.; Renyi A. (1960). "The Evolution of Random Graphs".  Magyar Tud. Akad. Math. Kutato INt. Koxl. 5:17-61.<br>
 * Gilber, E.N. (1959). "Random Graphs".  Annals of Mathematical Statistics 30: 1141 - 1144.<br>
 *
 * @author Patrick J. McSweeney
 * @version 1.0
 */
public class ErdosRenyiModel extends RandomNetworkModel {
	private double probability;

	/**
	 * Creates a model for constructing random graphs according to the
	 * erdos-renyi model. This constructor will create random graphs with a
	 * given number of edges. Each call to generate will create networks with
	 * the specified number of edges. G(n,m) model.
	 * 
	 * @param pNumNodes  The number of nodes in generated networks.
	 * @param pNumEdges  The number of edges in generated networks.
	 * @param pDirected  Generated networs are directed(TRUE) or undirected(FALSE).
	 * 
	 */
	public ErdosRenyiModel(int pNumNodes, int pNumEdges,
			boolean pAllowSelfEdge, boolean pDirected) {
		super(pNumNodes, pNumEdges, pAllowSelfEdge, pDirected);
		probability = UNSPECIFIED;
	}


	/**
	 * Creates a model for constructing random graphs according to the
	 * erdos-renyi model. This constructor will create random graphs with a
	 * given probability. So that each call to generate can create networks with
	 * a different number of edges. G(n,p)
	 * 
	 * @param pNumNodes The number of nodes in generated networks
	 * @param pDirected Generated networks are directed(TRUE) or undirected(FALSE)
	 * @param pProbability The probability of a single edge edge.  Probability should be a valid probability in the range [0: 1].
	 * 
	 */
	public ErdosRenyiModel(int pNumNodes,double pProbability,
	 boolean pAllowSelfEdge, boolean pDirected ) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);

		// TODO: Is it common practice to throw exceptions in these cases?
		// For now just force to valid range
		if (probability < 0d) {
			probability = 0d;
		}
		if (probability > 1.0d) {
			probability = 1.0d;
		}
		probability = pProbability;
	}
	
	
	/**
	 *  Copies the RandomNetworkGenerator.
	 *  This is function is necessary to give each thread its own instance of the RandomNetworkGenerator.
	 *
	 * @return The copy of the calling ErdosRenyiModel
	 */
	public RandomNetworkGenerator copy()
	{
		//If no probability
		if(probability == UNSPECIFIED)
		{
			return new ErdosRenyiModel( numNodes, numEdges, allowSelfEdge, directed);
		}
		else
		{
			return new ErdosRenyiModel( numNodes,probability, allowSelfEdge, directed);		
		}
	}


	/**
	 * @return Gets the display name for this generator.
	 */
	public String getName()
	{
		return new String("Erdos-Renyi Model");
	}

	

	/**
	 * Generates a random graph based on the model specified by the constructor:
	 * G(n,m) or G(n,p)
	 *
	 *
	 * @return The generated network, whose properties are specified by member variables.
	 */
	public RandomNetwork generate() {
		
		//Call the appropriate generator
		RandomNetwork random_network = null;
		if(probability == UNSPECIFIED)
		{
			random_network = gnmModel();
		}
		else
		{
			random_network = gnpModel();
		}
	
		//Return this network
		return random_network;
	}

	/**
	 *  Create a random network according to the G(n,m) model.
	 *  <br>
	 *  In this model each network with n nodes and m edges has
	 *  equal chance of being created.  
	 *
	 * @return The generated network according to the G(n,m) model.
	 */
	private RandomNetwork gnmModel()
	{
		//Create the graph object
		RandomNetwork random_network =  new RandomNetwork(directed);

		random_network.setTitle(getName());
		
		// Create N nodes
		int[] nodes = new int[numNodes];

		//create a list of existing edges
		LinkedList<Integer> existingEdges  = new LinkedList<Integer>();

		
		// For each edge
		for (int i = 0; i < numNodes; i++) 
		{
			// Create a new node nodeID = i, create = true
			nodes[i] = random_network.nodeCreate();
		}

		
		//Here we are ensuring that m is less then the maximum number of
		//edges given the network properitites: number of nodes, direcetedness,
		//and reflexive edges.
		
		//If directed
		if (directed) {
			//If reflexive edges
			if (allowSelfEdge) {
				numEdges = Math.min(numEdges, numNodes * numNodes);
			} else {
			//if reflexive edges are not allowed
				numEdges = Math.min(numEdges, numNodes * (numNodes - 1));
			}
		}
		//else we are undirected
		else {
			//If reflexive edges				
			if (allowSelfEdge) {
				numEdges = Math.min(numEdges,
						(int) ((numNodes * (numNodes - 1)) / 2.0)
								+ numNodes);
			} else {
			//if reflexive edges are not allowed
				numEdges = Math.min(numEdges,
						(int) ((numNodes * (numNodes - 1)) / 2.0));
			}
		}

		//Create each edge
		for (int i = 0; i < numEdges; i++) {

			// Select two nodes (source and target only apply if directed)
			int source = Math.abs(random.nextInt()) % numNodes;
			int target = Math.abs(random.nextInt()) % numNodes;

			
			boolean check =   existingEdges.contains(source*numNodes + target); 

						
			//We can enumerate all pairs of nodes by the formula
			//source * N + target, where source and target
			//refer to specific nodes between 0 and N - 1
			int higher = source * numNodes + target + 1;
			int lower = source * numNodes + target - 1;
			
			
			//The idea here is that if the source and target we 
			//initially chose has already been created, then create
			//the next closest edge according to our enumeration.
			//Randomly selecting a new edge is computationally 
			//prohibitive when the number of edges approaches the maximum
			while ((check)
					|| ((!allowSelfEdge) && (source == target))) {
					
				//Check to make sure that lower is 
				//within bounds	
				if (lower < 0) {
					lower = (numNodes * numNodes - 1);
				}
				//Chck to make sure that higher is within bounds
				if (higher == numNodes * numNodes) {
					higher = 0;
				}

				//Get the source and target from the lower number
				int source_lo = lower / numNodes;
				int target_lo = lower % numNodes;

				//Get the source and target from the higher number
				int source_hi = higher / numNodes;
				int target_hi = higher % numNodes;

				//Either this is a reflexive edge and they are allowed,
				//or it is not a reflexive edge
				if (((allowSelfEdge) && (source_lo == target_lo))
						|| (source_lo != target_lo)) {
				
					//Try to get this edge
					check = existingEdges.contains(source_lo*numNodes + target_lo); 
					//adjacency[source_lo][target_lo];
					
					//If this edge does not exist, choose this edge
					if (!check) {
						source = source_lo;
						target = target_lo;
						break;
					}
				}
			
				//Either this is a reflexive edge and they are allowed,
				//or it is not a reflexive edge
				if (((allowSelfEdge) && (source_hi == target_hi))
						|| (source_hi != target_hi)) {
			
					//try to get the higher edge
					check = existingEdges.contains(source_hi * numNodes + target_hi); //adjacency[source_hi][target_hi];

					//If the edge does not exist choose this edge
					if (!check) {
						source = source_hi;
						target = target_hi;
						break;
					}
				}

				higher++;
				lower--;

			}
			
			existingEdges.add(new Integer(source *numNodes + target));
			if(!directed)
			{
				existingEdges.add(new Integer(target * numNodes + source));
			}

		
			//Create the edge
			random_network.edgeCreate(nodes[source], nodes[target], directed);
			
		}
		existingEdges = null;
		return random_network;
	}
	
	
	/**
	*
	* Create a random network by the G(n,p) model.
	* In this model each edge has indpendent probability of existing: p.
	* Therefore the expected number of edges is: p * n * (n - 1)/2 undirected
	* <br> or  p * n * (n - 1) directed.
	*
	* If self loops are allows the term (n-1) is replaced by n in both of the above formulas.
	*
	*
	* @return The network generated using the G(n,p) model.
	*/
	public RandomNetwork gnpModel()
	{
		//Create a network
		RandomNetwork random_network =  new RandomNetwork(directed);
		random_network.setTitle(getName());
		
		int[] nodes = new int[numNodes];
		
		
		// For each edge
		for (int i = 0; i < numNodes; i++) 
		{
			// Create a new node nodeID = i, create = true
			nodes[i] = random_network.nodeCreate();
		
		}

		// For each node
		for (int i = 0; i < numNodes; i++) {
		
			//start defines valid targets for the source node i
			int start = 0;
			if (!directed) {
				start = i + 1;
				if (allowSelfEdge) {
					start = i;
				}
			}

			// For every other node
			for (int j = start; j < numNodes; j++) {

				//If this i,j represents a reflexive edge, and we
				//do not allow reflexive edges, ignore it.
				if ((!allowSelfEdge) && (i == j)) {
					continue;
				}

				// If random indicates this edge exists
				if (random.nextDouble() <= probability) {

					// Create and edge between node i and node j
					random_network.edgeCreate(nodes[i],nodes[j],directed);
				}
			}
		}

		return random_network;
	}


}
