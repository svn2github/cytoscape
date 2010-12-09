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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.MutInteger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.NetworkInspection;

/**
 * Utility class providing network functionality absent or deprecated in {@link org.cytoscape.model.CyNetwork} .
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
			CyTable table, Set<String> netAnalyzerAttr) {
		final List<String> visualizeAttr = new ArrayList<String>(computedAttr.size() + 1);
		final Map<String,Class<?>> columnTypeMap = table.getColumnTypeMap();
		for (final String attr : computedAttr) {
			if (columnTypeMap.get(attr) == Double.class || 
			    columnTypeMap.get(attr) == Integer.class) {
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
		final CyTable table = tableMgr.getTableMap(CyEdge.class,aNetwork).get(CyNetwork.DEFAULT_ATTRS);
		final Map<String,Class<?>> columnTypeMap = table.getColumnTypeMap();
		final Set<String> computedAttr = new HashSet<String>(columnTypeMap.keySet());
		for (final CyEdge n : aNetwork.getEdgeList()) {
			for ( final Map.Entry<String,Class<?> e : columnTypeMap.entrySet() )
			if (!n.getCyRow().isSet(e.getKey(),e.getValue())
				computedAttr.remove(e.getKey());
		}
		return keepAvailableAttributes(aNetwork, computedAttr, table, Messages.getEdgeAttributes());
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
		final CyTable table = tableMgr.getTableMap(CyNode.class,aNetwork).get(CyNetwork.DEFAULT_ATTRS);
		final Map<String,Class<?>> columnTypeMap = table.getColumnTypeMap();
		final Set<String> computedAttr = new HashSet<String>(columnTypeMap.keySet());
		for (final CyNode n : aNetwork.getNodeList()) {
			for ( final Map.Entry<String,Class<?> e : columnTypeMap.entrySet() )
			if (!n.getCyRow().isSet(e.getKey(),e.getValue())
				computedAttr.remove(e.getKey());
		}
		return keepAvailableAttributes(aNetwork, computedAttr, table, Messages.getNodeAttributes());
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
	public static int getPairConnCount(CyNetwork aNetwork, Collection<CyNode> aNodeIndices, boolean aIgnoreDir) {

		Set<CyEdge> connEdgeSet = new HashSet<CyEdge>();
		for ( CyNode n1 : aNodeIndices ) 
			for ( CyNode n2 : aNodeIndices ) 
				connEdges.addAll( aNetwork.getConnectingEdgeList(n1,n2,CyEdge.Type.ANY) );
			
		int edgeCount = connEdges.size();

		List<CyEdge> connEdges = new ArrayList<CyEdge>(connEdgeSet);

		for (int i = 0; i < connEdges.size; ++i) {
			CyEdge e = connEdges.get(i); 
			
			// Ignore self-loops
			if ( e.getSource() == e.getTarget() ) {
				edgeCount--;
			} else {
				// Ignore multiple edges
				for (int j = i + 1; j < connEdges.size; ++j) {
					CyEdge ee = connEdges.get(j); 
					if ( // directed edges have same source + target 
					     ( e.getSource() == ee.getSource() &&
					       e.getTarget() == ee.getTarget() ) ||
						 // or undirected edges have same source + target (if we care)
					     ( aIgnoreDir &&
						   ( e.getSource() == ee.getTarget() &&
						      e.getTarget() == ee.getSource() ) ) ) {

						edgeCount--;
						// TODO I think this break is wrong! 
						// What if there are more than two edges?
						break;
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
	 *            CyNode , whose neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 * @see #getNeighbors(CyNetwork, CyNode , int[])
	 */
	public static Set<CyNode> getNeighbors(CyNetwork aNetwork, CyNode aNode) {
		return getNeighbors(aNetwork, aNode, aNetwork.getAdjacentEdgeList(aNode)); 
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
	 *            CyNode , whose neighbors are to be found.
	 * @param aIncEdges
	 *            Array of all the edges incident on <code>aNode</code>.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 */
	public static Set<CyNode> getNeighbors(CyNetwork aNetwork, CyNode aNode, List<CyEdge> aIncEdges) {
		Set<CyNode> neighborsSet = new HashSet<CyNode>();
		for ( CyEdge e : aIncEdges ) {
			CyNode sourceNode = e.getSource();
			if (sourceNode != aNode) {
				neighborsSet.add(sourceNode);
			} else {
				CyNode targetNode = e.getTarget();
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
	 *            CyNode , whose neighbors are to be found.
	 * @return <code>Map</code> of <code>Node</code> instances as keys, containing all the neighbors of
	 *         <code>aNode</code> and the number of their occurrences as the values, encapsulated in
	 *         <code>MutInteger</code> instances.
	 * @see #getNeighborMap(CyNetwork, CyNode , int[])
	 */
	public static Map<CyNode, MutInteger> getNeighborMap(CyNetwork aNetwork, CyNode aNode) {
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
	 *            CyNode , whose neighbors are to be found.
	 * @param aIncEdges
	 *            Array of all the edges incident on <code>aNode</code>.
	 * @return <code>Map</code> of <code>Node</code> instances as keys, containing all the neighbors of
	 *         <code>aNode</code> and the number of their occurrences as values, encapsulated in
	 *         <code>MutInteger</code> instances.
	 */
	public static Map<CyNode, MutInteger> getNeighborMap(CyNetwork aNetwork, CyNode aNode, int[] aIncEdges) {
		Map<CyNode, MutInteger> m = new HashMap<CyNode, MutInteger>();
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
	public static int[] getIndices(Set<CyNode> aNodeSet) {
		int nodeCount = aNodeSet.size();
		int[] indices = new int[nodeCount];
		int i = 0;
		for (final CyNode node : aNodeSet) {
			indices[i++] = node.getRootGraphIndex();
		}
		return indices;
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
			final CyEdge edge = (CyEdge) edgesIter.next();
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
		final Set<CyNode> visited = new HashSet<CyNode>(aNetwork.getNodeCount());
		int removedCount = 0;

		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		final Iterator<?> itn = aNetwork.nodesIterator();
		while (itn.hasNext()) {
			final CyNode n1 = (CyNode) itn.next();
			final int ni = n1.getRootGraphIndex();
			final int[] incEdges = aNetwork.getAdjacentEdgeIndicesArray(ni, true, true, true);
			final Map<CyNode, MutInteger> neighborMap = CyNetworkUtils.getNeighborMap(aNetwork, n1, incEdges);
			// Check all neighbors of the node for multiple edges
			for (final Map.Entry<CyNode, MutInteger> nf : neighborMap.entrySet()) {
				final CyNode n2 = nf.getKey();
				if (!visited.contains(n2)) {
					final List<CyNode> nodes = new ArrayList<CyNode>(2);
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
						final CyEdge e = (CyEdge) obj;
						final CyNode source = e.getSource();
						final CyNode target = e.getTarget();
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
	 *            CyEdge for which duplicated edges are saved.
	 * @param aNumEdges
	 *            NUmber of edges duplicated to aEdge.
	 */
	private static void saveNumDuplEdges(CyEdge aEdge, int aNumEdges) {
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
			Edge e = (CyEdge) iter.next();
			if (e.getSource() == e.getTarget()) {
				aNetwork.removeEdge(e.getRootGraphIndex(), false);
				removedCount++;
			}
		}
		return removedCount;
	}
}
