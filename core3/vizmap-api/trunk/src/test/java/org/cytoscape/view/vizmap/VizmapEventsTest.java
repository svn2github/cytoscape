package org.cytoscape.view.vizmap;


import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.view.vizmap.events.VisualStyleCreatedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleRemovedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VizmapEventsTest {

	private VisualStyle style;
	private VisualStyleFactory factory;
	private VisualMappingManager manager;
	
	@Before
	public void setUp() throws Exception {
		style = createMock(VisualStyle.class);
		factory = createMock(VisualStyleFactory.class);
		manager = createMock(VisualMappingManager.class);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testVisualStyleCreatedEvent() {
		final VisualStyleCreatedEvent event = new VisualStyleCreatedEvent(factory, style);
		assertNotNull(event);
		assertEquals(style, event.getCreatedVisualStyle());
	}
	
	@Test
	public void testVisualStyleDestroyedEvent() {
		final VisualStyleRemovedEvent event = new VisualStyleRemovedEvent(manager, style);
		assertNotNull(event);
		assertEquals(style, event.getRemovedVisualStyle());
	}

}
