package org.cytoscape.view.vizmap;


import static org.junit.Assert.*;

import java.awt.Color;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractVisualStyleTest {

	
	protected VisualStyle style;
	
	protected String originalTitle;
	protected String newTitle;
	
	protected VisualLexicon lexicon;
	
	
	@Before
	public void setUp() throws Exception {
		// Create root node.
		final VisualProperty<NullDataType> twoDRoot = new NullVisualProperty(
				"TWO_D_ROOT", "2D Root Visual Property");
		
		lexicon = new TwoDVisualLexicon(twoDRoot);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testVisualStyle() {
		assertNotNull(style);
		assertNotNull(originalTitle);
		assertNotNull(newTitle);
		
		// Test title
		assertEquals(originalTitle, style.getTitle());
		style.setTitle(newTitle);
		assertEquals(newTitle, style.getTitle());
		
		// Test lexicon
		assertEquals(lexicon, style.getVisualLexicon());
		
		// Test default values.
		assertEquals(Color.white, style.getDefaultValue(TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR));
		style.setDefaultValue(TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR, Color.BLACK);
		assertEquals(Color.BLACK, style.getDefaultValue(TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR));
		
		assertEquals(Color.GRAY, style.getDefaultValue(TwoDVisualLexicon.NODE_PAINT));
		
		
		
		
		
		
	}
}
