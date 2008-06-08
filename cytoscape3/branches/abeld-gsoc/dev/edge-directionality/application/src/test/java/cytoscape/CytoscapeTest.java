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
package cytoscape;

import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;
import cytoscape.Cytoscape;

import cytoscape.data.ImportHandler;
import cytoscape.data.Semantics;

import org.cytoscape.Edge;
import org.cytoscape.Node;

import junit.framework.TestCase;

import java.io.IOException;

import java.util.*;


/**
 *
 */
public class CytoscapeTest extends TestCase {
	GraphPerspective cytoNetwork;
	String title;
	int nodeCount;
	int edgeCount;

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
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
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testGetImportHandler() throws Exception {
		ImportHandler importHandler = Cytoscape.getImportHandler();
		assertEquals(importHandler.getClass(), ImportHandler.class);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testNullNetwork() throws Exception {
		cytoNetwork = Cytoscape.getNullNetwork();

		title = cytoNetwork.getTitle();
		assertEquals("0", title);

		nodeCount = cytoNetwork.getNodeCount();
		assertEquals(0, nodeCount);

		edgeCount = cytoNetwork.getEdgeCount();
		assertEquals(0, edgeCount);
	}

	//try creating a network
	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testCreateNonexistentNetwork() throws Exception {
		try {
			cytoNetwork = Cytoscape.createNetworkFromFile("nonexistentNetwork");
		} catch (Exception e) {
			//System.out.println("this is the expected exception");
			//e.printStackTrace();
			assertEquals(1, 1);

			return;
		}

		fail("Did not catch expected exception");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testBug839() throws IOException {
		Set<String> nodes = new HashSet<String>();
		nodes.add("n1");
		nodes.add("n2");
		nodes.add("n3");
		nodes.add("n4");
		nodes.add("n5");

		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/bug_839.sif");

		// check that all nodes exist
		Iterator it = cytoNetwork.nodesIterator();

		while (it.hasNext()) {
			Node n = (Node) it.next();
			assertTrue("checking node " + n.getIdentifier(), nodes.contains(n.getIdentifier()));
		}

		Set<String> edges = new HashSet<String>();
		edges.add(Cytoscape.createEdgeIdentifier("n1", "activates", "n2"));
		edges.add(Cytoscape.createEdgeIdentifier("n1", "activates", "n4"));
		edges.add(Cytoscape.createEdgeIdentifier("n1", "activates", "n5"));
		edges.add(Cytoscape.createEdgeIdentifier("n2", "activates", "n1"));
		edges.add(Cytoscape.createEdgeIdentifier("n2", "activates", "n5"));
		edges.add(Cytoscape.createEdgeIdentifier("n3", "inhibits", "n3"));
		edges.add(Cytoscape.createEdgeIdentifier("n3", "inhibits", "n4"));
		edges.add(Cytoscape.createEdgeIdentifier("n3", "inhibits", "n5"));
		edges.add(Cytoscape.createEdgeIdentifier("n4", "activates", "n1"));
		edges.add(Cytoscape.createEdgeIdentifier("n4", "activates", "n2"));
		edges.add(Cytoscape.createEdgeIdentifier("n4", "activates", "n4"));
		edges.add(Cytoscape.createEdgeIdentifier("n5", "activates", "n1"));
		edges.add(Cytoscape.createEdgeIdentifier("n5", "activates", "n4"));
		edges.add(Cytoscape.createEdgeIdentifier("n5", "activates", "n5"));

		it = cytoNetwork.edgesIterator();

		while (it.hasNext()) {
			Edge e = (Edge) it.next();
			assertTrue("checking edge " + e.getIdentifier(), edges.contains(e.getIdentifier()));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testgetCyEdgeWithStrings() throws IOException {
		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/directedGraph.sif");

		assertEquals(2, cytoNetwork.getNodeCount());
		assertEquals(4, cytoNetwork.getEdgeCount());

		String en1 = Cytoscape.createEdgeIdentifier("a", "pp", "b");

		// edge should exist in network already
		Edge ce1 = Cytoscape.getCyEdge("a", en1, "b", "pp");
		assertNotNull(ce1);

		Edge ce1_again = Cytoscape.getCyEdge("a", en1, "b", "pp");
		assertTrue(ce1 == ce1_again);

		// edge should be created
		String en2 = Cytoscape.createEdgeIdentifier("a", "xx", "b");
		Edge ce2 = Cytoscape.getCyEdge("a", en2, "b", "pp");
		assertNotNull(ce2);

		// should create a different edge because of directedness
		String en3 = Cytoscape.createEdgeIdentifier("b", "pp", "a");
		Edge ce3 = Cytoscape.getCyEdge("b", en3, "a", "pp");
		assertTrue(ce1 != ce3);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void testgetCyEdgeWithNodes() throws IOException {
		cytoNetwork = Cytoscape.createNetworkFromFile("src/test/resources/testData/directedGraph.sif");

		Node a = Cytoscape.getCyNode("a");
		Node b = Cytoscape.getCyNode("b");
		Node c = Cytoscape.getCyNode("c", true);
		String attr = Semantics.INTERACTION;

		// test directed edges
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(b, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, a, attr, "pp", false, true));
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "pd", false, true));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "pd", false, true));

		// test undirectedness -- directed edge mustn't be returned as undirected edge
		assertNull(Cytoscape.getCyEdge(b, a, attr, "pd", false, false));

		// test non-existent edge
		assertNull(Cytoscape.getCyEdge(a, c, attr, "pp", false, true));

		// test bad attr_value
		assertNull(Cytoscape.getCyEdge(a, b, attr, "xx", false, true));

		// test create node
		assertNotNull(Cytoscape.getCyEdge(a, c, attr, "pd", true, true));

		// make sure we got the node we created
		assertNotNull(Cytoscape.getCyEdge(a, c, attr, "pd", false, true));
	}
	/**
	 *  Check that edge directedness is handled correctly:
	 *  Creating undirected edges, and value of .isDirected()
	 */
	public void testEdgeDirectionality() {
		// create nodes
		Node a = Cytoscape.getCyNode("from", true);
		Node b = Cytoscape.getCyNode("to", true);

		String attr = Semantics.INTERACTION;

		// assert that edge doesn't exist yet
		assertNull(Cytoscape.getCyEdge(a, b, attr, "u", false, false));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "u", false, false));

		// create undirected edge 
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "u", true, false) );
		// check that it was really created
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "u", false, false) );
		// check that it is visible in reverse direction
		assertNotNull(Cytoscape.getCyEdge(b, a, attr, "u", false, false) );

		Edge e = Cytoscape.getCyEdge(a, b, attr, "u", false, false);
		assertFalse("edge created is of correct directionality", e.isDirected());
		e = Cytoscape.getCyEdge(b, a, attr, "u", false, false);
		assertFalse("edge created is of correct directionality", e.isDirected());

		// check that for undirected edges, edge in reverse direction is the same
		Edge e2 = Cytoscape.getCyEdge(b, a, attr, "u", false, false);
		assertFalse(e2.isDirected());
		assertTrue(e == e2);
		assertTrue(e.getIdentifier() == e2.getIdentifier());

		// check isDirected() flag for directed edges:
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "d", true, true) );
		e = Cytoscape.getCyEdge(a, b, attr, "d", false, true);
		assertTrue("edge created is of correct directionality", e.isDirected());

		assertNull("directed edge is not visible in reverse dir.", Cytoscape.getCyEdge(b, a, attr, "d", false, true) );
	}

	/**
	 * This tests that getCyEdge() will allways return edge with given directionality;
	 * 
	 */
	public void testGetCyEdgeStrictness() {
		// create nodes
		Node a = Cytoscape.getCyNode("from", true);
		Node b = Cytoscape.getCyNode("to", true);

		String attr = Semantics.INTERACTION;
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "u", true, false));
		
		// the tests: no directed edge exsists:
		Edge test_edge = Cytoscape.getCyEdge(a, b, attr, "u", false, true);
		assertNull("didn't get an edge", test_edge);
		
		test_edge = Cytoscape.getCyEdge(b, a, attr, "u", false, true);
		assertNull("didn't get an edge", test_edge);
		
		// undirected edge, in other direction:
		test_edge = Cytoscape.getCyEdge(b, a, attr, "u", false, false);
		assertNotNull("got get an edge", test_edge);
		assertFalse("edge is of correct directionality", test_edge.isDirected());
	}
	
	/**
	 *  Check that edge directedness is handled correctly:
	 *  parallel undirected and directed edges must be distinct
	 */
	public void testParallelEdgesWithDifferentDirectionality() {
		// create nodes
		Node a = Cytoscape.getCyNode("one", true);
		Node b = Cytoscape.getCyNode("two", true);

		String attr = Semantics.INTERACTION;

		// assert that edge doesn't exist yet
		assertNull(Cytoscape.getCyEdge(a, b, attr, "u", false, false));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "u", false, false));
		assertNull(Cytoscape.getCyEdge(a, b, attr, "u", false, true));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "u", false, true));

		// create undirected edge:
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "u", true, false) );
		// create directed edge over it: (same source, target, and interaction)
		assertNotNull(Cytoscape.getCyEdge(a, b, attr, "u", true, true) );
		// test directionality:
		Edge e1 = Cytoscape.getCyEdge(a, b, attr, "u", false, false);
		assertFalse("edge is created with correct directionality", e1.isDirected());

		Edge e2 = Cytoscape.getCyEdge(a, b, attr, "u", false, true);
		assertTrue("edge is created with correct directionality", e2.isDirected());

		assertTrue("the two edges are different", e1 != e2);
		assertTrue("the two edges are different", e1.getIdentifier() != e2.getIdentifier());

		// check existence in reverse direction:
		assertNotNull(Cytoscape.getCyEdge(b, a, attr, "u", false, false));
		assertNull(Cytoscape.getCyEdge(b, a, attr, "u", false, true));
	}
}
