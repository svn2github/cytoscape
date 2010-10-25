package org.cytoscape.session.events;


import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyNetwork;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class AbstractNetworkEventTest {
	@Test
	public final void testGetNetwork() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		final CyNetwork network = mock(CyNetwork.class);
		final AbstractNetworkEvent event = new AbstractNetworkEvent(networkManager, Object.class, network);
		assertEquals("Network returned by getNetwork() is *not* the one passed into the constructor!",
			     network, event.getNetwork());
	}

	@Test
	public final void testNullNetworkConstructorFailure() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		try {
			final AbstractNetworkEvent event = new AbstractNetworkEvent(networkManager, Object.class, null);
		} catch (final NullPointerException e) {
			return;
		}

		fail("The expected NullPointerException was *not* triggered!");
	}
}