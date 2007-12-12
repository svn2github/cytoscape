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
public class NetworkTest {

	private Network network;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		network = new Network();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link profile.graph.Network#Network()}.
	 */
	@Test
	public void testNetwork() {
		assertNotNull(network);
	}

	/**
	 * Test method for {@link profile.graph.Network#Network(java.lang.String)}.
	 */
	@Test
	public void testNetworkString() {
		Network n = new Network("test");
		assertNotNull(n);
		assertEquals(n.getName(), "test");
	}

	/**
	 * Test method for {@link profile.graph.Network#getID()}.
	 */
	@Test
	public void testGetID() {
		assertTrue(network.getIdentifier() >= 0);
	}


	/**
	 * Test method for {@link profile.graph.Network#addNode(java.lang.String)}.
	 */
	@Test
	public void testAddNode() {
		Node nA = network.addNode("A");
		assertNotNull(nA);
		assertEquals(network.getNodes().size(), 1);
	}

	/**
	 * Test method for {@link profile.graph.Network#removeNode(profile.graph.Node)}.
	 */
	@Test
	public void testRemoveNode() {
		Node nA = network.addNode("A");
		assertNotNull(nA);
		assertEquals(network.getNodes().size(), 1);
		network.removeNode(nA);
		assertEquals(network.getNodes().size(), 0);
	}

	/**
	 * Test method for {@link profile.graph.Network#getNodes(int, int)}.
	 */
	@Test
	public void testGetNodesIntInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link profile.graph.Network#getNodes(profile.graph.Node, profile.graph.Node)}.
	 */
	@Test
	public void testGetNodesNodeNode() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link profile.graph.Network#getNodes()}.
	 */
	@Test
	public void testGetNodes() {
		Node nA = network.addNode("A");
		Node nB = network.addNode("B");
		Node nC = network.addNode("C");
		boolean directed = false;
		nA.addEdge(nB, directed);
		nB.addEdge(nC, directed);
		nC.addEdge(nA, directed);
		
		assertEquals(network.getNodes().size(), 3);
	}


}
