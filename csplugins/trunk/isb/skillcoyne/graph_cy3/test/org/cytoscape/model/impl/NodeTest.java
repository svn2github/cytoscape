/**
 * 
 */
package org.cytoscape.model.impl;

import org.cytoscape.model.*;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author skillcoy
 *
 */
public class NodeTest {

	private CyNode node;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		node = new CyNodeImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#Node()}.
	 */
	@Test
	public void testNode() {
		assertNotNull(node);
	}

	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#getIndex()}.
	 */
	@Test
	public void testGetIndex() {
		assertTrue(node.getIndex() >= 0);
	}

	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#visited(boolean)}.
	 */
	@Test
	public void testVisited() {
		assertFalse(node.hasBeenVisited());
		node.visited(true);
		assertTrue(node.hasBeenVisited());
	}


	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#connectTo(org.cytoscape.model.CyNode, boolean)}.
	 */
	@Test
	public void testConnectTo() {
		for (int i=0; i<100; i++) {
			CyNode n = new CyNodeImpl();
			node.connectTo(n, true);
			assertEquals(node.getEdges().size(), i+1);
		}
		
		assertEquals(node.getEdges().size(), 100);
	}

	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#removeEdge(org.cytoscape.model.CyEdge)}.
	 */
	@Test
	public void testRemoveEdge() {
		CyNode A = new CyNodeImpl();
		CyEdge edge = node.connectTo(A, false);
		assertNotNull(node.getEdges());
		assertNotNull(A.getEdges());
		
		assertEquals(node.getEdges().get(0), edge);
		
		CyEdge eA = A.getEdges().get(0);
		
		assertEquals(eA.getConnectedNode(), node);
		assertEquals(edge.getConnectedNode(), A);
		
		node.removeEdge(edge);
		
		assertEquals(node.getEdges().size(), 0);
		assertEquals(A.getEdges().size(), 0);
		
	}


	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#setAttributes(org.cytoscape.model.Attribute)}.
	 */
	@Test
	public void testSetAttributes() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.cytoscape.model.impl.Node#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {
		//fail("Not yet implemented"); // TODO
	}

}
