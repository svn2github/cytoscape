package org.cytoscape.session;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public abstract class AbstractCySessionManagerTest {


	protected CySessionManager mgr;

	
	@Test
	public void testGetCurrentSession() {
		assertNotNull(mgr);
		assertNotNull(mgr.getCurrentSession());
	}
	
	@Test
	public void testSetCurrentSession() {
		assertNotNull(mgr);
		CySession session = mock(CySession.class);
		mgr.setCurrentSession(session);
		assertNotNull(mgr.getCurrentSession());
		assertEquals(session,mgr.getCurrentSession());
	}
	
	@Test
	public void testGetCurrentSessionState() {
		assertNotNull(mgr);
		assertNotNull(mgr.getCurrentSessionState());
		// TODO not sure what to test here
	}
}
