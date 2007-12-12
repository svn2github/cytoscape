/**
 * 
 */
package profile.graph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author skillcoy
 *
 */
public class NodeTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link profile.graph.Node#Node()}.
	 */
	@Test
	public void testNode() {
		Node n = new Node();
		assertNotNull(n);
	}

	/**
	 * Test method for {@link profile.graph.Node#Node(java.lang.String)}.
	 */
	@Test
	public void testNodeString() {
		Node n = new Node("test");
		assertNotNull(n);
		assertEquals(n.getName(), "test");
	}

	/**
	 * Test method for {@link profile.graph.Node#getID()}.
	 */
	@Test
	public void testGetID() {
		Node n = new Node();
		assertNotNull(n.getIndex());
		assertTrue(n.getIndex() >= 0);
	}

	/**
	 * Test method for {@link profile.graph.Node#addEdge(profile.graph.Edge)}.
	 */
	@Test
	public void testAddEdge() {
		Node nA = new Node("A");
		Node nB = new Node("B");
		
		Edge eAB = nA.addEdge(nB, false);
		
		assertEquals(nA.getEdges().get(0), eAB);
		assertEquals(nB.getEdges().get(0), eAB);
	}

	/**
	 * Test method for {@link profile.graph.Node#removeEdge(profile.graph.Edge)}.
	 */
	@Test
	public void testRemoveEdge() {
		Node nA = new Node("A");
		Node nB = new Node("B");
		Edge eAB = nA.addEdge(nB, false);

		assertEquals(nA.getEdges().get(0), eAB);
		assertEquals(nB.getEdges().get(0), eAB);

		nA.removeEdge(eAB);
		assertEquals(nA.getEdges().size(), 0);
		assertEquals(nB.getEdges().size(), 0);
	}


}
