package org.cytoscape.vizmap;

import static org.junit.Assert.assertEquals;

import java.awt.Paint;

import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DiscreteMappingTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDiscreteMapping() {
		final String attrName = "sample attr 1";
		final Class<String> type = String.class;
		
		final DiscreteMapping<String, Paint> mapping = new DiscreteMapping<String, Paint>(attrName, type, MinimalVisualLexicon.NODE_FILL_COLOR);
		
		assertEquals(attrName, mapping.getMappingAttributeName());
		assertEquals(type, mapping.getMappingAttributeType());
		assertEquals(MinimalVisualLexicon.NODE_FILL_COLOR, mapping.getVisualProperty());
				
	}
	
}
