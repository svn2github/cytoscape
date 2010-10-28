package org.cytoscape.view.vizmap;


import static org.mockito.Mockito.mock;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.vizmap.internal.VisualMappingManagerImpl;
import org.junit.After;
import org.junit.Before;

public class VisualMappingManagerTest extends AbstractVisualMappingManagerTest {

	
	
	@Before
	public void setUp() throws Exception {
		final CyEventHelper eventHelper = mock(CyEventHelper.class);
		vmm = new VisualMappingManagerImpl(eventHelper);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	

}
