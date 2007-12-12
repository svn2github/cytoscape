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
public class EdgeTest {

	private Node source;
	private Node target;
	private Edge edge;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		source = new Node("A");
		target = new Node("B");
		edge = new Edge(source, target);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link profile.graph.Edge#Edge(profile.graph.Node, profile.graph.Node)}.
	 */
	@Test
	public void testEdgeNodeNode() {
		Edge e = new Edge(source, target);
		assertNotNull(e);
	}

	/**
	 * Test method for {@link profile.graph.Edge#Edge(java.lang.String, profile.graph.Node, profile.graph.Node)}.
	 */
	@Test
	public void testEdgeStringNodeNode() {
		Edge e = new Edge("test", source, target);
		assertNotNull(e);
		assertEquals(e.getName(), "test");
		
	}

	/**
	 * Test method for {@link profile.graph.Edge#getID()}.
	 */
	@Test
	public void testGetID() {
		 assertTrue(edge.getIndex() >= 0);
	}

	/**
	 * Test method for {@link profile.graph.Edge#getSource()}.
	 */
	@Test
	public void testGetSource() {
		assertEquals(edge.getSource(), source);
	}

	/**
	 * Test method for {@link profile.graph.Edge#getTarget()}.
	 */
	@Test
	public void testGetTarget() {
		assertEquals(edge.getTarget(), target);
	}

	/**
	 * Test method for {@link profile.graph.Edge#isDirected()}.
	 */
	@Test
	public void testIsDirected() {
		assertFalse(edge.isDirected());
	}

}
