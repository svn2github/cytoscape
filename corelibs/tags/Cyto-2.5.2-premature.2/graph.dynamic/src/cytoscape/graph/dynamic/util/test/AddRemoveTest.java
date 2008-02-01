
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

package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class AddRemoveTest {
	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		final DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
		final int[][] nodesArr = new int[][] { new int[100000], new int[99980], new int[100010] };
		final int[] edges = new int[1000000];
		final int iterations = 10000;

		for (int foo = 0; foo < iterations; foo++) {
			boolean print = false;

			if ((foo % 10) == 0) {
				print = true;
			}

			if (print) {
				System.out.println("at add/remove iteration " + (foo + 1) + " of " + iterations);
			}

			if (print) {
				System.out.println("creating nodes");
			}

			final int[] nodes = nodesArr[foo % nodesArr.length];

			for (int i = 0; i < nodes.length; i++)
				nodes[i] = graph.nodeCreate();

			if (print) {
				System.out.println("creating edges");
			}

			for (int i = 0; i < edges.length; i++)
				edges[i] = graph.edgeCreate(nodes[i % nodes.length], nodes[(i * 3) % nodes.length],
				                            true);

			if (print) {
				System.out.println("in graph: " + graph.nodes().numRemaining() + " nodes and "
				                   + graph.edges().numRemaining() + " edges");
			}

			if (print) {
				System.out.println();
			}

			if (print) {
				System.out.println("removing edges");
			}

			for (int i = 0; i < edges.length; i++)
				graph.edgeRemove(edges[i]);

			if (print) {
				System.out.println("removing nodes");
			}

			for (int i = 0; i < nodes.length; i++)
				graph.nodeRemove(nodes[i]);

			if (print) {
				System.out.println("in graph: " + graph.nodes().numRemaining() + " nodes and "
				                   + graph.edges().numRemaining() + " edges");
			}

			if (print) {
				System.out.println();
			}
		}
	}
}
