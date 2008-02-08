/**
 * 
 */
package cytoscape.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.*;
import cytoscape.data.Semantics;
import junit.framework.TestCase;

public class CyNetworkTest extends TestCase {
	private String title = "My Network";
	private GraphPerspective network;
	private int defaultNodeSetSize = 10;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		java.util.Collection<Node> nodes = this.getNodes(defaultNodeSetSize);
		java.util.Collection<Edge> edges = this.getEdges(nodes);
		network = Cytoscape.createNetwork(nodes, edges, title);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Cytoscape.destroyNetwork(network);
	}

	public void testCreateNetwork() {
		//GraphPerspective net = new GraphPerspective(new CytoscapeFingRootGraph(), );
		assertNotNull(network);
		assertEquals(network.getNodeCount(), defaultNodeSetSize);
		assertEquals(network.getEdgeCount(), defaultNodeSetSize-1);
		System.err.println("Should be creating the network the way it's done in Fing!");
	}
	
	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#getTitle()}.
	 */
	public void testGetTitle() {
		assertEquals(network.getTitle(), title);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setTitle(java.lang.String)}.
	 */
	public void testSetTitle() {
		network.setTitle("foobar");
		assertEquals(network.getTitle(), "foobar");
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#getIdentifier()}.
	 */
	public void testGetIdentifier() {
		assertNotNull(network.getIdentifier());
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setIdentifier(java.lang.String)}.
	 */
	public void testSetIdentifier() {
		String id = "12345";
		network.setIdentifier(id);
		assertEquals(network.getIdentifier(), id);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#appendNetwork(cytoscape.GraphPerspective)}.
	 */
	public void testAppendNetwork() {
		java.util.Collection<Node> nodes = new java.util.ArrayList<Node>();
		nodes.add( Cytoscape.getCyNode("foobar", true) );
		nodes.add( Cytoscape.getCyNode("blat", true) );
		nodes.add( Cytoscape.getCyNode("some name", true) );
		java.util.Collection<Edge> edges = this.getEdges(nodes);
		
		GraphPerspective appNet = Cytoscape.createNetwork(nodes, edges, "My network");
		assertNotNull(appNet);
		assertNotSame(appNet, network);
		
		network.appendNetwork(appNet);
		
		assertEquals(network.getNodeCount(), this.defaultNodeSetSize+3);
		assertEquals(network.getEdgeCount(), this.defaultNodeSetSize+1);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#selectAllNodes()}.
	 */
	public void testSelectAllNodes() {
		network.selectAllNodes();
		assertNotNull(network.getSelectedNodes());
		assertEquals(network.getSelectedNodes().size(), network.getNodeCount());
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#selectAllEdges()}.
	 */
	public void testSelectAllEdges() {
		network.selectAllEdges();
		assertNotNull(network.getSelectedEdges());
		assertEquals(network.getSelectedEdges().size(), network.getEdgeCount());
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#unselectAllNodes()}.
	 */
	public void testUnselectAllNodes() {
		network.selectAllNodes();
		assertNotNull(network.getSelectedNodes());
		assertEquals(network.getSelectedNodes().size(), network.getNodeCount());

		network.unselectAllNodes();
		assertEquals(network.getSelectedNodes().size(), 0);
		
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#unselectAllEdges()}.
	 */
	public void testUnselectAllEdges() {
		network.selectAllEdges();
		assertNotNull(network.getSelectedEdges());
		assertEquals(network.getSelectedEdges().size(), network.getEdgeCount());

		network.unselectAllEdges();
		assertEquals(network.getSelectedEdges().size(), 0);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedNodeState(java.util.Collection, boolean)}.
	 */
	public void testSetSelectedNodeStateCollectionBoolean() {
		network.setSelectedNodeState(this.getNodes(3), true);
		assertEquals(network.getSelectedNodes().size(), 3);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedNodeState(cytoscape.Node, boolean)}.
	 */
	public void testSetSelectedNodeStateNodeBoolean() {
		network.setSelectedNodeState(Cytoscape.getCyNode("1"), true);
		assertEquals(network.getSelectedNodes().size(), 1);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedEdgeState(java.util.Collection, boolean)}.
	 */
	public void testSetSelectedEdgeStateCollectionBoolean() {
		network.unselectAllNodes();
		network.unselectAllEdges();
		
		network.setSelectedNodeState(this.getNodes(3), true);
		java.util.Set<Node> selectedNodes = network.getSelectedNodes();
		
		for (Node node: selectedNodes) {
			assertTrue(network.containsNode(node));
		}
		
		java.util.Collection<Edge> edges = this.getEdges(selectedNodes);
		assertEquals(2, edges.size());
		
		// XXX Why does this fix the getSelected edges test???
//		for (Edge edge: edges) {
//			assertTrue(network.containsEdge(edge));
//		}
//		
		network.setSelectedEdgeState(edges, true);
		// TODO WTF?
//		assertEquals(edges.size(), network.getSelectedEdges().size());
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedEdgeState(cytoscape.Edge, boolean)}.
	 */
	public void testSetSelectedEdgeStateEdgeBoolean() {
		network.setSelectedNodeState(this.getNodes(2), true);
		java.util.Set<Node> selectedNodes = network.getSelectedNodes();
		
		assertEquals(this.getEdges(selectedNodes).size(), 1);
		java.util.Iterator<Edge> edgeI = this.getEdges(selectedNodes).iterator();
		while (edgeI.hasNext()) {
			Edge edge = edgeI.next();
			network.setSelectedEdgeState(edge, true);
		}
		assertEquals(network.getSelectedEdges().size(), 1);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#isSelected(cytoscape.Node)}.
	 */
	public void testIsSelectedNode() {
		Node node = Cytoscape.getCyNode("1");
		network.setSelectedNodeState(node, true);
		assertTrue(network.isSelected(node));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#isSelected(cytoscape.Edge)}.
	 */
	public void testIsSelectedEdge() {
		Node node_1 = Cytoscape.getCyNode("1");
		Node node_2 = Cytoscape.getCyNode("2");
		
		Edge edge = Cytoscape.getCyEdge(node_1, node_2, Semantics.INTERACTION, "test", false);
		assertNotNull(edge);
		
		network.setSelectedEdgeState(edge, true);
		assertTrue(network.isSelected(edge));
		
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#getSelectedNodes()}.
	 */
	public void testGetSelectedNodes() {
		network.selectAllNodes();
		assertNotNull(network.getSelectedNodes());
		assertEquals(network.getSelectedNodes().size(), this.defaultNodeSetSize);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#getSelectedEdges()}.
	 */
	public void testGetSelectedEdges() {
		network.selectAllEdges();
		assertNotNull(network.getSelectedEdges());
		assertEquals(network.getSelectedEdges().size(), this.defaultNodeSetSize-1);
	}



	/**
	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addNode(int)}.
	 */
	public void testAddNodeInt() {
		Node newNode = Cytoscape.getCyNode("123", true);
		assertNotNull(network.addNode(newNode.getRootGraphIndex()));
		assertTrue(network.containsNode(newNode));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addNode(cytoscape.Node)}.
	 */
	public void testAddNodeNode() {
		Node newNode = Cytoscape.getCyNode("321", true);
		assertNotNull(network.addNode(newNode));
		assertTrue(network.containsNode(newNode));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#removeNode(int, boolean)}.
	 */
	public void testRemoveNode() {
		Node node = Cytoscape.getCyNode("1");
		assertNotNull(node);
		assertTrue(network.removeNode(node.getRootGraphIndex(), true));
	}


	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addEdge(int)}.
	 */
	public void testAddEdgeInt() {
		Edge edge = Cytoscape.getCyEdge( Cytoscape.getCyNode("1"), Cytoscape.getCyNode("4"), Semantics.INTERACTION, "test", true);
		assertNotNull(edge);
		assertNotNull(network.addEdge(edge.getRootGraphIndex()));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addEdge(cytoscape.Edge)}.
	 */
	public void testAddEdgeEdge() {
		Edge edge = Cytoscape.getCyEdge( Cytoscape.getCyNode("1"), Cytoscape.getCyNode("4"), Semantics.INTERACTION, "test", true);
		assertNotNull(edge);
		assertNotNull(network.addEdge(edge));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#removeEdge(int, boolean)}.
	 */
	public void testRemoveEdge() {
		Edge edge = Cytoscape.getCyEdge( Cytoscape.getCyNode("1"), Cytoscape.getCyNode("2"), Semantics.INTERACTION, "test", true);
		assertNotNull(edge);
		assertTrue(network.removeEdge(edge.getRootGraphIndex(), true));
	}


	public void testSetTitleEvent() {
		String InitialTitle = "My Network";
		String NewTitle = "Foobar";
		GraphPerspective network = Cytoscape.createNetwork(InitialTitle);
		TitleListener tl =  new TitleListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(tl);
		network.setTitle(NewTitle);
		PropertyChangeEvent event = tl.getEvent();
		assertEquals(event.getPropertyName(), Cytoscape.NETWORK_TITLE_MODIFIED);
		assertEquals( ((CyNetworkTitleChange)event.getOldValue()).getNetworkIdentifier(), 
					  ((CyNetworkTitleChange)event.getNewValue()).getNetworkIdentifier());
		assertEquals( ((CyNetworkTitleChange)event.getOldValue()).getNetworkTitle(), InitialTitle);
		assertEquals(  ((CyNetworkTitleChange)event.getNewValue()).getNetworkTitle(), NewTitle);

	}
	
	/* ----------------------------------------------------------------- */
	// use only for testSetTitleEvent()
	private class TitleListener implements PropertyChangeListener {
		private PropertyChangeEvent pcEvent;
		
		public void propertyChange(PropertyChangeEvent event) {
			pcEvent = event;
		}
		
		public PropertyChangeEvent getEvent() {
			return pcEvent;
		}
	}

	
	private java.util.Collection<Node> getNodes(int size) {
		java.util.List<Node> Nodes = new java.util.ArrayList<Node>();
		for (int i=0; i<size; i++) {
			Nodes.add(Cytoscape.getCyNode(Integer.toString(i), true));	
		}
		return Nodes;
	}

	private java.util.Collection<Edge> getEdges(java.util.Collection<Node> Nodes) {
		java.util.List<Edge> Edges = new java.util.ArrayList<Edge>();
		
		java.util.Iterator<Node> nodeI = Nodes.iterator();
		Node LastNode = null;
		while (nodeI.hasNext()) {
			Node Node = nodeI.next();
			if (LastNode != null) 
				Edges.add(Cytoscape.getCyEdge(LastNode, Node, Semantics.INTERACTION, "test", true));

			LastNode = Node;
		}
		return Edges;
	}

	
}
