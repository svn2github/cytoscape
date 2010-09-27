package org.cytoscape.io.internal.read.gml;

import java.io.File;
import java.io.FileInputStream;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.junit.Test;
import static org.junit.Assert.*;

public class GMLNetworkViewReaderTest extends AbstractNetworkViewReaderTester {
	@Test
	public void testLoadGml() throws Exception {
		File file = new File("src/test/resources/testData/gml/example1.gml");
		GMLNetworkViewReader reader = new GMLNetworkViewReader(new FileInputStream(file), netFactory, viewFactory);
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
