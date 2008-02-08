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
package cytoscape.util;

import cytoscape.AllTests;
import cytoscape.GraphPerspective;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.CyAttributes;

import cytoscape.util.GraphSetUtils;

import cytoscape.RootGraph;

import junit.framework.*;

import java.lang.Object;

import java.util.*;


/**
 *
 */
public class GraphSetUtilsTest extends TestCase {
	protected List networklist;
	protected int a;
	protected int b;
	protected int c;
	protected int d;
	protected int e;
	protected int ab;
	protected int bc;
	protected int ac;
	protected int bd;
	protected int be;
	protected int cd;
	protected GraphPerspective net1;
	protected GraphPerspective net2;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
		networklist = new ArrayList();

		RootGraph root = Cytoscape.getRootGraph();
		a = root.createNode();
		b = root.createNode();
		c = root.createNode();
		d = root.createNode();
		e = root.createNode();

		int[] nodes1 = new int[] { a, b, c, d };
		int[] nodes2 = new int[] { b, c, d, e };

		ab = root.createEdge(a, b, true);
		bc = root.createEdge(b, c, true);
		ac = root.createEdge(a, c, true);
		bd = root.createEdge(b, d, true);
		be = root.createEdge(b, e, true);
		cd = root.createEdge(c, d, true);

		int[] edges1 = new int[] { ab, bc, ac, bd };
		int[] edges2 = new int[] { bd, bc, be };

		net1 = Cytoscape.createNetwork(nodes1, edges1, "graph1");
		net2 = Cytoscape.createNetwork(nodes2, edges2, "graph2");
		networklist.add(0, net1);
		networklist.add(1, net2);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testNetwork() {
		//verify created nodes exist
		assertTrue(net1.containsNode(net1.getNode(a)));
		assertTrue(net1.containsNode(net1.getNode(b)));
		assertTrue(net1.containsNode(net1.getNode(c)));
		assertTrue(net1.containsNode(net1.getNode(d)));

		assertTrue(net2.containsNode(net2.getNode(b)));
		assertTrue(net2.containsNode(net2.getNode(c)));
		assertTrue(net2.containsNode(net2.getNode(d)));
		assertTrue(net2.containsNode(net2.getNode(e)));

		assertTrue(net1.containsEdge(net1.getEdge(ab)));
		assertTrue(net1.containsEdge(net1.getEdge(bc)));
		assertTrue(net1.containsEdge(net1.getEdge(ac)));
		assertTrue(net1.containsEdge(net1.getEdge(bd)));

		assertTrue(net2.containsEdge(net2.getEdge(bd)));
		assertTrue(net2.containsEdge(net2.getEdge(bc)));
		assertTrue(net2.containsEdge(net2.getEdge(be)));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testIntersection() {
		GraphPerspective n = GraphSetUtils.createIntersectionGraph(networklist, true, "intersect");

		assertTrue(n.containsEdge(n.getEdge(bc)));
		assertTrue(n.containsEdge(n.getEdge(bd)));
		assertNull(n.getEdge(ab));
		assertNull(n.getEdge(be));

		assertTrue(n.containsNode(n.getNode(b)));
		assertTrue(n.containsNode(n.getNode(c)));
		assertTrue(n.containsNode(n.getNode(d)));
		assertNull(n.getNode(a));
		assertNull(n.getNode(e));
	} // testIntersection

	/**
	 *  DOCUMENT ME!
	 */
	public void testDifference() {
		GraphPerspective x = GraphSetUtils.createDifferenceGraph(networklist, true, "difference");

		assertTrue(x.containsNode(x.getNode(a)));
		assertTrue(x.containsNode(x.getNode(b)));
		assertTrue(x.containsNode(x.getNode(c)));
		assertNull(x.getNode(d));
		assertNull(x.getNode(e));

		assertTrue(x.containsEdge(x.getEdge(ab)));
		assertNull(x.getEdge(bc));
		assertTrue(x.containsEdge(x.getEdge(ac)));
		assertNull(x.getEdge(be));
		assertNull(x.getEdge(bd));
	} // testDifference

	/**
	 *  DOCUMENT ME!
	 */
	public void testUnion() {
		GraphPerspective y = GraphSetUtils.createUnionGraph(networklist, true, "union");

		assertTrue(y.containsEdge(y.getEdge(ab)));
		assertTrue(y.containsEdge(y.getEdge(bc)));
		assertTrue(y.containsEdge(y.getEdge(ac)));
		assertTrue(y.containsEdge(y.getEdge(bd)));

		assertTrue(y.containsNode(y.getNode(a)));
		assertTrue(y.containsNode(y.getNode(b)));
		assertTrue(y.containsNode(y.getNode(c)));
		assertTrue(y.containsNode(y.getNode(d)));
		assertTrue(y.containsNode(y.getNode(e)));
	} // testUnion
}
