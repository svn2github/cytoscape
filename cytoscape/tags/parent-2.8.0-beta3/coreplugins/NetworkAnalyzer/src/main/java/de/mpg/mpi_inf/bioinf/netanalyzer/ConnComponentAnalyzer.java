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

import giny.model.Node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import cytoscape.CyNetwork;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfo;

/**
 * Control class providing analysis of the connected components in a Cytoscape network.
 * 
 * @author Yassen Assenov
 */
public class ConnComponentAnalyzer {

	/**
	 * Gets all nodes in the given connected component.
	 * 
	 * @param aNetwork Network in which the given connected components is contained.
	 * @param aCompInfo Information about the connected component of interest.
	 * @return Set of all nodes in <code>aCompInfo</code>; empty set if this component is not
	 *         contained in <code>aNetwork</code>.
	 */
	public static Set<Node> getNodesOf(CyNetwork aNetwork, CCInfo aCompInfo) {
		Set<Node> nodes = new HashSet<Node>(aCompInfo.getSize());
		nodes.add(aCompInfo.getNode());
		LinkedList<Node> toTraverse = new LinkedList<Node>();
		toTraverse.add(aCompInfo.getNode());

		while (!toTraverse.isEmpty()) {
			final Node node = toTraverse.removeFirst();
			final Set<Node> neighbors = CyNetworkUtils.getNeighbors(aNetwork, node);
			for (Node nb : neighbors) {
				if (!nodes.contains(nb)) {
					nodes.add(nb);
					toTraverse.add(nb);
				}
			}
		}
		return nodes;
	}

	/**
	 * Initializes a new instance of <code>ConnComponentAnalyzer</code>.
	 * 
	 * @param aNetwork Network to be analyzed.
	 */
	public ConnComponentAnalyzer(CyNetwork aNetwork) {
		network = aNetwork;
	}

	/**
	 * Finds all components in the analyzer network.
	 * 
	 * @return Set of all components in the analyzed network ({@link #getNetwork()}); empty set if
	 *         the analyzed network is empty.
	 */
	public Set<CCInfo> findComponents() {
		int untravCount = network.getNodeCount();

		Set<Node> traversed = new HashSet<Node>(untravCount);
		Set<CCInfo> components = new HashSet<CCInfo>();

		// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
		Iterator<?> it = network.nodesIterator();
		while (untravCount > 0 && it.hasNext()) {
			final Node node = (Node) it.next();
			if (!traversed.contains(node)) {
				// Unmarked node reached - create new conn. component
				final int ccSize = traverseReachable(node, traversed);
				untravCount -= ccSize;
				components.add(new CCInfo(ccSize, node));
			}
		}
		return components;
	}

	/**
	 * Finds (the) largest connected component of the analyzed network. The largest component is the
	 * one that contains the most nodes.
	 * <p>
	 * If the network contains more than one largest connected components, one of them is returned.
	 * There is no guarantee that the one returned satisfies any requirements (apart from having
	 * largest size).
	 * </p>
	 * 
	 * @return Connected component C in the analyzed network, such that no other connected component
	 *         in the network contains more nodes than C.
	 */
	public CCInfo findLargestComponent() {
		CCInfo largest = new CCInfo(0, null);
		final Set<CCInfo> comps = findComponents();
		for (CCInfo current : comps) {
			if (current.getSize() > largest.getSize()) {
				largest = current;
			}
		}
		return largest;
	}

	/**
	 * Gets the analyzed network.
	 * 
	 * @return Network instance to be analyzed.
	 */
	public CyNetwork getNetwork() {
		return network;
	}

	/**
	 * Gets all nodes in the given connected component.
	 * 
	 * @param aCompInfo Information about the connected component of interest.
	 * @return Set of all nodes in <code>aCompInfo</code>; empty set if this component is not
	 *         contained in the analyzed network ({@link #getNetwork()}).
	 * 
	 * @see #getNodesOf(CyNetwork, CCInfo)
	 */
	public Set<Node> getNodesOf(CCInfo aCompInfo) {
		return getNodesOf(network, aCompInfo);
	}

	/**
	 * Traverses all nodes that are reachable from the given node.
	 * 
	 * @param aNode Node to start the traversal from.
	 * @param aTraversed Set of traversed nodes. This method add all visited nodes to this set.
	 * @return Number of nodes which were traversed. <code>aNode</code> itself is also counted as
	 *         traversed.
	 */
	private int traverseReachable(Node aNode, Set<Node> aTraversed) {
		int size = 1;
		LinkedList<Node> toTraverse = new LinkedList<Node>();
		aTraversed.add(aNode);
		toTraverse.add(aNode);
		while (!toTraverse.isEmpty()) {
			final Node currentNode = toTraverse.removeFirst();
			final Set<Node> neighbors = CyNetworkUtils.getNeighbors(network, currentNode);
			for (Node nb : neighbors) {
				if (!aTraversed.contains(nb)) {
					size++;
					toTraverse.add(nb);
					aTraversed.add(nb);
				}
			}
		}
		return size;
	}

	/**
	 * Analyzed network.
	 */
	private CyNetwork network;
}
