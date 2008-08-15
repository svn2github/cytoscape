/**
 * 
 */
package org.cytoscape.model.network;

import junit.framework.TestCase;
import org.cytoscape.impl.FRootGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CyNetworkTest extends TestCase {
	private CyNetwork network;
	private int defaultNodeSetSize = 10;
	private RootGraph root;
	private List<CyNode> nodes;
	private List<CyEdge> edges;

	private int nodeIndex0;
	private int nodeIndex1;
	private int nodeIndex2;
	private int nodeIndex3;

	protected void setUp() throws Exception {
		super.setUp();
		
		root = new FRootGraph();

		nodes = new ArrayList<CyNode>();
		for (int i=0; i<defaultNodeSetSize; i++) 
			nodes.add(root.getNode(root.createNode()));	

		nodeIndex0 = nodes.get(0).getRootGraphIndex();
		nodeIndex1 = nodes.get(1).getRootGraphIndex();
		nodeIndex2 = nodes.get(2).getRootGraphIndex();
		nodeIndex3 = nodes.get(3).getRootGraphIndex();

		edges = new ArrayList<CyEdge>();
		CyNode lastNode = null;
		for ( CyNode node : nodes ) {
			if (lastNode != null) 
				edges.add(root.getEdge(root.createEdge(lastNode, node, true)));
			lastNode = node;
		}

		network = root.createGraphPerspective(nodes, edges);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateNetwork() {
		assertNotNull(network);
		assertEquals(network.getNodeCount(), defaultNodeSetSize);
		assertEquals(network.getEdgeCount(), defaultNodeSetSize-1);
		System.err.println("Should be creating the network the way it's done in Fing!");
	}
	
	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setTitle(java.lang.String)}
	 * and {@link cytoscape.giny.GraphPerspective#getTitle()}.
	 */
	public void testSetGetTitle() {
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
		Collection<CyNode> nnodes = new ArrayList<CyNode>();
		nnodes.add( root.getNode(root.createNode()) );
		nnodes.add( root.getNode(root.createNode()) );
		nnodes.add( root.getNode(root.createNode()) );
		Collection<CyEdge> eedges = new ArrayList<CyEdge>();
		CyNode lastNode = null;
		for ( CyNode node : nnodes ) {
			if (lastNode != null) 
				eedges.add(root.getEdge(root.createEdge(lastNode, node, true)));
			lastNode = node;
		}
		
		CyNetwork appNet = root.createGraphPerspective(nnodes, eedges);
		assertNotNull(appNet);
		assertNotSame(appNet, network);
		
		network.appendNetwork(appNet);
		
		assertEquals(defaultNodeSetSize+3, network.getNodeCount());
		assertEquals(defaultNodeSetSize+1, network.getEdgeCount());
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
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedNodeState(Collection, boolean)}.
	 */
	public void testSetSelectedNodeStateCollectionBoolean() {
		network.setSelectedNodeState(this.getExistingNodes(3), true);
		assertEquals(3, network.getSelectedNodes().size());
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedNodeState(cytoscape.Node, boolean)}.
	 */
	public void testSetSelectedNodeStateNodeBoolean() {
		network.setSelectedNodeState(root.getNode(nodeIndex0), true);
		assertEquals(network.getSelectedNodes().size(), 1);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#setSelectedEdgeState(Collection, boolean)}.
	 */
	public void donttestSetSelectedEdgeStateCollectionBoolean() {
		network.unselectAllNodes();
		network.unselectAllEdges();
		
		network.setSelectedNodeState(this.getExistingNodes(3), true);
		Set<CyNode> selectedNodes = network.getSelectedNodes();
		
		for (CyNode node: selectedNodes) {
			assertTrue(network.containsNode(node));
		}
		
		Collection<CyEdge> edges = this.getExistingEdges(selectedNodes);
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
		network.setSelectedNodeState(this.getExistingNodes(2), true);
		Set<CyNode> selectedNodes = network.getSelectedNodes();
		
		assertEquals(this.getExistingEdges(selectedNodes).size(), 1);
		Iterator<CyEdge> edgeI = this.getExistingEdges(selectedNodes).iterator();
		while (edgeI.hasNext()) {
			CyEdge edge = edgeI.next();
			network.setSelectedEdgeState(edge, true);
		}
		assertEquals(network.getSelectedEdges().size(), 1);
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#isSelected(cytoscape.Node)}.
	 */
	public void testIsSelectedNode() {
		CyNode node = root.getNode(nodeIndex0);
		network.setSelectedNodeState(node, true);
		assertTrue(network.isSelected(node));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#isSelected(cytoscape.Edge)}.
	 */
	public void testIsSelectedEdge() {
		CyEdge edge = edges.get(0);
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
		CyNode newNode = root.getNode(root.createNode());
		assertNotNull(network.addNode(newNode.getRootGraphIndex()));
		assertTrue(network.containsNode(newNode));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addNode(cytoscape.Node)}.
	 */
	public void testAddNodeNode() {
		CyNode newNode = root.getNode(root.createNode());
		assertNotNull(network.addNode(newNode));
		assertTrue(network.containsNode(newNode));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#removeNode(int, boolean)}.
	 */
	public void testRemoveNode() {
		CyNode node = root.getNode(nodeIndex0);
		assertNotNull(node);
		assertTrue(network.removeNode(node.getRootGraphIndex(), true));
	}


	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addEdge(int)}.
	 */
	public void testAddEdgeInt() {
		CyEdge edge = root.getEdge( root.createEdge( root.getNode(nodeIndex0), root.getNode(nodeIndex3), true) );
		assertNotNull(edge);
		assertNotNull(network.addEdge(edge.getRootGraphIndex()));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#addEdge(cytoscape.Edge)}.
	 */
	public void testAddEdgeEdge() {
		CyEdge edge = root.getEdge( root.createEdge( root.getNode(nodeIndex0), root.getNode(nodeIndex3), true ) );
		assertNotNull(edge);
		assertNotNull(network.addEdge(edge));
	}

	/**
	 * Test method for {@link cytoscape.giny.GraphPerspective#removeEdge(int, boolean)}.
	 */
	public void testRemoveEdge() {
		CyEdge edge = root.getEdge( root.createEdge( root.getNode(nodeIndex0), root.getNode(nodeIndex1), true) );
		assertNotNull(edge);
		assertTrue(network.removeEdge(edge.getRootGraphIndex(), true));
	}


	private Collection<CyNode> getExistingNodes(int size) {
		List<CyNode> enodes = new ArrayList<CyNode>();
		for (int i=0; i<size; i++) {
			enodes.add( nodes.get(i) );	
		}	
		return enodes;
	}
	
	private Collection<CyEdge> getExistingEdges(Collection<CyNode> Nodes) {
		List<CyEdge> xdges = new ArrayList<CyEdge>();
		CyNode l = null;
		for ( CyNode n : Nodes ) {
			if ( l != null ) {
				int[] edgeA = root.getEdgeIndicesArray(l.getRootGraphIndex(),n.getRootGraphIndex(),true,true);
				for (int e : edgeA ) 
					xdges.add(root.getEdge(e));
			}
			l = n;
		}
		return xdges;
	}
}
