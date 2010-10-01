package org.cytoscape.session;

import static org.junit.Assert.*;

import org.cytoscape.session.CySession;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCySessionTest {


	protected CySession session;

	@Test
	public void testGetNetworkViews() {
		assertNotNull(session);
		assertNotNull(session.getNetworkViews());
	}

	@Test
	public void testGetTables() {
		assertNotNull(session);
		assertNotNull(session.getTables());
	}
	
	@Test
	public void testGetViewVisualStyleMap() {
		assertNotNull(session);
		assertNotNull(session.getViewVisualStyleMap());
	}
	
	@Test
	public void testGetCytoscapeProperties() {
		assertNotNull(session);
		assertNotNull(session.getCytoscapeProperties());
	}

	@Test
	public void testGetVizmapProperties() {
		assertNotNull(session);
		assertNotNull(session.getVizmapProperties());
	}

	@Test
	public void testGetDesktopProperties() {
		assertNotNull(session);
		assertNotNull(session.getDesktopProperties());
	}

	@Test
	public void testGetPluginFileListMap() {
		assertNotNull(session);
		assertNotNull(session.getPluginFileListMap());
	}
}
