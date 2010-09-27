/*
 * Copyright (c) 2006, 2007, 2008, 2010, Max Planck Institute for Informatics, Saarbruecken, Germany.
 *
 * This file is part of NetworkAnalyzer.
 * 
 * NetworkAnalyzer is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * NetworkAnalyzer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with NetworkAnalyzer. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.mpg.mpi_inf.bioinf.netanalyzer;

import giny.model.Edge;
import giny.model.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfo;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.DegreeDistribution;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.LogBinDistribution;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.LongHistogram;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.MutInteger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkInterpretation;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NodeBetweenInfo;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.PathLengthData;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Points2D;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.SimpleUndirParams;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.SumCountPair;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Utils;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.io.SettingsSerializer;

/**
 * Network analyzer for networks that contain undirected edges only.
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 * @author Nadezhda Doncheva
 */
public class UndirNetworkAnalyzer extends NetworkAnalyzer {

	/**
	 * Initializes a new instance of <code>UndirNetworkAnalyzer</code>.
	 * 
	 * @param aNetwork
	 *            Network to be analyzed.
	 * @param aNodeSet
	 *            Subset of nodes in <code>aNetwork</code>, for which topological parameters are to be
	 *            calculated. Set this to <code>null</code> if parameters must be calculated for all nodes in
	 *            the network.
	 * @param aInterpr
	 *            Interpretation of the network edges.
	 * @param aDupEdges
	 *            Flag indicating if the network contains a pair of nodes connected by more than one edge. If
	 *            this parameter is <code>true</code>, node and edge betweenness are not computed.
	 */
	public UndirNetworkAnalyzer(CyNetwork aNetwork, Set<Node> aNodeSet, NetworkInterpretation aInterpr,
			boolean aDupEdges) {
		super(aNetwork, aNodeSet, aInterpr);
		if (nodeSet != null) {
			stats.set("nodeCount", nodeSet.size());
		}
		nodeCount = stats.getInt("nodeCount");
		sPathLengths = new long[nodeCount];
		sharedNeighborsHist = new long[nodeCount];
		visited = new HashSet<Node>(nodeCount);
		useNodeAttributes = SettingsSerializer.getPluginSettings().getUseNodeAttributes();
		useEdgeAttributes = SettingsSerializer.getPluginSettings().getUseEdgeAttributes();
		nodeBetweenness = new HashMap<Node, NodeBetweenInfo>();
		edgeBetweenness = new HashMap<Edge, Double>();
		stress = new HashMap<Node, Long>();
		roundingDigits = 8;
		computeNB = !aDupEdges && (nodeSet == null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.NetworkAnalyzer#computeAll()
	 */
	@Override
	public void computeAll() {

		long time = System.currentTimeMillis();
		analysisStarting();
		int edgeCount = 0;
		SimpleUndirParams params = new SimpleUndirParams();
		int maxConnectivity = 0;
		DegreeDistribution degreeDist = new DegreeDistribution(nodeCount);
		// clustering coefficients
		HashMap<Integer, SumCountPair> CCps = new HashMap<Integer, SumCountPair>();
		// topological coefficients
		ArrayList<Point2D.Double> topCoefs = new ArrayList<Point2D.Double>(nodeCount);
		// closeness centrality
		ArrayList<Point2D.Double> closenessCent = new ArrayList<Point2D.Double>(nodeCount);
		// node betweenness
		ArrayList<Point2D.Double> nodeBetweennessArray = new ArrayList<Point2D.Double>(nodeCount);
		// neighborhood connectivity
		HashMap<Integer, SumCountPair> NCps = new HashMap<Integer, SumCountPair>();
		// average shortest path length
		Map<Node, Double> aplMap = new HashMap<Node, Double>();
		// stress
		LogBinDistribution stressDist = new LogBinDistribution();
		// Compute number of connected components
		ConnComponentAnalyzer cca = new ConnComponentAnalyzer(network);
		Set<CCInfo> components = cca.findComponents();
		params.connectedComponentCount = components.size();

		for (CCInfo aCompInfo : components) {
			// Get nodes of connected component
			Set<Node> connNodes = cca.getNodesOf(aCompInfo);
			// Set<Node> connNodes = new HashSet<Node>(connNodes);
			if (nodeSet != null) {
				connNodes.retainAll(nodeSet);
			}

			if (computeNB) {
				// Initialize the parameters for node and edge betweenness calculation
				nodeBetweenness.clear();
				edgeBetweenness.clear();
				stress.clear();
				aplMap.clear();
				for (Node n : connNodes) {
					nodeBetweenness.put(n, new NodeBetweenInfo(0, -1, 0.0));
					stress.put(n, Long.valueOf(0));
				}
			}

			int componentDiameter = 0;
			for (final Node node : connNodes) {
				++progress;
				final String nodeID = node.getIdentifier();
				final int[] incEdges = getIncidentEdges(node);
				final Map<Node, MutInteger> neighborMap = CyNetworkUtils.getNeighborMap(network, node,
						incEdges);

				// Degree distribution calculation
				final int degree = getDegree(node, incEdges);
				edgeCount += degree;
				degreeDist.addObservation(degree);
				if (useNodeAttributes) {
					setAttr(nodeID, "deg", degree);
				}
				final int neighborCount = calcSimple(nodeID, incEdges, neighborMap, params);
				if (maxConnectivity < neighborCount) {
					maxConnectivity = neighborCount;
				}

				if (neighborCount > 0) {
					final Set<Node> neighbors = neighborMap.keySet();

					// Neighborhood connectivity computation
					int[] neighborInd = CyNetworkUtils.getIndices(neighbors);
					final double neighborConnect = averageNeighbors(neighbors);
					accumulate(NCps, neighborCount, neighborConnect);

					if (neighborCount > 1) {

						// Topological coefficients computation
						double topCoef = computeTC(node, neighbors);
						if (!Double.isNaN(topCoef)) {
							topCoefs.add(new Point2D.Double(neighborCount, topCoef));
						} else {
							topCoef = 0.0;
						}

						// Clustering coefficients computation
						final double nodeCCp = computeCC(neighborInd);
						accumulate(CCps, neighborCount, nodeCCp);
						if (useNodeAttributes) {
							setAttr(nodeID, "cco", Utils.roundTo(nodeCCp, roundingDigits));
							setAttr(nodeID, "tco", Utils.roundTo(topCoef, roundingDigits));
						}

					} else if (useNodeAttributes) {
						setAttr(nodeID, "cco", 0.0);
						setAttr(nodeID, "tco", 0.0);
					}
					setAttr(nodeID, "nco", Utils.roundTo(neighborConnect, roundingDigits));
				} else if (useNodeAttributes) {
					setAttr(nodeID, "nco", 0.0);
					setAttr(nodeID, "cco", 0.0);
					setAttr(nodeID, "tco", 0.0);
				}
				if (cancelled) {
					analysisFinished();
					return;
				}

				// Shortest path lengths computation
				if (nodeSet != null) {
					continue;
				}
				PathLengthData pathLengths = computeSPandSN(node);
				final int eccentricity = pathLengths.getMaxLength();
				if (params.diameter < eccentricity) {
					params.diameter = eccentricity;
				}
				if (0 < eccentricity && eccentricity < params.radius) {
					params.radius = eccentricity;
				}
				if (componentDiameter < eccentricity) {
					componentDiameter = eccentricity;
				}
				final double apl = (pathLengths.getCount() > 0) ? pathLengths.getAverageLength() : 0;
				aplMap.put(node, Double.valueOf(apl));
				final double closeness = (apl > 0.0) ? 1 / apl : 0.0;
				closenessCent.add(new Point2D.Double(neighborCount, closeness));

				// Store max. and avg. shortest path lengths, and closeness in node attributes
				if (useNodeAttributes) {
					setAttr(nodeID, "spl", eccentricity);
					setAttr(nodeID, "apl", Utils.roundTo(apl, roundingDigits));
					setAttr(nodeID, "clc", Utils.roundTo(closeness, roundingDigits));
				}

				// Node and edge betweenness calculation
				if (computeNB) {
					computeNBandEB(node);
					// Reset everything except the betweenness value
					for (final Node n : connNodes) {
						NodeBetweenInfo nodeInfo = nodeBetweenness.get(n);
						nodeInfo.reset();
					}
				}

				if (cancelled) {
					analysisFinished();
					return;
				}
			}

			if (nodeSet == null) {
				final double nNormFactor = computeNormFactor(nodeBetweenness.size());
				for (final Node n : connNodes) {
					String id = n.getIdentifier();
					// Normalize and save node betweenness
					if (computeNB) {
						final NodeBetweenInfo nbi = nodeBetweenness.get(n);
						double nb = nbi.getBetweenness() * nNormFactor;
						if (Double.isNaN(nb)) {
							nb = 0.0;
						}
						final int degree = getDegree(n, getIncidentEdges(n));
						nodeBetweennessArray.add(new Point2D.Double(degree, nb));
						final long nodeStress = stress.get(n).longValue();
						stressDist.addObservation(nodeStress);
						if (useNodeAttributes) {
							setAttr(id, "nbt", Utils.roundTo(nb, roundingDigits));
							setAttr(id, "stress", nodeStress);
						}
					}
					// Compute node radiality
					final double rad = (componentDiameter + 1.0 - aplMap.get(n).doubleValue())
							/ componentDiameter;
					if (useNodeAttributes) {
						setAttr(id, "rad", Utils.roundTo(rad, roundingDigits));
					}
				}

				// Normalize and save edge betweenness
				if (computeNB) {
					final double eNormFactor = computeNormFactor(edgeBetweenness.size());
					for (final Map.Entry<Edge, Double> betEntry : edgeBetweenness.entrySet()) {
						double eb = betEntry.getValue().doubleValue() * eNormFactor;
						if (Double.isNaN(eb)) {
							eb = 0.0;
						}
						if (useEdgeAttributes) {
							final Edge e = betEntry.getKey();
							setEAttr(e.getIdentifier(), "ebt", Utils.roundTo(eb, roundingDigits));
						}
					}
				}
			}
		}
		if (params.connectivityAccum != null) {
			final double meanConnectivity = params.connectivityAccum.getAverage();
			stats.set("avNeighbors", meanConnectivity);
			final double density = meanConnectivity / (nodeCount - 1);
			stats.set("density", meanConnectivity / (nodeCount - 1));
			stats.set("centralization", (nodeCount / ((double) nodeCount - 2))
					* (maxConnectivity / ((double) nodeCount - 1) - density));
			final double nom = params.sqConnectivityAccum.getSum() * nodeCount;
			final double denom = params.connectivityAccum.getSum() * params.connectivityAccum.getSum();
			stats.set("heterogeneity", Math.sqrt(nom / denom - 1));
		}

		// Save degree distribution in the statistics instance
		stats.set("degreeDist", degreeDist.createHistogram());

		// Save C(k) in the statistics instance
		if (CCps.size() > 0) {
			Point2D.Double[] averages = new Point2D.Double[CCps.size()];
			double cc = accumulateCCs(CCps, averages) / nodeCount;
			stats.set("cc", cc);
			if (averages.length > 1) {
				stats.set("cksDist", new Points2D(averages));
			}
		}

		// Save topological coefficients in the statistics instance
		if (topCoefs.size() > 1) {
			stats.set("topCoefs", new Points2D(topCoefs));
		}

		stats.set("ncc", params.connectedComponentCount);
		stats.set("usn", params.unconnectedNodeCount);
		stats.set("nsl", params.selfLoopCount);
		stats.set("mnp", params.multiEdgePartners / 2);
		if (interpr.isPaired()) {
			stats.set("edgeCount", edgeCount / 2);
		}

		if (nodeSet == null) {
			long connPairs = 0; // total number of connected pairs of nodes
			long totalPathLength = 0;
			for (int i = 1; i <= params.diameter; ++i) {
				connPairs += sPathLengths[i];
				totalPathLength += i * sPathLengths[i];
			}
			stats.set("connPairs", connPairs);

			// Save shortest path lengths distribution
			if (params.diameter > 0) {
				stats.set("diameter", params.diameter);
				stats.set("radius", params.radius);
				stats.set("avSpl", (double) totalPathLength / connPairs);
				if (params.diameter > 1) {
					stats.set("splDist", new LongHistogram(sPathLengths, 1, params.diameter));
				}
				int largestCommN = 0;
				for (int i = 1; i < nodeCount; ++i) {
					if (sharedNeighborsHist[i] != 0) {
						sharedNeighborsHist[i] /= 2;
						largestCommN = i;
					}
				}
				// Save common neighbors distribution
				if (largestCommN > 0) {
					stats.set("commNeighbors", new LongHistogram(sharedNeighborsHist, 1, largestCommN));
				}
			}
		}

		// Save closeness centrality in the statistics instance
		if (closenessCent.size() > 1) {
			stats.set("closenessCent", new Points2D(closenessCent));
		}

		// Save node betweenness in the statistics instance
		if (nodeBetweennessArray.size() > 2) {
			stats.set("nodeBetween", new Points2D(nodeBetweennessArray));
		}

		// Save neighborhood connectivity in the statistics instance
		if (NCps.size() > 1) {
			stats.set("neighborConn", new Points2D(getAverages(NCps)));
		}

		// Save stress distribution in the statistics instance
		if (computeNB) {
			stats.set("stressDist", stressDist.createPoints2D());
		}

		analysisFinished();
		time = System.currentTimeMillis() - time;
		stats.set("time", time / 1000.0);
		progress = nodeCount;
		if (useNodeAttributes || useEdgeAttributes) {
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}
	}

	/**
	 * Calculates a set of simple properties of the given node.
	 * 
	 * @param aNodeID
	 *            ID of the node of interest. This parameter is used for storing attribute values.
	 * @param aIncEdges
	 *            Array of the indices of all the neighbors of the node of interest.
	 * @param aNeMap
	 *            Map of neighbors of the node of interest and their frequency.
	 * @param aParams
	 *            Instance to accumulate the computed values.
	 * @return Number of neighbors of the node of interest.
	 */
	private int calcSimple(String aNodeID, int[] aIncEdges, Map<Node, MutInteger> aNeMap,
			SimpleUndirParams aParams) {
		final int neighborCount = aNeMap.size();

		// Avg. number of neighbors, density & centralization calculation
		if (aParams.connectivityAccum != null) {
			aParams.connectivityAccum.add(neighborCount);
		} else {
			aParams.connectivityAccum = new SumCountPair(neighborCount);
		}
		// Heterogeneity calculation
		if (aParams.sqConnectivityAccum != null) {
			aParams.sqConnectivityAccum.add(neighborCount * neighborCount);
		} else {
			aParams.sqConnectivityAccum = new SumCountPair(neighborCount * neighborCount);
		}

		// Number of unconnected nodes calculation
		if (neighborCount == 0) {
			aParams.unconnectedNodeCount++;
		}

		// Number of self-loops and number of directed/undireceted edges
		// calculation
		int selfLoops = 0;
		int dirEdges = 0;
		for (int j = 0; j < aIncEdges.length; j++) {
			Edge e = network.getEdge(aIncEdges[j]);
			if (e.isDirected()) {
				dirEdges++;
			}
			if (e.getSource() == e.getTarget()) {
				selfLoops++;
			}
		}
		aParams.selfLoopCount += selfLoops;
		int undirEdges = aIncEdges.length - dirEdges;

		// Number of multi-edge node partners calculation
		int partnerOfMultiEdgeNodePairs = 0;
		for (final MutInteger freq : aNeMap.values()) {
			if (freq.value > 1) {
				partnerOfMultiEdgeNodePairs++;
			}
		}
		aParams.multiEdgePartners += partnerOfMultiEdgeNodePairs;

		// Storing the values in attributes
		if (useNodeAttributes) {
			setAttr(aNodeID, "slo", selfLoops);
			setAttr(aNodeID, "isn", (neighborCount == 0));
			setAttr(aNodeID, "nue", undirEdges);
			setAttr(aNodeID, "nde", dirEdges);
			setAttr(aNodeID, "pmn", partnerOfMultiEdgeNodePairs);
		}
		return neighborCount;
	}

	/**
	 * Computes the clustering coefficient of a node.
	 * 
	 * @param aNeighborIndices
	 *            Array of the indices of all the neighbors of the node of interest.
	 * @return Clustering coefficient of <code>aNode</code> as a value in the range <code>[0,1]</code>.
	 */
	private double computeCC(int[] aNeighborIndices) {
		int edgeCount = CyNetworkUtils.getPairConnCount(network, aNeighborIndices, true);
		int neighborsCount = aNeighborIndices.length;
		return (double) 2 * edgeCount / (neighborsCount * (neighborsCount - 1));
	}

	/**
	 * Computes the shortest path lengths from the given node to all other nodes in the network. In addition,
	 * this method accumulates values in the {@link #sharedNeighborsHist} histogram.
	 * <p>
	 * This method stores the lengths found in the array {@link #sPathLengths}.<br/>
	 * <code>sPathLengths[i] == 0</code> when i is the index of <code>aNode</code>.<br/>
	 * <code>sPathLengths[i] == Integer.MAX_VALUE</code> when node i and <code>aNode</code> are
	 * disconnected.<br/> <code>sPathLengths[i] == d &gt; 0</code> when every shortest path between node i and
	 * <code>aNode</code> contains <code>d</code> edges.
	 * </p>
	 * <p>
	 * This method uses a breadth-first traversal through the network, starting from the specified node, in
	 * order to find all reachable nodes and accumulate their distances to <code>aNode</code> in
	 * {@link #sPathLengths}.
	 * </p>
	 * 
	 * @param aNode
	 *            Starting node of the shortest paths to be found.
	 * @return Data on the shortest path lengths from the current node to all other reachable nodes in the
	 *         network.
	 */
	private PathLengthData computeSPandSN(Node aNode) {
		visited.clear();
		visited.add(aNode);
		Set<Node> nbs = null;
		LinkedList<Node> reachedNodes = new LinkedList<Node>();
		reachedNodes.add(aNode);
		reachedNodes.add(null);
		int currentDist = 1;
		PathLengthData result = new PathLengthData();

		for (Node currentNode = reachedNodes.removeFirst(); !reachedNodes.isEmpty(); currentNode = reachedNodes
				.removeFirst()) {
			if (currentNode == null) {
				// Next level of the BFS tree
				currentDist++;
				reachedNodes.add(null);
			} else {
				// Traverse next reached node
				final Set<Node> neighbors = getNeighbors(currentNode);
				if (nbs == null) {
					nbs = neighbors;
				}
				for (final Node neighbor : neighbors) {
					if (visited.add(neighbor)) {
						final int snCount = (currentDist > 2) ? 0 : countNeighborsIn(nbs, neighbor);
						sharedNeighborsHist[snCount]++;
						sPathLengths[currentDist]++;
						result.addSPL(currentDist);
						reachedNodes.add(neighbor);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Accumulates the node and edge betweenness of all nodes in a connected component. The node betweenness
	 * is calculate using the algorithm of Brandes (U. Brandes: A Faster Algorithm for Betweenness Centrality.
	 * Journal of Mathematical Sociology 25(2):163-177, 2001). The edge betweenness is calculated as used by
	 * Newman and Girvan (M.E. Newman and M. Girvan: Finding and Evaluating Community Structure in Networks.
	 * Phys. Rev. E Stat. Nonlin. Soft. Matter Phys., 69, 026113.). In each run of this method a different
	 * source node is chosen and the betweenness of all nodes is replaced by the new one. For the final result
	 * this method has to be run for all nodes of the connected component.
	 * 
	 * This method uses a breadth-first search through the network, starting from a specified source node, in
	 * order to find all paths to the other nodes in the network and to accumulate their betweenness.
	 * 
	 * @param source
	 *            Node where a run of breadth-first search is started, in order to accumulate the node and
	 *            edge betweenness of all other nodes
	 */
	private void computeNBandEB(Node source) {
		LinkedList<Node> done_nodes = new LinkedList<Node>();
		LinkedList<Node> reached = new LinkedList<Node>();
		HashMap<Edge, Double> edgeDependency = new HashMap<Edge, Double>();
		HashMap<Node, Long> stressDependency = new HashMap<Node, Long>();

		final NodeBetweenInfo sourceNBInfo = nodeBetweenness.get(source);
		sourceNBInfo.setSource();
		reached.add(source);
		stressDependency.put(source, Long.valueOf(0));

		// Use BFS to find shortest paths from source to all nodes in the network
		while (!reached.isEmpty()) {
			final Node current = reached.removeFirst();
			done_nodes.addFirst(current);
			final NodeBetweenInfo currentNBInfo = nodeBetweenness.get(current);
			final Set<Node> neighbors = getNeighbors(current);
			for (Node neighbor : neighbors) {
				final NodeBetweenInfo neighborNBInfo = nodeBetweenness.get(neighbor);
				final Edge edge = CyNetworkUtils.getConnEdge(network, current, neighbor);
				final int expectSPLength = currentNBInfo.getSPLength() + 1;
				if (neighborNBInfo.getSPLength() < 0) {
					// Neighbor traversed for the first time
					reached.add(neighbor);
					neighborNBInfo.setSPLength(expectSPLength);
					stressDependency.put(neighbor, Long.valueOf(0));
				}
				// shortest path via current to neighbor found
				if (neighborNBInfo.getSPLength() == expectSPLength) {
					neighborNBInfo.addSPCount(currentNBInfo.getSPCount());
					// check for long overflow
					if (neighborNBInfo.getSPCount() < 0) {
						computeNB = false;
					}

					neighborNBInfo.addPredecessor(current);
					currentNBInfo.addOutedge(edge);
				}
				if (!edgeDependency.containsKey(edge)) {
					edgeDependency.put(edge, new Double(0.0));
				}
			}
		}

		// Return nodes in order of non-increasing distance from source
		while (!done_nodes.isEmpty()) {
			final Node current = done_nodes.removeFirst();
			final NodeBetweenInfo currentNBInfo = nodeBetweenness.get(current);
			if (currentNBInfo != null) {
				final long currentStress = stressDependency.get(current).longValue();
				while (!currentNBInfo.isEmptyPredecessors()) {
					final Node predecessor = currentNBInfo.pullPredecessor();
					final NodeBetweenInfo predecessorNBInfo = nodeBetweenness.get(predecessor);
					predecessorNBInfo
							.addDependency((1.0 + currentNBInfo.getDependency())
									* ((double) predecessorNBInfo.getSPCount() / (double) currentNBInfo
											.getSPCount()));
					// accumulate all sp count
					final long oldStress = stressDependency.get(predecessor).longValue();
					stressDependency.put(predecessor, new Long(oldStress + 1 + currentStress));
					// accumulate edge betweenness
					final Edge e = CyNetworkUtils.getConnEdge(network, predecessor, current);
					if (e != null) {
						LinkedList<Edge> currentedges = currentNBInfo.getOutEdges();
						double oldbetweenness = 0.0;
						double newbetweenness = 0.0;
						if (edgeBetweenness.containsKey(e)) {
							oldbetweenness = edgeBetweenness.get(e).doubleValue();
						}
						// if the node is a leaf node in this search tree
						if (currentedges.size() == 0) {
							newbetweenness = (double) predecessorNBInfo.getSPCount()
									/ (double) currentNBInfo.getSPCount();
						} else {
							double neighbourbetw = 0.0;
							for (Edge neighbouredge : currentedges) {
								if (!e.equals(neighbouredge)) {
									neighbourbetw += edgeDependency.get(neighbouredge).doubleValue();
								}
							}
							newbetweenness = (1 + neighbourbetw)
									* ((double) predecessorNBInfo.getSPCount() / (double) currentNBInfo
											.getSPCount());
						}
						edgeDependency.put(e, new Double(newbetweenness));
						edgeBetweenness.put(e, new Double(newbetweenness + oldbetweenness));
					}
				}
				// accumulate node betweenness in each run
				if (!current.equals(source)) {
					currentNBInfo.addBetweenness(currentNBInfo.getDependency());
					// accumulate number of shortest paths
					final long allSpPaths = stress.get(current).longValue();
					stress.put(current, new Long(allSpPaths + currentNBInfo.getSPCount() * currentStress));
				}
			}
		}
	}

	/**
	 * Computes a normalization factor for node and edge betweenness normalization
	 * 
	 * @param count
	 *            Number of nodes/edges for which betweenness has been computed
	 * @return Normalization factor for node and edge betweenness normalization
	 */
	protected double computeNormFactor(int count) {
		return (count > 2) ? 1.0 / ((count - 1) * (count - 2)) : 1.0;
	}

	/**
	 * Computes the average number of neighbors of the nodes in a given node set.
	 * 
	 * @param aNodes
	 *            Non-empty set of nodes. Specifying <code>null</code> or an empty set for this parameter
	 *            results in throwing an exception.
	 * @return Average number of neighbors of the nodes in <code>aNodes</code>.
	 */
	private double averageNeighbors(Set<Node> aNodes) {
		int neighbors = 0;
		for (final Node node : aNodes) {
			neighbors += getNeighbors(node).size();
		}
		return (double) neighbors / aNodes.size();
	}

	/**
	 * Counts the number of neighbors of the given node that occur in the given set of nodes.
	 * 
	 * @param aSet
	 *            Set of nodes to be searched in.
	 * @param aNode
	 *            Node whose neighbors will be searched in <code>aSet</code>.
	 * @return Number of nodes in <code>aSet</code> that are neighbors of <code>aNode</code>.
	 */
	private int countNeighborsIn(Set<Node> aSet, Node aNode) {
		Set<Node> nbs = CyNetworkUtils.getNeighbors(network, aNode, getIncidentEdges(aNode));
		nbs.retainAll(aSet);
		return nbs.size();
	}

	/**
	 * Computes the topological coefficient of the given node.
	 * 
	 * @param aNode
	 *            Node to get the topological coefficient of.
	 * @param aNeighbors
	 *            Set of all the neighbors of the given node.
	 * @return Topological coefficient of the <code>aNode</code> as a number in the range [0, 1];
	 *         <code>NaN</code> if the topological coefficient function is not defined for the given node.
	 */
	private double computeTC(Node aNode, Set<Node> aNeighbors) {
		Set<Node> comNeNodes = new HashSet<Node>(); // nodes that share common
		// neighbor with aNode
		int tc = 0;
		for (final Node nb : aNeighbors) {
			Set<Node> currentComNeNodes = getNeighbors(nb);
			for (final Node n : currentComNeNodes) {
				if (n != aNode) {
					tc++;
					if (comNeNodes.add(n)) {
						if (aNeighbors.contains(n)) {
							tc++;
						}
					}
				}
			}
		}
		return (double) tc / (double) (comNeNodes.size() * aNeighbors.size());
	}

	/**
	 * Gets all the neighbors of the given node.
	 * 
	 * @param aNode
	 *            Node, whose neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 * @see CyNetworkUtils#getNeighbors(CyNetwork, Node, int[])
	 */
	private Set<Node> getNeighbors(Node aNode) {
		return CyNetworkUtils.getNeighbors(network, aNode, getIncidentEdges(aNode));
	}

	/**
	 * Gets the degree of a given node.
	 * 
	 * @param aNode
	 *            Node to get the degree of.
	 * @param aIncEdges
	 *            Array of the indices of all edges incident on the given node.
	 * @return Degree of the given node, as defined in the book &qout;Graph Theory&qout; by Reinhard Diestel.
	 */
	private int getDegree(Node aNode, int[] aIncEdges) {
		int degree = aIncEdges.length;
		for (int i = 0; i < aIncEdges.length; ++i) {
			Edge e = network.getEdge(aIncEdges[i]);
			if (e.getSource() == e.getTarget() && (!(e.isDirected() && interpr.isPaired()))) {
				degree++;
			}
		}
		return degree;
	}

	/**
	 * Gets all edges incident on the given node.
	 * 
	 * @param aNode
	 *            Node, on which incident edges are to be found.
	 * @return Array of edge indices, containing all the edges in the network incident on <code>aNode</code>.
	 */
	private int[] getIncidentEdges(Node aNode) {
		int ni = aNode.getRootGraphIndex();
		return network.getAdjacentEdgeIndicesArray(ni, true, !interpr.isPaired(), true);
	}

	/**
	 * Histogram of shortest path lengths.
	 * <p>
	 * <code>sPathLength[0]</code> stores the number of nodes processed so far.<br/>
	 * <code>sPathLength[i]</code> for <code>i &gt; 0</code> stores the number of shortest paths of length
	 * <code>i</code> found so far.
	 * </p>
	 */
	private long[] sPathLengths;

	/**
	 * Flag, indicating if the computed parameters must be stored as node attributes.
	 */
	private boolean useNodeAttributes;

	/**
	 * Flag, indicating if the computed parameters must be stored as edge attributes.
	 */
	private boolean useEdgeAttributes;

	/**
	 * Histogram of pairs of nodes that share common neighbors. The i-th element of this array accumulates the
	 * number of node pairs that share i neighbors.
	 */
	private long[] sharedNeighborsHist;

	/**
	 * Round doubles in attributes to <code>roundingDigits</code> decimals after the point.
	 */
	private int roundingDigits;

	/**
	 * Set of visited nodes.
	 * <p>
	 * This set is used exclusively by the method {@link #computeSPandSN(Node)}.
	 * </p>
	 */
	private final Set<Node> visited;

	/**
	 * Flag indicating if node(edge) betweenness and stress should be computed. It is set to false if the
	 * number of shortest paths exceeds the maximum long value.
	 */
	private boolean computeNB;

	/**
	 * Map of all nodes with their respective node betweenness information, which stores information needed
	 * for the node betweenness calculation.
	 */
	private Map<Node, NodeBetweenInfo> nodeBetweenness;

	/**
	 * Map of all nodes with their respective edge betweenness.
	 */
	private Map<Edge, Double> edgeBetweenness;

	/**
	 * Map of all nodes with their respective stress, i.e. number of shortest paths passing through a node.
	 */
	private Map<Node, Long> stress;

	private int nodeCount;
}
