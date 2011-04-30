package org.cytoscape.io.internal.read.gml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GMLNetworkViewReaderTest extends AbstractNetworkViewReaderTester {
	@Mock private RenderingEngineManager renderingEngineManager;
	@Mock private VisualLexicon lexicon;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		MockitoAnnotations.initMocks(this);
		
		when(renderingEngineManager.getDefaultVisualLexicon()).thenReturn(lexicon);
		
		// FIXME
		//when(lexicon.getVisualProperties(any(String.class))).thenReturn(new LinkedList<VisualProperty<?>>());
	}
	
	@Test
	public void testLoadGml() throws Exception {
		File file = new File("src/test/resources/testData/gml/example1.gml");
		GMLNetworkViewReader reader =
			new GMLNetworkViewReader(new FileInputStream(file), netFactory, viewFactory, renderingEngineManager, viewThreshold);
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
