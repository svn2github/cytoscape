
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

import giny.model.GraphPerspective;
import giny.model.RootGraph;


/**
 *
 */
public class GCTest {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
		final int[] nodeInx = root.createNodes(10000);
		final int[] edgeInx = new int[100000];

		for (int i = 0; i < edgeInx.length; i++)
			edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
			                             nodeInx[(i * 3) % nodeInx.length]);

		System.out.println("RootGraph node count: " + root.getNodeCount());
		System.out.println("RootGraph edge count: " + root.getEdgeCount());
		System.out.println();

		for (int i = 0; i < 1000; i++) {
			GraphPerspective persp = root.createGraphPerspective(nodeInx, edgeInx);
			System.out.println("GraphPerspective node count: " + persp.getNodeCount());
			System.out.println("GraphPerspective edge count: " + persp.getEdgeCount());
			System.out.println();
		}
	}
}
