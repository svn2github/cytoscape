package de.mpg.mpi_inf.bioinf.netanalyzer;

import giny.model.Edge;
import giny.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.MutInteger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkInspection;

/**
 * Utility class providing network functionality absent or deprecated in {@link cytoscape.CyNetwork} .
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public abstract class CyNetworkUtils {

	/**
	 * Keeps attributes that are computed by NetworkAnalyzer in this network and can be visualized.
	 * 
	 * @param aNetwork
	 *            The selected network.
	 * @param computedAttr
	 *            Set of the names of all attributes computed in the network.
	 * @param cyAttr
	 *            Set of node/edge attributes in Cytoscape.
	 * @param netAnalyzerAttr
	 *            Set of all node/edge attributes computed by NetworkAnalyzer
	 * 
	 * @return The computed attribute names in the form of two arrays. The names are placed in the returned
	 *         array in alphabetical order.
	 */
	private static String[][] keepAvailableAttributes(CyNetwork aNetwork, Set<String> computedAttr,
			CyAttributes cyAttr, Set<String> netAnalyzerAttr) {
		final List<String> visualizeAttr = new ArrayList<String>(computedAttr.size() + 1);
		for (final String attr : computedAttr) {
			if (cyAttr.getType(attr) == CyAttributes.TYPE_FLOATING
					|| cyAttr.getType(attr) == CyAttributes.TYPE_INTEGER) {
				visualizeAttr.add(attr);
			}
		}
		final List<String> resultNetAnalyzer = new ArrayList<String>(visualizeAttr);
		final List<String> resultOther = new ArrayList<String>(visualizeAttr);
		resultNetAnalyzer.retainAll(netAnalyzerAttr);
		resultOther.removeAll(netAnalyzerAttr);
		Collections.sort(resultNetAnalyzer);
		Collections.sort(resultOther);
		String[][] result = new String[2][];
		result[0] = resultNetAnalyzer.toArray(new String[resultNetAnalyzer.size()]);
		result[1] = resultOther.toArray(new String[resultOther.size()]);
		return result;
	}

	/**
	 * Checks the specified network has values for the given edge attributes.
	 * 
	 * @param aNetwork
	 *            Network of interest.
	 * @return Array of all attributes for which all edges in <code>aNetwork</code> have values; an empty
	 *         set if no such attributes are found.
	 */
	public static String[][] getComputedEdgeAttributes(CyNetwork aNetwork) {
		final CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		final Collection<String> allAttrs = Arrays.asList(edgeAttributes.getAttributeNames());
		final Set<String> computedAttr = new HashSet<String>(allAttrs);
		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		final Iterator<?> itn = aNetwork.edgesIterator();
		while (itn.hasNext()) {
			final String id = ((Edge) itn.next()).getIdentifier();
			for (final String attr : allAttrs) {
				if (!hasAttr(id, edgeAttributes, attr)) {
					computedAttr.remove(attr);
				}
			}
		}
		return keepAvailableAttributes(aNetwork, computedAttr, edgeAttributes, Messages.getEdgeAttributes());
	}

	/**
	 * Checks the specified network has values for the given node attributes.
	 * 
	 * @param aNetwork
	 *            Network of interest.
	 * @return Array of all attributes for which all nodes in <code>aNetwork</code> have values; an empty
	 *         set if no such attributes are found.
	 */
	public static String[][] getComputedNodeAttributes(CyNetwork aNetwork) {
		final CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		final Collection<String> allAttrs = Arrays.asList(nodeAttributes.getAttributeNames());
		final Set<String> computedAttr = new HashSet<String>(allAttrs);
		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		final Iterator<?> itn = aNetwork.nodesIterator();
		while (itn.hasNext()) {
			final String id = ((Node) itn.next()).getIdentifier();
			for (final String attr : allAttrs) {
				if (!hasAttr(id, nodeAttributes, attr)) {
					computedAttr.remove(attr);
				}
			}
		}
		return keepAvailableAttributes(aNetwork, computedAttr, nodeAttributes, Messages.getNodeAttributes());
	}

	/**
	 * Checks if the attribute of a node with current id has a computed value.
	 * 
	 * @param id
	 *            Id of a node of interest.
	 * @param cyAttr
	 *            Set of node/edge attributes in Cytoscape.
	 * @param attr
	 *            An attribute shown in Cytoscape.
	 * @return <code>true</code> when there are computed values for the given attribute; <code>false</code>
	 *         otherwise.
	 */
	private static boolean hasAttr(String id, CyAttributes cyAttr, String attr) {
		final byte attrType = cyAttr.getType(attr);
		if (attrType == CyAttributes.TYPE_FLOATING) {
			if (cyAttr.getDoubleAttribute(id, attr) != null) {
				return true;
			}
		}
		if (attrType == CyAttributes.TYPE_INTEGER) {
			if (cyAttr.getIntegerAttribute(id, attr) != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the directed edge connecting two nodes. We assume this is called only if the graph has no
	 * multiple edges.
	 * 
	 * @param aNode1
	 *            Node that has an edge with aNode2
	 * @param aNode2
	 *            Node that has an edge with aNode1
	 * @return Edge connecting aNode1 and aNode2
	 */
	protected static Edge getConnEdge(CyNetwork aNetwork, Node aNode1, Node aNode2) {
		List<Node> nodes = new ArrayList<Node>(2);
		nodes.add(aNode1);
		nodes.add(aNode2);
		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		List<?> edges = aNetwork.getConnectingEdges(nodes);
		for (int i = 0; i < edges.size(); i++) {
			Edge e = (Edge) edges.get(i);
			if (!e.getSource().equals(e.getTarget())) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets the number of pair connections between the given set of nodes.
	 * <p>
	 * This method effectively counts the number of edges between nodes in the given set, ignoring self-loops
	 * and multiple edges.
	 * </p>
	 * 
	 * @param aNetwork
	 *            Network containing the nodes of interest.
	 * @param aNodeIndices
	 *            Indices of the nodes to be examined.
	 * @param aIgnoreDir
	 *            Flag indicating if connections are undirected.
	 * @return Number of pair connections between the nodes in the given node set. The returned value is
	 *         always in the range <code>[0, n(n-1)/2]</code> for undirected networks (
	 *         <code>aIgnoreDir == true</code>) and <code>[0, n(n-1)]</code> for directed networks (
	 *         <code>aIgnoreDir == true</code>).
	 */
	public static int getPairConnCount(CyNetwork aNetwork, int[] aNodeIndices, boolean aIgnoreDir) {
		int[] connEdges = aNetwork.getConnectingEdgeIndicesArray(aNodeIndices);
		int edgeCount = connEdges.length;

		for (int i = 0; i < connEdges.length; ++i) {
			int edgeIndex = connEdges[i];
			int sourceNodeIndex = aNetwork.getEdgeSourceIndex(edgeIndex);
			int targetNodeIndex = aNetwork.getEdgeTargetIndex(edgeIndex);
			if (sourceNodeIndex == targetNodeIndex) {
				// Ignore self-loops
				edgeCount--;
			} else {
				for (int j = i + 1; j < connEdges.length; j++) {
					if (edgeMatches(aNetwork, connEdges[j], sourceNodeIndex, targetNodeIndex, aIgnoreDir)) {
						// Ignore multiple edges
						edgeCount--;
						break;
					}
				}
			}
		}

		return edgeCount;
	}

	/**
	 * Gets all the neighbors of the given node. All types of edges incident on the node are considered -
	 * incoming, outgoing and undirected.
	 * <p>
	 * Note that the node itself is never returned as its neighbor.
	 * </p>
	 * 
	 * @param aNetwork
	 *            Network that contains the node of interest - <code>aNode</code>.
	 * @param aNode
	 *            Node, whose neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 * @see #getNeighbors(CyNetwork, Node, int[])
	 */
	public static Set<Node> getNeighbors(CyNetwork aNetwork, Node aNode) {
		return getNeighbors(aNetwork, aNode, aNetwork.getAdjacentEdgeIndicesArray(aNode.getRootGraphIndex(),
				true, true, true));
	}

	/**
	 * Gets all the neighbors of the given node.
	 * <p>
	 * Note that the node itself is never returned as its neighbor.
	 * </p>
	 * 
	 * @param aNetwork
	 *            Network that contains the node of interest - <code>aNode</code>.
	 * @param aNode
	 *            Node, whose neighbors are to be found.
	 * @param aIncEdges
	 *            Array of all the edges incident on <code>aNode</code>.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 */
	public static Set<Node> getNeighbors(CyNetwork aNetwork, Node aNode, int[] aIncEdges) {
		Set<Node> neighborsSet = new HashSet<Node>();
		for (int i = 0; i < aIncEdges.length; ++i) {
			Edge e = aNetwork.getEdge(aIncEdges[i]);
			Node sourceNode = e.getSource();
			if (sourceNode != aNode) {
				neighborsSet.add(sourceNode);
			} else {
				Node targetNode = e.getTarget();
				if (targetNode != aNode) {
					neighborsSet.add(targetNode);
				}
			}
		}
		return neighborsSet;
	}

	/**
	 * Gets all the neighbors of the given node and their frequencies. All types of edges incident on the node
	 * are considered - incoming, outgoing and undirected.
	 * <p>
	 * Note that the node itself is never counted as its neighbor.
	 * </p>
	 * 
	 * @param aNetwork
	 *            Network that contains the node of interest - <code>aNode</code>.
	 * @param aNode
	 *            Node, whose neighbors are to be found.
	 * @return <code>Map</code> of <code>Node</code> instances as keys, containing all the neighbors of
	 *         <code>aNode</code> and the number of their occurrences as the values, encapsulated in
	 *         <code>MutInteger</code> instances.
	 * @see #getNeighborMap(CyNetwork, Node, int[])
	 */
	public static Map<Node, MutInteger> getNeighborMap(CyNetwork aNetwork, Node aNode) {
		return getNeighborMap(aNetwork, aNode, aNetwork.getAdjacentEdgeIndicesArray(
				aNode.getRootGraphIndex(), true, true, true));
	}

	/**
	 * Gets all the neighbors of the given node and their frequencies.
	 * <p>
	 * All types of edges incident on the node are considered - incoming, outgoing and undirected.
	 * </p>
	 * <p>
	 * Note that the node itself is never counted as its neighbor.
	 * </p>
	 * 
	 * @param aNetwork
	 *            Network that contains the node of interest - <code>aNode</code>.
	 * @param aNode
	 *            Node, whose neighbors are to be found.
	 * @param aIncEdges
	 *            Array of all the edges incident on <code>aNode</code>.
	 * @return <code>Map</code> of <code>Node</code> instances as keys, containing all the neighbors of
	 *         <code>aNode</code> and the number of their occurrences as values, encapsulated in
	 *         <code>MutInteger</code> instances.
	 */
	public static Map<Node, MutInteger> getNeighborMap(CyNetwork aNetwork, Node aNode, int[] aIncEdges) {
		Map<Node, MutInteger> m = new HashMap<Node, MutInteger>();
		for (int i = 0; i < aIncEdges.length; i++) {
			Edge e = aNetwork.getEdge(aIncEdges[i]);
			Node n = (e.getSource() == aNode) ? e.getTarget() : e.getSource();
			if (n != aNode) {
				MutInteger count = m.get(n);
				if (count == null)
					m.put(n, new MutInteger(1));
				else
					count.value++;
			}
		}
		return m;
	}

	/**
	 * Gets the indices of a given set of nodes.
	 * <p>
	 * Note that the method stores the indices found in an array. The order in which the indices are stored is
	 * unspecified.
	 * </p>
	 * 
	 * @param aNodeSet
	 *            Set of nodes, whose indices are to be found.
	 * @return Array of indices of the nodes contained in <code>aNodeSet</code>.
	 */
	public static int[] getIndices(Set<Node> aNodeSet) {
		int nodeCount = aNodeSet.size();
		int[] indices = new int[nodeCount];
		int i = 0;
		for (final Node node : aNodeSet) {
			indices[i++] = node.getRootGraphIndex();
		}
		return indices;
	}

	/**
	 * Checks if two edges match.
	 * 
	 * @param aNetwork
	 *            Network containing the edges.
	 * @param aEdgeIndex
	 *            Index of the first edge.
	 * @param aSourceIndex
	 *            Source node of the second edge.
	 * @param aTargetIndex
	 *            Target node of the second edge.
	 * @param aIgnoreDir
	 *            Flag indicating if the direction of the edges is to be ignored.
	 * @return <code>true</code> if the edges connect the same pair of nodes (and have same direction, when
	 *         <code>aIgnoreDir</code> is <code>false</code>); <code>false</code> otherwise.
	 */
	private static boolean edgeMatches(CyNetwork aNetwork, int aEdgeIndex, int aSourceIndex,
			int aTargetIndex, boolean aIgnoreDir) {
		int eSource = aNetwork.getEdgeSourceIndex(aEdgeIndex);
		int eTarget = aNetwork.getEdgeTargetIndex(aEdgeIndex);
		return (eSource == aSourceIndex && eTarget == aTargetIndex)
				|| (aIgnoreDir && eSource == aTargetIndex && eTarget == aSourceIndex);
	}

	/**
	 * Performs an inspection on what kind of edges the given network contains.
	 * 
	 * @param aNetwork
	 *            Network to be inspected.
	 * @return Results of the inspection, encapsulated in a <code>NetworkInspection</code> instance.
	 * 
	 * @throws IllegalArgumentException
	 *             If <code>aNetwork</code> is empty, that is, contains zero nodes.
	 * @throws NullPointerException
	 *             If <code>aNetwork</code> is <code>null</code>.
	 */
	public static NetworkInspection inspectNetwork(CyNetwork aNetwork) {
		if (aNetwork.getNodeCount() == 0) {
			throw new IllegalArgumentException();
		}
		final NetworkInspection insp = new NetworkInspection();

		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		Iterator<?> edgesIter = aNetwork.edgesIterator();
		while (edgesIter.hasNext()) {
			final Edge edge = (Edge) edgesIter.next();
			final int ei = edge.getRootGraphIndex();

			// Get all the edges that connect the two ends of the given edge
			final int[] nodeIndices = new int[2];
			final int source = nodeIndices[0] = aNetwork.getEdgeSourceIndex(ei);
			nodeIndices[1] = aNetwork.getEdgeTargetIndex(ei);
			final int[] connecting = aNetwork.getConnectingEdgeIndicesArray(nodeIndices);

			if (nodeIndices[1] == source) {
				// Self-loop inspection
				int d = 0;
				int u = 0;
				for (final int cei : connecting) {
					if (aNetwork.isEdgeDirected(cei)) {
						d++;
					} else {
						u++;
					}
				}
				if (d > 0) {
					insp.dirLoops = true;
					if ((d & 1) != 0) {
						insp.uniqueDir = true;
					}
				}
				if (u > 0) {
					insp.undirLoops = true;
				}
			} else {
				int ssd = 0;
				int ttd = 0;
				int std = 0;
				int tsd = 0;
				int ssu = 0;
				int ttu = 0;
				int u = 0;

				for (final int cei : connecting) {
					final int ceiSource = aNetwork.getEdgeSourceIndex(cei);
					final int ceiTarget = aNetwork.getEdgeTargetIndex(cei);
					final boolean directed = aNetwork.isEdgeDirected(cei);
					if (ceiSource == source) {
						if (ceiTarget == source) {
							if (directed) {
								ssd++;
							} else {
								ssu++;
							}
						} else if (directed) {
							std++;
						} else {
							u++;
						}
					} else if (ceiTarget == source) {
						if (directed) {
							tsd++;
						} else {
							u++;
						}
					} else if (directed) {
						ttd++;
					} else {
						ttu++;
					}
				}

				if (ssd != 0 || ttd != 0) {
					insp.dirLoops = true;
				}
				if (std != 0 || tsd != 0) {
					insp.dir = true;
				}
				if (ssu != 0 || ttu != 0) {
					insp.undirLoops = true;
				}
				if (u != 0) {
					insp.undir = true;
				}
				if (((ssd & 1) != 0) || ((ttd & 1) != 0) || std != tsd) {
					insp.uniqueDir = true;
				}
				if (std > 1 || tsd > 1) {
					insp.dupDirEdges = insp.dupEdges = true;
				} else if (std + tsd + u > 1) {
					insp.dupEdges = true;
				}
			}
		}

		insp.stopTimer();
		return insp;
	}

	/**
	 * Removes all duplicated edges in the network, not including self-loops.
	 * 
	 * @param aNetwork
	 *            Network from which duplicated edges are to be removed.
	 * @param aIgnoreDir
	 *            Flag indicating if edge direction should be ignored. Set this to <code>true</code> if all
	 *            edges are to be treated as undirected.
	 * @param aCreateEdgeAttr
	 *            Flag indicating if an edge attribute representing the number of duplicated edges should be
	 *            created.
	 * 
	 * @return Number of edges removed from the network.
	 */
	public static int removeDuplEdges(CyNetwork aNetwork, boolean aIgnoreDir, boolean aCreateEdgeAttr) {
		final Set<Node> visited = new HashSet<Node>(aNetwork.getNodeCount());
		int removedCount = 0;

		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		final Iterator<?> itn = aNetwork.nodesIterator();
		while (itn.hasNext()) {
			final Node n1 = (Node) itn.next();
			final int ni = n1.getRootGraphIndex();
			final int[] incEdges = aNetwork.getAdjacentEdgeIndicesArray(ni, true, true, true);
			final Map<Node, MutInteger> neighborMap = CyNetworkUtils.getNeighborMap(aNetwork, n1, incEdges);
			// Check all neighbors of the node for multiple edges
			for (final Map.Entry<Node, MutInteger> nf : neighborMap.entrySet()) {
				final Node n2 = nf.getKey();
				if (!visited.contains(n2)) {
					final List<Node> nodes = new ArrayList<Node>(2);
					nodes.add(n1);
					nodes.add(n2);
					Edge undirEdge = null;
					Edge dir12Edge = null;
					Edge dir21Edge = null;
					int numUndir = 0;
					int numDir12 = 0;
					int numDir21 = 0;

					// Traverse all edges connecting n1 and n2
					for (final Object obj : aNetwork.getConnectingEdges(nodes)) {
						final Edge e = (Edge) obj;
						final Node source = e.getSource();
						final Node target = e.getTarget();
						if (source != target) {
							if (aIgnoreDir == false && e.isDirected()) {
								if (source == n1) {
									if (dir12Edge != null) {
										aNetwork.removeEdge(e.getRootGraphIndex(), false);
										removedCount++;
									} else {
										dir12Edge = e;
									}
									numDir12++;
								} else if (dir21Edge != null) {
									aNetwork.removeEdge(e.getRootGraphIndex(), false);
									removedCount++;
									numDir21++;
								} else {
									dir21Edge = e;
									numDir21++;
								}
							} else if (undirEdge != null) {
								aNetwork.removeEdge(e.getRootGraphIndex(), false);
								removedCount++;
								numUndir++;
							} else {
								undirEdge = e;
								numUndir++;
							}
						}
					}
					// store the number of removed edges as edge attribute
					if (aCreateEdgeAttr) {
						saveNumDuplEdges(undirEdge, numUndir);
						saveNumDuplEdges(dir12Edge, numDir12);
						saveNumDuplEdges(dir21Edge, numDir21);
					}
				}
			}
			visited.add(n1);
		}
		return removedCount;
	}

	/**
	 * Saves the number of edges duplicated to aEdge as an edge attribute.
	 * 
	 * @param aEdge
	 *            Edge for which duplicated edges are saved.
	 * @param aNumEdges
	 *            NUmber of edges duplicated to aEdge.
	 */
	private static void saveNumDuplEdges(Edge aEdge, int aNumEdges) {
		if (aEdge != null) {
			Cytoscape.getEdgeAttributes().setAttribute(aEdge.getIdentifier(), Messages.getAttr("dpe"),
					new Integer(aNumEdges));
		}
	}

	/**
	 * Removes all self-loops in a network and returns the number of removed self-loops. All types of edges
	 * are considered - incoming, outgoing and undirected.
	 * 
	 * @param aNetwork
	 *            Network from which self-loops are to be removed.
	 * @return Number of removed self-loops.
	 */
	public static int removeSelfLoops(CyNetwork aNetwork) {
		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		Iterator<?> iter = aNetwork.edgesIterator();
		int removedCount = 0;
		while (iter.hasNext()) {
			Edge e = (Edge) iter.next();
			if (e.getSource() == e.getTarget()) {
				aNetwork.removeEdge(e.getRootGraphIndex(), false);
				removedCount++;
			}
		}
		return removedCount;
	}
}
