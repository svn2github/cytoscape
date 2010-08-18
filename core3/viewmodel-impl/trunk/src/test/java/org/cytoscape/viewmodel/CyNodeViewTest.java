package org.cytoscape.viewmodel;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.TestCyNetworkFactory;
import org.cytoscape.view.model.AbstractViewTest;
import org.cytoscape.view.model.internal.ViewImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CyNodeViewTest extends AbstractViewTest<CyNode> {
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		final CyNetwork network = TestCyNetworkFactory.getInstance();
		final CyNode node = network.addNode();
		
		final CyEventHelper mockHelper = new DummyCyEventHelper();
		
		view = new ViewImpl<CyNode>(node, mockHelper);
		
	}
	

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
    public void testGetModel() {
		assertNotNull( view.getModel() );
		
		boolean modelTypeTest = false;
		if(view.getModel() instanceof CyNode)
			modelTypeTest = true;
		
		assertTrue(modelTypeTest);
		
	}

}
