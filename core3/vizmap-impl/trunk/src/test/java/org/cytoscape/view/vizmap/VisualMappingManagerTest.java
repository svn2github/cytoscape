package org.cytoscape.view.vizmap;


import static org.mockito.Mockito.*;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.vizmap.internal.VisualMappingManagerImpl;
import org.junit.After;
import org.junit.Before;

public class VisualMappingManagerTest extends AbstractVisualMappingManagerTest {

	
	
	@Before
	public void setUp() throws Exception {
		final CyEventHelper eventHelper = mock(CyEventHelper.class);
		defaultStyle = mock(VisualStyle.class);
		when(defaultStyle.getTitle()).thenReturn("DEFAULT");
		
		vmm = new VisualMappingManagerImpl(eventHelper);
		
		// In the real implementations, this will be done through OSGi.
		((VisualMappingManagerImpl)vmm).addDefaultStyle(defaultStyle, null);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	

}
