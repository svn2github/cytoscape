package org.cytoscape.viewmodel;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.TestCyNetworkFactory;
import org.cytoscape.view.model.AbstractCyNetworkViewTest;
import org.cytoscape.view.model.internal.NetworkViewImpl;
import org.junit.After;
import org.junit.Before;


public class CyNetworkViewTest extends AbstractCyNetworkViewTest {
	
	@Before
	public void setUp() throws Exception {
		network = TestCyNetworkFactory.getInstance();
		buildNetwork();
		final CyEventHelper mockHelper = new DummyCyEventHelper();
		view = new NetworkViewImpl(network, mockHelper);
	}
	

	@After
	public void tearDown() throws Exception {
	}
	
	

}
