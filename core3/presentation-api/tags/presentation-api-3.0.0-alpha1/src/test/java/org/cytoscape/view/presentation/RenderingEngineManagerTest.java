package org.cytoscape.view.presentation;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.events.PresentationCreatedEvent;
import org.cytoscape.view.presentation.events.PresentationCreatedListener;
import org.cytoscape.view.presentation.events.PresentationDestroyedEvent;
import org.cytoscape.view.presentation.events.PresentationDestroyedListener;
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
		final CyNetworkView networkView1 = mock(CyNetworkView.class);
		final CyNetworkView networkView2 = mock(CyNetworkView.class);
		
		final RenderingEngineFactory<CyNetwork> factory = mock(RenderingEngineFactory.class);
		
		final RenderingEngine<CyNetwork> engine1 = mock(RenderingEngine.class);
		
		final RenderingEngine<CyNetwork> engine2 = mock(RenderingEngine.class);
		when(engine2.getViewModel()).thenReturn(networkView1);
		final RenderingEngine<CyNetwork> engine3 = mock(RenderingEngine.class);
		when(engine3.getViewModel()).thenReturn(networkView2);
		
		final Collection<RenderingEngine<?>> engineSet = manager.getRendringEngines(networkView1);
		assertNotNull(engineSet);
		assertEquals(0, engineSet.size());
		
		final PresentationCreatedEvent createdEvent1 = new PresentationCreatedEvent(factory, engine1);
		
		when(engine1.getViewModel()).thenReturn(networkView1);
		((PresentationCreatedListener)manager).handleEvent(createdEvent1);

		assertEquals(1, manager.getRendringEngines(networkView1).size());
		assertEquals(engine1, manager.getRendringEngines(networkView1).iterator().next());
		
		
		// Remove from manager
		final PresentationDestroyedEvent destroyEvent = new PresentationDestroyedEvent(factory, engine1);
		((PresentationDestroyedListener)manager).handleEvent(destroyEvent);
		
		assertEquals(0, manager.getRendringEngines(networkView1).size());
		assertTrue(manager.getRendringEngines(networkView1) instanceof Set);
		
		// Add multiple engines
		final PresentationCreatedEvent createdEvent2 = new PresentationCreatedEvent(factory, engine2);
		final PresentationCreatedEvent createdEvent3 = new PresentationCreatedEvent(factory, engine3);
		((PresentationCreatedListener)manager).handleEvent(createdEvent2);
		((PresentationCreatedListener)manager).handleEvent(createdEvent1);
		((PresentationCreatedListener)manager).handleEvent(createdEvent3);
		
		assertEquals(2, manager.getRendringEngines(networkView1).size());
		assertEquals(1, manager.getRendringEngines(networkView2).size());
		
		assertTrue(manager.getRendringEngines(networkView1).contains(engine1));
		assertTrue(manager.getRendringEngines(networkView1).contains(engine2));
		assertTrue(manager.getRendringEngines(networkView2).contains(engine3));
		
	}
}
