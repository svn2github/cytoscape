/*
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
import cytoscape.view.*;
import giny.view.*;
import cytoscape.data.*;
import java.util.*;

public class WattsStrogatzModel extends RandomNetworkModel {

	private double beta;

	private double degree;

	/**
	 * Creates a model for constructing random graphs according to the
	 * watts-strogatz model.
	 * 
	 * @param pNumNodes
	 *            <int> : # of nodes in Network
	 * @param pDirected
	 *            <boolean> : Network is directed(TRUE) or undirected(FALSE)
	 * @param pProbability
	 *            <double> : probability of an edge
	 * 
	 */
	public WattsStrogatzModel(int pNumNodes, boolean pAllowSelfEdge,
			boolean pDirected, double pBeta, double pDegree) {
		super(pNumNodes, UNSPECIFIED, pAllowSelfEdge, pDirected);

		// TODO: Is it common practice to throw exceptions in these cases?
		// For now just force to valid range

		degree = pDegree;
		beta = pBeta;
	}

	/*
	 * Generates a random graph based on the model specified by the constructor:
	 * G(n,m) or G(n,p)
	 */
	public CyNetwork Generate() {

		CyNetwork random_network = Cytoscape
				.createNetwork("Watts-Strogatz Network");
		//CyNetworkView view = Cytoscape.createNetworkView(random_network);

		numEdges = 0;

		long time = System.currentTimeMillis();

		// Create N nodes
		CyNode[] nodes = new CyNode[numNodes];

		// For each edge
		for (int i = 0; i < numNodes; i++) {
			// Create a new node nodeID = i, create = true
			CyNode node = Cytoscape.getCyNode(time + "(" + i + ")", true);

			// Add this node to the network
			random_network.addNode(node);

			// Save node in array
			nodes[i] = node;


			/*
			// Create a Node view
			NodeView nv = view.addNodeView(node.getRootGraphIndex());

			double x_pos = random.nextDouble();
			double y_pos = random.nextDouble();
			nv.setXPosition(x_pos * 200.0d);
			nv.setYPosition(y_pos * 200.0d);
			*/
		}

		LinkedList edges = new LinkedList();
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				int value = i - j;
				if (value < 0) {
					value = (value + numNodes) % numNodes;
				}

				if ((i != j) && (value <= degree)) {
					int index = i * numNodes + j;
					edges.add(new Integer(index));
					numEdges++;
				}
			}
		}

		while (edges.size() != 0) {
			int e = ((Integer) edges.remove()).intValue();

			int source = e / numNodes;
			int target = e % numNodes;

			double percent = random.nextDouble();
			if (percent < beta) {
				int k = Math.abs(random.nextInt() % numNodes);
				while (source == k) // || (connect[i][k] == 1))
				{
					k = Math.abs(random.nextInt() % numNodes);
				}
				target = k;

			}

			CyEdge edge = Cytoscape.getCyEdge(nodes[source], nodes[target],
					Semantics.INTERACTION, new String("("
							+ Math.min(source, target) + ","
							+ Math.max(source, target) + ")"), true, directed);
			random_network.addEdge(edge);
		}

		/*
		 * for(int i = 0; i < nuN; i++) { for(int j = i + 1; j < N; j++) {
		 * if(connect[i][j] == 1) { double percent = rand.nextDouble();
		 * if(percent < Beta) { int k = Math.abs(random.nextInt() % N); while((i ==
		 * k) || (connect[i][k] == 1)) { k = Math.abs(rand.nextInt() % N); }
		 * 
		 * connect[i][k] = 1; connect[k][i] = 1; /* k = Math.abs(rand.nextInt() %
		 * N); while((j == k) || (connect[j][k] == 1)) { k =
		 * Math.abs(rand.nextInt() % N); }
		 * 
		 * connect[j][k] = 1; connect[k][j] = 1;
		 * 
		 * connect[i][j] = 0; connect[j][i] = 0; } } }
		 *  }
		 */
		 
		 return random_network;
	}

	public void Compare() {

	}

}
