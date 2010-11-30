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

import java.util.Iterator;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

/**
 * Controller class providing algorithms for intersection, union and difference of two networks.
 * 
 * @author Caroline Becker
 * @author Yassen Assenov
 * @version 1.0
 */
public class GOPTAlgorithm {

	/**
	 * Initializes a new instance of <code>GOPTAlgorithm</code>.
	 * 
	 * @param aNetwork1 First network.
	 * @param aNetwork2 Second network.
	 * @throws NullPointerException If <code>aNetwork1</code> or <code>aNetwork2</code> is
	 *         <code>null</code>.
	 */
	public GOPTAlgorithm(CyNetwork aNetwork1, CyNetwork aNetwork2) {
		if (aNetwork1 == null || aNetwork2 == null) {
			throw new NullPointerException();
		}
		network1 = aNetwork1;
		network2 = aNetwork2;
	}

	/**
	 * Computes the requested operations of the networks this instance was initialized with.
	 * 
	 * @param aIntersection Flag indicating if the intersection of the two networks must be computed.
	 * @param aUnion Flag indicating if the union of the two networks must be computed.
	 * @param aDifference Flag indicating if the differences of the two networks must be computed.
	 */
	public void computeNetworks(boolean aIntersection, boolean aUnion, boolean aDifference) {
		if (aIntersection || aUnion || aDifference) {
			// Create the required (empty) networks
			final String title1 = network1.getTitle();
			final String title2 = network2.getTitle();
			if (aIntersection) {
				intersectionNw = Cytoscape.createNetwork(title1 + " AND " + title2);
			}
			if (aUnion) {
				unionNw = Cytoscape.createNetwork(title1 + " OR " + title2);
			}
			if (aDifference) {
				diffNw1 = Cytoscape.createNetwork(title1 + " - " + title2);
				diffNw2 = Cytoscape.createNetwork(title2 + " - " + title1);
			}

			// Iterate over the nodes of the two networks
			// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
			Iterator<?> nodeIt = network1.nodesIterator();
			Node actNode;
			while (nodeIt.hasNext()) {
				actNode = (Node) nodeIt.next();
				if (network2.containsNode(actNode)) {
					if (aIntersection) {
						intersectionNw.addNode(actNode);
					}
				} else {
					if (aUnion) {
						unionNw.addNode(actNode);
					}
					if (aDifference) {
						diffNw1.addNode(actNode);
					}
				}
			}
			nodeIt = network2.nodesIterator();
			while (nodeIt.hasNext()) {
				actNode = (Node) nodeIt.next();
				if (aUnion) {
					unionNw.addNode(actNode);
				}
				if (aDifference && (! network1.containsNode(actNode))) {
					diffNw2.addNode(actNode);
				}
			}

			// Iterate over the edges of the two networks
			// TODO: [Cytoscape 2.8] Check if the returned iterator is parameterized
			Iterator<?> edgeIt = network1.edgesIterator();
			Edge actEdge;
			while (edgeIt.hasNext()) {
				actEdge = (Edge) edgeIt.next();
				if (network2.containsEdge(actEdge)) {
					if (aIntersection) {
						intersectionNw.addEdge(actEdge);
					}
				} else {
					if (aUnion) {
						unionNw.addEdge(actEdge);
					}
					if (aDifference && diffNw1.containsNode(actEdge.getSource())
						&& diffNw1.containsNode(actEdge.getTarget())) {
						diffNw1.addEdge(actEdge);
					}
				}
			}
			edgeIt = network2.edgesIterator();
			while (edgeIt.hasNext()) {
				actEdge = (Edge) edgeIt.next();
				if (aUnion) {
					unionNw.addEdge(actEdge);
				}
				if (aDifference && ( !network1.containsEdge(actEdge))
					&& diffNw2.containsNode(actEdge.getSource())
					&& diffNw2.containsNode(actEdge.getTarget())) {
					diffNw2.addEdge(actEdge);
				}
			}
		}
	}

	/**
	 * First of the two manipulated networks.
	 */
	private CyNetwork network1;

	/**
	 * Second manipulated network.
	 */
	private CyNetwork network2;

	/**
	 * Network obtained by intersecting {@link #network1} and {@link #network2}.
	 */
	private CyNetwork intersectionNw;

	/**
	 * Network obtained by the union of {@link #network1} and {@link #network2}.
	 */
	private CyNetwork unionNw;

	/**
	 * Network obtained the difference {@link #network1} <code>\</code> {@link #network2}.
	 */
	private CyNetwork diffNw1;

	/**
	 * Network obtained the difference {@link #network2} <code>\</code> {@link #network1}.
	 */
	private CyNetwork diffNw2;
}
