package org.cytoscape.view.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public abstract class AbstractVisualPropertyTest<T> {
	
	protected VisualProperty<T> vp;
	
	protected String id;
	protected T defaultVal;
	protected String displayName;
	protected Class<T> type;
	protected Boolean ignore;
	
	
	@Test
	public void testAbstractVisualProperty() {
		assertNotNull(vp);
	}

	@Test
	public void testGetType() {
		assertEquals(type, vp.getType());
	}

	@Test
	public void testGetDefault() {
		assertEquals(defaultVal, vp.getDefault());
	}

	@Test
	public void testGetIdString() {
		assertEquals(id, vp.getIdString());
	}

	@Test
	public void testGetDisplayName() {
		assertNotNull(vp.getDisplayName());
		assertEquals(displayName, vp.getDisplayName());
	}

	@Test
	public void testIsIgnoreDefault() {
		assertEquals(ignore, vp.shouldIgnoreDefault());
	}
}
