package org.cytoscape.vizmap;

import java.awt.Color;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.junit.After;
import org.junit.Before;

public class DiscreteMappingTest extends
		AbstractVisualMappingFunctionTest<String, Color> {
	
	private CyNetworkView networkView;

	@Before
	public void setUp() throws Exception {
		attrName = "sample attr 1";
		attrType = String.class;
		vp = new ColorVisualProperty("NODE", Color.red, "colorVP", "Color Visual Property");
		
		mapping = new DiscreteMapping<String, Color>(attrName, attrType, vp);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	
}
