package org.cytoscape.view.vizmap;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.cytoscape.view.model.CyNetworkView;
import org.junit.Test;

public abstract class AbstractVisualMappingManagerTest {

	protected VisualMappingManager vmm;

	@Test
	public void testVisualMappingManager() {
		assertNotNull(vmm);
	}

	@Test
	public void testGetAndSetVisualStyle() {
		final VisualStyle style1 = mock(VisualStyle.class);
		final VisualStyle style2 = mock(VisualStyle.class);
		
		final CyNetworkView view1 = mock(CyNetworkView.class);
		final CyNetworkView view2 = mock(CyNetworkView.class);
		
		assertNotNull(vmm.getAllVisualStyles());
		assertEquals(0, vmm.getAllVisualStyles().size());
		
		vmm.setVisualStyle(style1, view1);
		vmm.addVisualStyle(style2);
		vmm.setVisualStyle(style2, view2);
		
		assertEquals(2, vmm.getAllVisualStyles().size());
	}


	@Test
	public void testAddAndRemoveVisualStyle() {
		
		int originalSize = vmm.getAllVisualStyles().size();
		
		final VisualStyle style1 = mock(VisualStyle.class);
		final VisualStyle style2 = mock(VisualStyle.class);
		
		vmm.addVisualStyle(style1);
		vmm.addVisualStyle(style2);
		assertEquals(originalSize + 2, vmm.getAllVisualStyles().size());
		vmm.removeVisualStyle(style2);
		assertEquals(originalSize + 1, vmm.getAllVisualStyles().size());
		
	}
}
