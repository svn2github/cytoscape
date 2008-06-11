/* File: RandomNetworkModel.java
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


/*
 File: RandomNetworkPlugin
 Author: Patrick J. McSweeney
 Creation Date: 5/07/08
 */
package cytoscape.randomnetwork;

import cytoscape.*;
import java.util.Random;


/* The base class for all random network 
 * models
 */
public abstract class RandomNetworkModel {

	//The number of nodes in network
	protected int numNodes;

	//The number of edges in the network
	protected int numEdges;

	//Whether the network is directed or not
	protected boolean directed;
	
	//Whether to allow reflexive edges
	protected boolean allowSelfEdge;

	//Experimental seed
	protected long seed;

	//Random number generated
	protected Random random;

	//A Flag to represent that a value is not set
	protected static int UNSPECIFIED = -1;


	/*
	 * Constructor
	 */
	RandomNetworkModel(int pNumNodes, int pNumEdges, boolean pAllowSelfEdge,
			boolean pDirected) {
		numNodes = pNumNodes;
		numEdges = pNumEdges;
		directed = pDirected;
		allowSelfEdge = pAllowSelfEdge;
		seed = UNSPECIFIED;
		random = new Random();
	}

	/*
	 * @param pSeed the seed to set for the random # generator 
	 */
	public void setSeed(long pSeed) {
		seed = pSeed;
		random = new Random(seed);
	}

	public long getSeed() {
		return seed;
	}

	public int getNumNodes() {
		return numNodes;
	}

	public int getNumEdges() {
		return numEdges;
	}

	public boolean getDirected() {
		return directed;
	}

	public abstract CyNetwork Generate();

	public abstract void Compare();
}
