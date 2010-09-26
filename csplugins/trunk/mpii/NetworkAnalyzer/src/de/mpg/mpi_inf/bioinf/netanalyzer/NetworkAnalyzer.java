package de.mpg.mpi_inf.bioinf.netanalyzer;

import giny.model.Edge;
import giny.model.Node;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkInterpretation;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkStats;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.SumCountPair;

/**
 * Base class for all control classes that perform analysis on a specific network type.
 * 
 * @author Yassen Assenov
 * @author Nadezhda Doncheva
 */
public abstract class NetworkAnalyzer {

	/**
	 * Gets the title of the network being analyzed.
	 * 
	 * @return Title of the network as a <code>String</code> instance; <code>null</code> if the network does
	 *         not have a title.
	 */
	public String getNetworkTitle() {
		return stats.getTitle();
	}

	/**
	 * Gets the statistics of the analyzed network.
	 * <p>
	 * This method should be called only after the call to {@link #computeAll()}. Otherwise the
	 * <code>NetworkStats</code> instance returned will not be populated with data.
	 * </p>
	 * 
	 * @return Computed statistics of the analyzed network, wrapped as a <code>NetworkStats</code> instance.
	 */
	public NetworkStats getStats() {
		return stats;
	}

	/**
	 * Gets the current progress of the analyzer as a number of steps.
	 * 
	 * @return Number of steps completed in the analysis process.
	 */
	public int getCurrentProgress() {
		return progress;
	}

	/**
	 * Gets the maximum progress of the analyzer as a number of steps.
	 * 
	 * @return Total number of steps required for the analyzer to finish.
	 */
	public int getMaxProgress() {
		return stats.getInt("nodeCount") + 1;
	}

	/**
	 * Computes all the network parameters.
	 */
	public abstract void computeAll();

	/**
	 * Cancels the process of network analysis.
	 * <p>
	 * Note that this method does not force the analyzer to cancel immediately; it takes an unspecified period
	 * of time until the analysis thread actually stops.
	 * </p>
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Checks if this analyzer performs analysis on the whole network.
	 * 
	 * @return <code>true</code> if this analyzed calculates topological parameters for all nodes in the
	 *         targeted network; <code>false</code> if topological parameters are calculated for a subset of
	 *         nodes only.
	 */
	public boolean isGlobal() {
		return nodeSet == null;
	}

	/**
	 * Initializes the fields of this class.
	 * 
	 * @param aNetwork
	 *            Network to be analyzed.
	 * @param aNodeSet
	 *            Subset of nodes in <code>aNetwork</code>, for which topological parameters are to be
	 *            calculated. Set this to <code>null</code> if parameters must be calculated for all nodes in
	 *            the network.
	 * @param aInterpr
	 *            Interpretation of network's edges.
	 * @see #network
	 * @see #interpr
	 * @see #stats
	 */
	protected NetworkAnalyzer(CyNetwork aNetwork, Set<Node> aNodeSet, NetworkInterpretation aInterpr) {
		network = aNetwork;
		nodeSet = aNodeSet;
		interpr = aInterpr;
		stats = new NetworkStats(aNetwork, aInterpr.getInterpretSuffix());
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		progress = 0;
	}

	/**
	 * Prepares the network, if necessary, before the analysis starts.
	 * <p>
	 * If stored in the interpretation, this method removes all undirected self-loops from the network.
	 * </p>
	 */
	protected void analysisStarting() {
		if (interpr.isIgnoreUSL()) {
			removedEdges = new HashSet<Edge>();
			// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
			Iterator<?> edgesIter = network.edgesIterator();
			while (edgesIter.hasNext()) {
				Edge edge = (Edge) edgesIter.next();
				if (!edge.isDirected()) {
					int ei = edge.getRootGraphIndex();
					if (network.getEdgeSourceIndex(ei) == network.getEdgeTargetIndex(ei)) {
						network.removeEdge(ei, false);
						removedEdges.add(edge);
					}
				}
			}
			// Update edge count
			stats.set("edgeCount", new Integer(network.getEdgeCount()));
		}
	}

	/**
	 * Returns the network to initial state, if necessary, after the analysis has finished.
	 * <p>
	 * If previously removed, this method puts back all undirected self-loops in the network.
	 * </p>
	 */
	protected void analysisFinished() {
		if (interpr.isIgnoreUSL()) {
			for (final Edge e : removedEdges) {
				network.addEdge(e);
			}
		}
	}

