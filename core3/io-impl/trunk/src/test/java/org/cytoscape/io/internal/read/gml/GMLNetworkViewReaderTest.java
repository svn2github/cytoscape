package org.cytoscape.io.internal.read.gml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GMLNetworkViewReaderTest extends AbstractNetworkViewReaderTester {
	@Mock private CyApplicationManager applicationManager;
	@Mock private RenderingEngine<CyNetwork> engine;
	@Mock private VisualLexicon lexicon;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		MockitoAnnotations.initMocks(this);
		
		when(applicationManager.getCurrentRenderingEngine()).thenReturn(engine);
		when(engine.getVisualLexicon()).thenReturn(lexicon);
		
		// FIXME
		//when(lexicon.getVisualProperties(any(String.class))).thenReturn(new LinkedList<VisualProperty<?>>());
	}
	
	@Test
	public void testLoadGml() throws Exception {
		File file = new File("src/test/resources/testData/gml/example1.gml");
		GMLNetworkViewReader reader =
			new GMLNetworkViewReader(new FileInputStream(file), netFactory, viewFactory, applicationManager);
		reader.run(taskMonitor);
		CyNetworkView[] networkViews = reader.getNetworkViews();
		
		assertNotNull(networkViews);
		assertEquals(1, networkViews.length);
		
		CyNetworkView view = networkViews[0];
		assertNotNull(view);
		
		CyNetwork model = view.getModel();
		assertNotNull(model);
		
		assertEquals(3, model.getNodeCount());
		assertEquals(3, model.getEdgeCount());
	}
}
