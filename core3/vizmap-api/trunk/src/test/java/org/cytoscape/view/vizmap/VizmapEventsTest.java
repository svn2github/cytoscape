package org.cytoscape.view.vizmap;


import org.cytoscape.view.vizmap.events.VisualStyleChangedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleCreatedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleDestroyedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class VizmapEventsTest {

	private VisualStyle style;
	
	@Before
	public void setUp() throws Exception {
		style = createMock(VisualStyle.class);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testVisualStyleCreatedEvent() {
		final VisualStyleCreatedEvent event = new VisualStyleCreatedEvent(style);
		assertNotNull(event);
		assertEquals(style, event.getSource());
	}
	
	@Test
	public void testVisualStyleDestroyedEvent() {
		final VisualStyleDestroyedEvent event = new VisualStyleDestroyedEvent(style);
		assertNotNull(event);
		assertEquals(style, event.getSource());
	}
	
	@Test
	public void testVisualStyleChangedEvent() {
		final VisualStyleChangedEvent event = new VisualStyleChangedEvent(style);
		assertNotNull(event);
		assertEquals(style, event.getSource());
	}

}
