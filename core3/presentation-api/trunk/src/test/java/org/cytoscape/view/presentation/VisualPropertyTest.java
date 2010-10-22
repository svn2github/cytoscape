package org.cytoscape.view.presentation;


import static org.junit.Assert.*;

import java.awt.Color;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.PaintVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VisualPropertyTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testVisualProperties() {
		final VisualProperty<Color> colorProp = TwoDVisualLexicon.NODE_COLOR;
		assertEquals(Color.class, colorProp.getType());
		
		final VisualProperty<Boolean> booleanProp = TwoDVisualLexicon.NODE_VISIBLE;
		assertEquals(Boolean.class, booleanProp.getType());
		assertEquals("false", booleanProp.toSerializableString(Boolean.FALSE));
		assertEquals(false, booleanProp.parseSerializableString("false"));
		assertEquals(false, booleanProp.parseSerializableString("False"));
		assertEquals(false, booleanProp.parseSerializableString("FALSE"));
		
		final VisualProperty<Double> doubleProp = TwoDVisualLexicon.NODE_SIZE;
		assertEquals(Boolean.class, booleanProp.getType());
		assertEquals("false", booleanProp.toSerializableString(Boolean.FALSE));
		assertEquals(false, booleanProp.parseSerializableString("false"));
		assertEquals(false, booleanProp.parseSerializableString("False"));
		assertEquals(false, booleanProp.parseSerializableString("FALSE"));
	}

}
