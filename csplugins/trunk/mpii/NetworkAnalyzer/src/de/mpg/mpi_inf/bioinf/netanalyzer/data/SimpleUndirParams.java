package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Storage class for some of the simple network parameters of an undirected network.
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public class SimpleUndirParams {

	/**
	 * Initializes a new instance of <code>SimpleUndirParams</code>.
	 */
	public SimpleUndirParams() {
		connectedComponentCount = 0;
		diameter = 0;
		radius = Integer.MAX_VALUE;
		unconnectedNodeCount = 0;
	}

	/**
	 * Accumulates the connectivities of all nodes. These values are used in the calculation of
	 * network density and centralization.
	 */
	public SumCountPair connectivityAccum;

	/**
	 * Accumulates the squared connectivities of all nodes. These values are used in the calculation
	 * of network heterogeneity.
	 */
	public SumCountPair sqConnectivityAccum;

	/**
	 * Number of connected components.
	 */
	public int connectedComponentCount;

	/**
	 * Length of diameter.
	 */
	public int diameter;

	/**
	 * Length of radius.
	 */
	public int radius;

	/**
	 * Number of nodes that are not connected to any other nodes.
	 */
	public int unconnectedNodeCount;

	/**
	 * Number of self-loops (edges that connect a node with itself).
	 */
	public int selfLoopCount;

	/**
	 * Overall number of partners of multi-edged node pairs. A partner is one member of such a pair.
	 * <code>multiEdgePartners / 2</code> is the number of multi-edged node pairs.
	 */
	public int multiEdgePartners;
}
