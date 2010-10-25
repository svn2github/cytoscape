package org.cytoscape.session.events;


import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class AbstractNetworkViewEventTest {
	@Test
	public final void testGetNetwork() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		final CyNetworkView networkView = mock(CyNetworkView.class);
		final AbstractNetworkViewEvent event =
			new AbstractNetworkViewEvent(networkManager, Object.class, networkView);
		assertEquals("Network returned by getNetworkView() is *not* the one passed into the constructor!",
			     networkView, event.getNetworkView());
	}

	@Test
	public final void testNullNetworkConstructorFailure() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		try {
			final AbstractNetworkViewEvent event =
				new AbstractNetworkViewEvent(networkManager, Object.class, null);
		} catch (final NullPointerException e) {
			return;
		}

		fail("The expected NullPointerException was *not* triggered!");
	}
}