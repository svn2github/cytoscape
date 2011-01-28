package org.cytoscape.view.model;


import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.events.AddedEdgeViewEvent;
import org.cytoscape.view.model.events.AddedNodeViewEvent;
import org.cytoscape.view.model.events.FitContentEvent;
import org.cytoscape.view.model.events.FitSelectedEvent;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ViewModelEventsTest {
	
	CyEventHelper helper;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testEvents() {
		final View<CyNode> nodeView = mock(View.class);
		final CyNetworkView networkView = mock(CyNetworkView.class);
		AddedNodeViewEvent ev1 = new AddedNodeViewEvent(networkView, nodeView);
		
		assertEquals(nodeView, ev1.getNodeView());
		assertEquals(networkView, ev1.getSource());
		
		final View<CyEdge> edgeView = mock(View.class);
		AddedEdgeViewEvent ev2 = new AddedEdgeViewEvent(networkView, edgeView);
		
		assertEquals(edgeView, ev2.getEdgeView());
		assertEquals(networkView, ev2.getSource());
		
		UpdateNetworkPresentationEvent ev3 = new UpdateNetworkPresentationEvent(networkView);
		assertEquals(networkView, ev3.getSource());
		
		FitSelectedEvent ev4 = new FitSelectedEvent(networkView);
		assertEquals(networkView, ev4.getSource());
		
		FitContentEvent ev5 = new FitContentEvent(networkView);
		assertEquals(networkView, ev5.getSource());
		
	}
	
	

}
