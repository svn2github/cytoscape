package org.cytoscape.view.presentation;


import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.internal.property.NullDataTypeImpl;
import org.cytoscape.view.presentation.internal.property.VisualizableImpl;
import org.cytoscape.view.presentation.property.NullVisualProperty;
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
		final VisualProperty<Paint> colorProp = TwoDVisualLexicon.NODE_COLOR;
		assertEquals(Paint.class, colorProp.getType());
		
		final VisualProperty<Boolean> booleanProp = TwoDVisualLexicon.NODE_VISIBLE;
		assertEquals(Boolean.class, booleanProp.getType());
		assertEquals("false", booleanProp.toSerializableString(Boolean.FALSE));
		assertEquals(false, booleanProp.parseSerializableString("false"));
		assertEquals(false, booleanProp.parseSerializableString("False"));
		assertEquals(false, booleanProp.parseSerializableString("FALSE"));
		assertEquals(CyNode.class, booleanProp.getTargetDataType());
		
		final VisualProperty<Double> doubleProp = TwoDVisualLexicon.NODE_SIZE;
		assertEquals(Double.class, doubleProp.getType());
		assertEquals("20.0", doubleProp.toSerializableString(Double.valueOf(20)));
		assertEquals(Double.valueOf(100.12), doubleProp.parseSerializableString("100.12"));
		
		final VisualProperty<Paint> paintProp = TwoDVisualLexicon.NODE_COLOR;
		assertEquals(Paint.class, paintProp.getType());
		assertEquals(CyNode.class, paintProp.getTargetDataType());
		
		final Color testColor = new Color(10, 20, 30); 
		assertEquals("10,20,30", paintProp.toSerializableString(testColor));
		assertEquals(testColor, paintProp.parseSerializableString("#0A141E"));
		assertEquals(testColor, paintProp.parseSerializableString("10,20,30"));
		
		try {
			final Paint result = paintProp.parseSerializableString("#2JK20A141E");
		}catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			final Paint result = paintProp.parseSerializableString("10, 20");
		}catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		try {
			final Paint result = paintProp.parseSerializableString("10, 20, IJK");
		}catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		final VisualProperty<Visualizable> visualizableProp = TwoDVisualLexicon.NODE;
		assertEquals(Visualizable.class, visualizableProp.getType());
		assertTrue(visualizableProp.toSerializableString(new VisualizableImpl()).contains("Visualizable"));
		assertTrue(visualizableProp.parseSerializableString("test string") instanceof Visualizable );
		
		final VisualProperty<String> stringProp = TwoDVisualLexicon.NODE_LABEL;
		assertEquals(String.class, stringProp.getType());
		assertEquals("test string", stringProp.toSerializableString("test string"));
		assertEquals("test string 2", stringProp.parseSerializableString("test string 2"));
		
		final VisualProperty<NullDataType> nullProp = new NullVisualProperty("ROOT", "Root Visual Property");
		assertEquals(NullDataType.class, nullProp.getType());
		assertTrue(nullProp.toSerializableString(new NullDataTypeImpl()).contains("org.cytoscape.view.presentation.internal.property.NullDataTypeImpl"));
		assertTrue(nullProp.parseSerializableString("test string") instanceof NullDataType );
		
	}

}
