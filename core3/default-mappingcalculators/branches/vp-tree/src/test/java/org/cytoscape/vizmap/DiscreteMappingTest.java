package org.cytoscape.vizmap;

import static org.junit.Assert.*;

import java.awt.Color;

import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
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
		
		final DiscreteMapping<String, Color> mapping = new DiscreteMapping<String, Color>(attrName, type, TwoDVisualLexicon.NODE_COLOR);
		
		assertEquals(attrName, mapping.getMappingAttributeName());
		assertEquals(type, mapping.getMappingAttributeType());
		assertEquals(TwoDVisualLexicon.NODE_COLOR, mapping.getVisualProperty());
				
	}
	
}
