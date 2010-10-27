package org.cytoscape.view.presentation;


import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

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
		
		final RenderingEngineFactory<CyNetwork> factory = createMock(RenderingEngineFactory.class);
		final RenderingEngine<CyNetwork> engine = createMock(RenderingEngine.class);
		final PresentationCreatedEvent createdEvent = new PresentationCreatedEvent(factory, engine);
		assertEquals(factory, createdEvent.getSource());
		assertEquals(engine, createdEvent.getRenderingEngine());
		
		final JComponent deletePanel = new JPanel();
		final PresentationDestroyedEvent destroyedEvent = new PresentationDestroyedEvent(deletePanel, engine);
		assertEquals(deletePanel, destroyedEvent.getSource());
		assertEquals(engine, destroyedEvent.getRenderingEngine());
	}

}