	/**
	 * Adds a value in a mapping of integers and <code>SummCountPair</code>s.
	 * <p>
	 * This method accumulates the given value to the sequence statistics (<code>SummCountPair</code>
	 * instance) mapped to the specified key. If the mapping does not contain such a key, a new (key, value)
	 * pair is created, where the value is a new <code>SummCountPair</code> instance with the given value
	 * added.
	 * </p>
	 * 
	 * @param aMapping
	 *            Mapping between integers and sequence statistics.
	 * @param aKey
	 *            Key to which the value is to ba added.
	 * @param aValue
	 *            Value to be added.
	 */
	protected void accumulate(Map<Integer, SumCountPair> aMapping, int aKey, double aValue) {
		accumulate(aMapping, new Integer(aKey), aValue);
	}

	/**
	 * Adds a value in a mapping of integers and <code>SummCountPair</code>s.
	 * <p>
	 * This method accumulates the given value to the sequence statistics (<code>SummCountPair</code>
	 * instance) mapped to the specified key. If the mapping does not contain such a key, a new (key, value)
	 * pair is created, where the value is a new <code>SummCountPair</code> instance with the given value
	 * added.
	 * </p>
	 * 
	 * @param aMapping
	 *            Mapping between integers and sequence statistics.
	 * @param aKey
	 *            Key to which the value is to ba added.
	 * @param aValue
	 *            Value to be added.
	 */
	protected void accumulate(Map<Integer, SumCountPair> aMapping, Integer aKey, double aValue) {
		final SumCountPair sequenceStat = aMapping.get(aKey);
		if (sequenceStat != null) {
			sequenceStat.add(aValue);
		} else {
			aMapping.put(aKey, new SumCountPair(aValue));
		}
	}

	/**
	 * Fills the values for the average clustering coefficients given their sums.
	 * 
	 * @param aCCps
	 *            Map of clustering coefficients. The keys of the map must be node connectivities, and the
	 *            values - their sums, encapsulated in <code>SumCountPair</code> instances.
	 * @param aAverages
	 *            Array of points to be filled with the average clustering coefficient for every connectivity.
	 * @return Sum of all clustering coefficients in the given hashmap.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the size of the <code>aAverages</code> array is less than the number of entries in
	 *             <code>aCCps</code>.
	 * @throws NullPointerException
	 *             If <code>aCCps</code> is <code>null</code>; or if <code>aCCps</code> is non-empty and
	 *             <code>aAverages</code> is <code>null</code>.
	 */
	protected double accumulateCCs(Map<Integer, SumCountPair> aCCps, Point2D.Double[] aAverages) {
		double total = 0;
		Set<Integer> neighborCounts = aCCps.keySet();
		int i = 0;
		for (Integer nc : neighborCounts) {
			SumCountPair coefs = aCCps.get(nc);
			total += coefs.getSum();
			aAverages[i++] = new Point2D.Double(nc.doubleValue(), coefs.getAverage());
		}
		return total;
	}

	/**
	 * Gets the averages of the accumulated values and stores them in a set.
	 * <p>
	 * This method is used for computing the average neighborhood connectivity. In this case, the keys of the
	 * map are node connectivities and the values - the accumulated connectivities of the neighbors of a node
	 * with k links.
	 * </p>
	 * 
	 * @param pAccumulatedValues
	 *            Mapping of integers and accumulated values.
	 * @return Set of points that stores the averages of the accumulated values in the mapping. The
	 *         <code>x</code> coordinate of each point is a key in the mapping and the <code>y</code>
	 *         coordinate - the average of the accumulated numbers in the corresponding value of the map.
	 */
	protected static Set<Point2D.Double> getAverages(Map<Integer, SumCountPair> pAccumulatedValues) {
		Set<Point2D.Double> averages = new HashSet<Point2D.Double>(pAccumulatedValues.size());
		for (Integer x : pAccumulatedValues.keySet()) {
			final double y = pAccumulatedValues.get(x).getAverage();
			averages.add(new Point2D.Double(x.doubleValue(), y));
		}
		return averages;
	}

