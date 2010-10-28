package org.cytoscape.view.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.junit.Test;

public abstract class AbstractCyNetworkViewTest {
	
	private static final int NODE_COUNT = 5;
	private static final int EDGE_COUNT = 8;
	
	protected CyNetwork network;
	protected CyNetworkView  view;
	
	protected  CyNode node1, node2, node3, node4, node5;
	protected  CyEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8; 
	
	
	@Test
	public void testNetworkViewOnlyMethods() throws Exception {
		
		assertNotNull(view.getNodeViews());
		assertEquals(NODE_COUNT, view.getNodeViews().size());
		
		assertNotNull(view.getEdgeViews());
		assertEquals(EDGE_COUNT, view.getEdgeViews().size());
		
		assertNotNull(view.getAllViews());
		assertEquals(NODE_COUNT+EDGE_COUNT+1, view.getAllViews().size());
		
		assertNotNull(view.getNodeView(node1));
		assertNotNull(view.getNodeView(node2));
		assertNotNull(view.getNodeView(node3));
		assertNotNull(view.getNodeView(node4));
		assertNotNull(view.getNodeView(node5));
		
		assertNotNull(view.getEdgeView(edge1));
		assertNotNull(view.getEdgeView(edge2));
		assertNotNull(view.getEdgeView(edge3));
		assertNotNull(view.getEdgeView(edge4));
		assertNotNull(view.getEdgeView(edge5));
		assertNotNull(view.getEdgeView(edge6));
		assertNotNull(view.getEdgeView(edge7));
		assertNotNull(view.getEdgeView(edge8));
	}
	
	@Test
    public void testGetModel() {
		assertNotNull( view.getModel() );
		
		boolean modelTypeTest = false;
		if(view.getModel() instanceof CyNetwork)
			modelTypeTest = true;
		
		assertTrue(modelTypeTest);
		
	}
	
	
	/**
	 * Create a very small network for testing
	 */
	protected void buildNetwork() {
		
		node1 = network.addNode();
		node2 = network.addNode();
		node3 = network.addNode();
		node4 = network.addNode();
		node5 = network.addNode();
		
		edge1 = network.addEdge(node1, node2, true);
		edge2 = network.addEdge(node2, node2, true);
		edge3 = network.addEdge(node3, node4, true);
		edge4 = network.addEdge(node1, node5, true);
		edge5 = network.addEdge(node5, node4, true);
		edge6 = network.addEdge(node3, node2, true);
		edge7 = network.addEdge(node5, node1, true);
		edge8 = network.addEdge(node4, node3, true);
	}
	
	

}
