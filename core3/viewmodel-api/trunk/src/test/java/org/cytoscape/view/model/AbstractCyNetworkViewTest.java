package org.cytoscape.view.model;


import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractCyNetworkViewTest {
	
	private static final int NODE_COUNT = 5;
	private static final int EDGE_COUNT = 8;
	
	private CyNetwork network;
	private CyNetworkView  view;
	private CyNetworkViewFactory factory;
	

	@Before
	public void setUp() throws Exception {
		buildNetwork();
		view = factory.getNetworkView(network);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testNetworkView() throws Exception {
		
	}
	
	
	/**
	 * Create a very small network for testing
	 */
	private void buildNetwork() {
		
		CyNode node1 = network.addNode();
		CyNode node2 = network.addNode();
		CyNode node3 = network.addNode();
		CyNode node4 = network.addNode();
		CyNode node5 = network.addNode();
		
		CyEdge edge1 = network.addEdge(node1, node2, true);
		CyEdge edge2 = network.addEdge(node1, node2, true);
		CyEdge edge3 = network.addEdge(node1, node2, true);
		CyEdge edge4 = network.addEdge(node1, node2, true);
		CyEdge edge5 = network.addEdge(node1, node2, true);
		CyEdge edge6 = network.addEdge(node1, node2, true);
		CyEdge edge7 = network.addEdge(node1, node2, true);
		CyEdge edge8 = network.addEdge(node1, node2, true);
	}

}
