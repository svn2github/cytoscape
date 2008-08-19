
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

package fing.model.test;

import fing.model.FingRootGraphFactory;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;


/**
 *
 */
public final class RootGraphAddRemoveTest {
	// No constructor.
	private RootGraphAddRemoveTest() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws ClassNotFoundException DOCUMENT ME!
	 * @throws InstantiationException DOCUMENT ME!
	 * @throws IllegalAccessException DOCUMENT ME!
	 */
	public static final void main(String[] args)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		final RootGraph root = getRootGraph(args);

		int[] nodeInx;
		final int[] edgeInx = new int[1000000];
		final int[] nodeNums = new int[] { 200000, 199900, 200200 };
		final int iterations = 100;

		for (int foo = 0; foo < iterations; foo++) {
			boolean print = true;

			if (!((foo % 1) == 0))
				print = false;

			if (print)
				System.out.println("at add/remove iteration " + (foo + 1) + " of " + iterations);

			final int numNodes = nodeNums[foo % nodeNums.length];

			if (print)
				System.out.println("creating " + numNodes + " nodes");

			nodeInx = root.createNodes(numNodes);

			if (print)
				System.out.println("creating " + edgeInx.length + " edges");

			for (int i = 0; i < edgeInx.length; i++)
				edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
				                             nodeInx[(i * 3) % nodeInx.length]);

			if (print)
				printme(root);

			if ((foo % 2) == 0) {
				if (print)
					System.out.println("removing all edges from RootGraph");

				root.removeEdges(edgeInx);
			}

			if (print)
				System.out.println("removing all nodes from RootGraph");

			root.removeNodes(nodeInx);

			if (print)
				printme(root);
		}
	}

	private static void printme(RootGraph root) {
		System.out.println("in RootGraph: " + root.getNodeCount() + " nodes and "
		                   + root.getEdgeCount() + " edges");
		System.out.println();
	}

	private static final RootGraph getRootGraph(String[] mainArgs)
	    throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if ((mainArgs.length > 0) && mainArgs[0].equalsIgnoreCase("luna"))
			return (RootGraph) Class.forName("luna.LunaRootGraph").newInstance();
		else

			return FingRootGraphFactory.instantiateRootGraph();
	}
}
