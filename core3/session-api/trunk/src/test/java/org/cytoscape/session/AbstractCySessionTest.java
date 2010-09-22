package org.cytoscape.session;

import static org.junit.Assert.*;

import org.cytoscape.session.CySession;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCySessionTest {


	protected CySession session;

	
	@Test
	public void testSessionName() {
		assertNotNull(session);
		assertNotNull(session.getSessionName());
		assertFalse(session.getSessionName().equals(""));
	}
	
	@Test
	public void testGetNetworks() {
		assertNotNull(session);
		assertNotNull(session.getNetworks());
	}
	
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
	public void testGetVisualStyles() {
		assertNotNull(session);
		assertNotNull(session.getVisualStyles());
	}
	
	@Test
	public void testGetProperties() {
		assertNotNull(session);
		assertNotNull(session.getProperties());
		assertNotNull(session.getProperties().get("cytoscape"));
		assertNotNull(session.getProperties().get("vizmap"));
	}
	
	@Test
	public void testGetFilename() {
		assertNotNull(session);
		assertNotNull(session.getFileName());
		assertFalse(session.getSessionName().equals(""));
	}
}
