
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

package cytoscape.graph.dynamic;

import cytoscape.graph.dynamic.util.DynamicGraphFactory;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import junit.framework.*;

public class GraphTest extends TestCase {

	public void testGraph() {
		final DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
		System.out.println("Creating 10 nodes...");

		for (int i = 0; i < 10; i++)
			graph.nodeCreate();

		IntEnumerator nodesEnum = graph.nodes();
		int index = -1;
		int[] nodes = new int[nodesEnum.numRemaining()];
		System.out.print("Here are the nodes: ");

		while (nodesEnum.numRemaining() > 0) {
			nodes[++index] = nodesEnum.nextInt();
			System.out.print(nodes[index] + " ");
		}

		System.out.println();
		System.out.println();

		boolean[] edgesDir = new boolean[] {
		                         false, true, true, true, false, true, false, false, true, true,
		                         false, false, true, false, true
		                     };
		int[][] edgesDef = new int[][] {
		                       { 2, 5 },
		                       { 0, 8 },
		                       { 4, 1 },
		                       { 9, 0 },
		                       { 9, 0 },
		                       { 0, 8 },
		                       { 1, 4 },
		                       { 2, 2 },
		                       { 7, 7 },
		                       { 1, 1 },
		                       { 3, 1 },
		                       { 7, 2 },
		                       { 1, 0 },
		                       { 8, 5 },
		                       { 4, 9 }
		                   };

		for (int i = 0; i < edgesDir.length; i++) {
			System.out.println("Creating " + (edgesDir[i] ? "directed" : "undirected")
			                   + " edge from node " + nodes[edgesDef[i][0]] + " to node "
			                   + nodes[edgesDef[i][1]] + "...");
			graph.edgeCreate(nodes[edgesDef[i][0]], nodes[edgesDef[i][1]], edgesDir[i]);
		}

		IntEnumerator edgesEnum = graph.edges();
		System.out.println();
		System.out.println("Here are the edges:");

		while (edgesEnum.numRemaining() > 0) {
			final int edge = edgesEnum.nextInt();
			System.out.println(((graph.edgeType(edge) == DynamicGraph.DIRECTED_EDGE) ? "Directed"
			                                                                         : "Undirected")
			                   + " edge " + edge + " with source " + graph.edgeSource(edge)
			                   + " and target " + graph.edgeTarget(edge) + ".");
		}

		System.out.println();
		System.out.println("All adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, true, true, true);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();
		System.out.println("All undirected adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, false, false, true);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();
		System.out.println("All undirected and incoming adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, false, true, true);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();
		System.out.println("All outgoing adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, true, false, false);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();
		System.out.println("All outgoing and incoming adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, true, true, false);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();

		for (int i = 0; i < nodes.length; i++)
			if ((i % 3) == 0) {
				System.out.println("Removing node " + nodes[i] + "...");
				graph.nodeRemove(nodes[i]);
			}

		System.out.println();
		System.out.println("All adjacent edges...");
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node = nodesEnum.nextInt();
			IntEnumerator adjEdges = graph.edgesAdjacent(node, true, true, true);
			System.out.print("For node " + node + ": ");

			while (adjEdges.numRemaining() > 0) {
				final int edge = adjEdges.nextInt();
				System.out.print(edge + " ");
			}

			System.out.println();
		}

		System.out.println();
		nodesEnum = graph.nodes();

		while (nodesEnum.numRemaining() > 0) {
			final int node0 = nodesEnum.nextInt();
			IntEnumerator nodesEnum2 = graph.nodes();

			while (nodesEnum2.numRemaining() > 0) {
				final int node1 = nodesEnum2.nextInt();
				IntIterator connectingEdges = graph.edgesConnecting(node0, node1, true, true, true);
				System.out.print("All edges connecting node " + node0 + " with node " + node1
				                 + ": ");

				while (connectingEdges.hasNext())
					System.out.print(connectingEdges.nextInt() + " ");

				System.out.println();
			}
		}
	}
}
