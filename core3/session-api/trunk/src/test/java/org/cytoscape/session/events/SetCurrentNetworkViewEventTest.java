package org.cytoscape.session.events;

import static org.junit.Assert.*;

import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class SetCurrentNetworkViewEventTest {

	@Test
	public void testGoodGetNetworkView() {
		CyApplicationManager source = mock(CyApplicationManager.class);
		CyNetworkView n = mock(CyNetworkView.class);
		SetCurrentNetworkViewEvent e = new SetCurrentNetworkViewEvent(source,n);
		assertNotNull( e.getNetworkView() );
		assertEquals( n,e.getNetworkView() );
	}

	@Test
	public void testNullGetNetworkView() {
		CyApplicationManager source = mock(CyApplicationManager.class);
		SetCurrentNetworkViewEvent e = new SetCurrentNetworkViewEvent(source,null);
		assertNull( e.getNetworkView() );
	}

}