	/**
	 * Sets the boolean value of the given node attribute.
	 * 
	 * @param aNodeID
	 *            ID of node to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setAttr(String aNodeID, String aAttributeID, boolean aValue) {
		nodeAttributes.setAttribute(aNodeID, Messages.getAttr(aAttributeID), new Boolean(aValue));
	}

	/**
	 * Sets the floating-point value of the given node attribute.
	 * 
	 * @param aNodeID
	 *            ID of node to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setAttr(String aNodeID, String aAttributeID, double aValue) {
		nodeAttributes.setAttribute(aNodeID, Messages.getAttr(aAttributeID), new Double(aValue));
	}

	/**
	 * Sets the floating-point (<code>Double</code>) value of the given node attribute.
	 * 
	 * @param aNodeID
	 *            ID of node to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setAttr(String aNodeID, String aAttributeID, Double aValue) {
		nodeAttributes.setAttribute(aNodeID, Messages.getAttr(aAttributeID), aValue);
	}

	/**
	 * Sets the integer value of the given node attribute.
	 * 
	 * @param aNodeID
	 *            ID of node to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setAttr(String aNodeID, String aAttributeID, int aValue) {
		nodeAttributes.setAttribute(aNodeID, Messages.getAttr(aAttributeID), new Integer(aValue));
	}

	/**
	 * Sets the boolean value of the given edge attribute.
	 * 
	 * @param anEdgeID
	 *            ID of edge to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setEAttr(String anEdgeID, String aAttributeID, boolean aValue) {
		edgeAttributes.setAttribute(anEdgeID, Messages.getAttr(aAttributeID), new Boolean(aValue));
	}

	/**
	 * Sets the floating-point value of the given edge attribute.
	 * 
	 * @param anEdgeID
	 *            ID of edge to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setEAttr(String anEdgeID, String aAttributeID, double aValue) {
		edgeAttributes.setAttribute(anEdgeID, Messages.getAttr(aAttributeID), new Double(aValue));
	}

	/**
	 * Sets the floating-point (<code>Double</code>) value of the given edge attribute.
	 * 
	 * @param anEdgeID
	 *            ID of edge to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setEAttr(String anEdgeID, String aAttributeID, Double aValue) {
		edgeAttributes.setAttribute(anEdgeID, Messages.getAttr(aAttributeID), aValue);
	}

	/**
	 * Sets the integer value of the given edge attribute.
	 * 
	 * @param anEdgeID
	 *            ID of edge to have its attribute set.
	 * @param aAttributeID
	 *            ID of attribute name. The attribute name itself is obtained by calling
	 *            {@link Messages#getAttr(String)}.
	 * @param aValue
	 *            Value of the attribute.
	 */
	protected void setEAttr(String anEdgeID, String aAttributeID, int aValue) {
		edgeAttributes.setAttribute(anEdgeID, Messages.getAttr(aAttributeID), new Integer(aValue));
	}

	/**
	 * Target network for analysis.
	 */
	protected CyNetwork network;

	/**
	 * Subset of nodes to be analyzed.
	 */
	protected Set<Node> nodeSet;

	/**
	 * Interpretation of edges in {@link #network}.
	 */
	protected NetworkInterpretation interpr;

	/**
	 * Statistics computed over the network.
	 */
	protected NetworkStats stats;

	/**
	 * Current progress of the analysis.
	 * <p>
	 * The progress of the analyzer is measured in number of steps. Extender classes are responsible for
	 * maintaining the value of this field up to date. The progress must be a natural number not greater than
	 * the maximal progress.
	 * </p>
	 * 
	 * @see #getMaxProgress()
	 */
	protected int progress;

	/**
	 * Flag indicating if the process of analysis was cancelled by the user.
	 * <p>
	 * This flag should only be modified by calling {@link #cancel()}. Extender classes should terminate the
	 * analysis once this flag is set to <code>true</code>. Therefore, in the process of network analysis, the
	 * value of this flag should be checked at regular intervals.
	 * </p>
	 */
	protected boolean cancelled;

	/**
	 * Global <code>HashMap</code> for storing node attributes.
	 */
	protected CyAttributes nodeAttributes;

	/**
	 * Global <code>HashMap</code> for storing edge attributes.
	 */
	protected CyAttributes edgeAttributes;

	/**
	 * Set of all edges that are removed from {@link #network} before the analysis. These edges are added back
	 * to the network after the analysis completes or is cancelled.
	 */
	private Set<Edge> removedEdges;
}
