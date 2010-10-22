package org.cytoscape.view.presentation;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.events.PresentationCreatedEvent;
import org.cytoscape.view.presentation.events.PresentationCreatedEventListener;
import org.cytoscape.view.presentation.events.PresentationDestroyedEvent;
import org.cytoscape.view.presentation.events.PresentationDestroyedEventListener;
import org.cytoscape.view.presentation.internal.RenderingEngineManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RenderingEngineManagerTest {
	
	private RenderingEngineManager manager;

	@Before
	public void setUp() throws Exception {
		
		manager = new RenderingEngineManagerImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRenderingEngineManagerImpl() {
		assertNotNull(manager);
	}

	@Test
	public void testGetRendringEngines() {
		
		// First, create mock view models.
		final CyNetworkView networkView1 = createMock(CyNetworkView.class);
		final CyNetworkView networkView2 = createMock(CyNetworkView.class);
		
		final RenderingEngine<CyNetwork> engine1 = createMock(RenderingEngine.class);
		
		final RenderingEngine<CyNetwork> engine2 = createMock(RenderingEngine.class);
		expect(engine2.getViewModel()).andReturn(networkView1).anyTimes();
		replay(engine2);
		final RenderingEngine<CyNetwork> engine3 = createMock(RenderingEngine.class);
		expect(engine3.getViewModel()).andReturn(networkView2).anyTimes();
		replay(engine3);
		
		final Collection<RenderingEngine<?>> engineSet = manager.getRendringEngines(networkView1);
		assertNotNull(engineSet);
		assertEquals(0, engineSet.size());
		
		final PresentationCreatedEvent createdEvent1 = new PresentationCreatedEvent(engine1);
		
		expect(engine1.getViewModel()).andReturn(networkView1).anyTimes();
		replay(engine1);
		((PresentationCreatedEventListener)manager).handleEvent(createdEvent1);

		assertEquals(1, manager.getRendringEngines(networkView1).size());
		assertEquals(engine1, manager.getRendringEngines(networkView1).iterator().next());
		
		
		// Remove from manager
		final PresentationDestroyedEvent destroyEvent = new PresentationDestroyedEvent(engine1);
		((PresentationDestroyedEventListener)manager).handleEvent(destroyEvent);
		
		assertEquals(0, manager.getRendringEngines(networkView1).size());
		assertTrue(manager.getRendringEngines(networkView1) instanceof Set);
		
		// Add multiple engines
		final PresentationCreatedEvent createdEvent2 = new PresentationCreatedEvent(engine2);
		final PresentationCreatedEvent createdEvent3 = new PresentationCreatedEvent(engine3);
		((PresentationCreatedEventListener)manager).handleEvent(createdEvent2);
		((PresentationCreatedEventListener)manager).handleEvent(createdEvent1);
		((PresentationCreatedEventListener)manager).handleEvent(createdEvent3);
		
		assertEquals(2, manager.getRendringEngines(networkView1).size());
		assertEquals(1, manager.getRendringEngines(networkView2).size());
		
		assertTrue(manager.getRendringEngines(networkView1).contains(engine1));
		assertTrue(manager.getRendringEngines(networkView1).contains(engine2));
		assertTrue(manager.getRendringEngines(networkView2).contains(engine3));
		
	}

}
