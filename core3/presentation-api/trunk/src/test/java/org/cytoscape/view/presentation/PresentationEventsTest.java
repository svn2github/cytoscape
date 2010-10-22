package org.cytoscape.view.presentation;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.presentation.events.PresentationCreatedEvent;
import org.cytoscape.view.presentation.events.PresentationDestroyedEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PresentationEventsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testEvents() {
		
		final RenderingEngine<CyNetwork> engine = createMock(RenderingEngine.class);
		final PresentationCreatedEvent createdEvent = new PresentationCreatedEvent(engine);
		assertEquals(engine, createdEvent.getSource());
		
		final PresentationDestroyedEvent destroyedEvent = new PresentationDestroyedEvent(engine);
		assertEquals(engine, destroyedEvent.getSource());
	}

}
