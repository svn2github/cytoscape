package org.cytoscape.session.events;


import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class NetworkViewAboutToBeDestroyedEventTest {
	@Test
	public final void testGetNetworkView() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		final CyNetworkView networkView = mock(CyNetworkView.class);
		final NetworkViewAboutToBeDestroyedEvent event =
			new NetworkViewAboutToBeDestroyedEvent(networkManager, networkView);
		assertEquals("Network returned by getNetworkView() is *not* the one passed into the constructor!",
			     networkView, event.getNetworkView());
	}

	@Test
	public final void testNullNetworkConstructorFailure() {
		final CyNetworkManager networkManager = mock(CyNetworkManager.class);
		try {
			final NetworkViewAboutToBeDestroyedEvent event =
				new NetworkViewAboutToBeDestroyedEvent(networkManager, null);
		} catch (final NullPointerException e) {
			return;
		}

		fail("The expected NullPointerException was *not* triggered!");
	}
}