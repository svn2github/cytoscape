package org.cytoscape.view.presentation;

import static org.junit.Assert.*;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.internal.VisualLexiconNodeFactoryImpl;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.presentation.property.VisualPropertyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VisualPropertyUtilTest {

	private VisualLexicon lexicon;
	private NullVisualProperty twoDRoot;
	
	private VisualProperty<Double> dummy;

	@Before
	public void setUp() throws Exception {
		dummy = new DoubleVisualProperty(new Double(10), "DUMMY", "Dummy Prop");
		// Create root node.
		twoDRoot = new NullVisualProperty("TWO_D_ROOT",
				"2D Root Visual Property");

		lexicon = new TwoDVisualLexicon(twoDRoot,
				new VisualLexiconNodeFactoryImpl());
	}

	@Test
	public void testIsChildOf() {
		assertTrue(VisualPropertyUtil.isChildOf(twoDRoot, TwoDVisualLexicon.EDGE, lexicon));
		
		try {
			VisualPropertyUtil.isChildOf(twoDRoot, dummy, lexicon);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		assertFalse(VisualPropertyUtil.isChildOf(TwoDVisualLexicon.NODE, TwoDVisualLexicon.EDGE, lexicon));
	}

	@Test
	public void testGetGraphObjectType() {
		assertEquals("NODE", VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.NODE_COLOR, lexicon));
		assertEquals("EDGE", VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.EDGE_COLOR, lexicon));
		assertEquals("NETWORK", VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.NETWORK_BACKGROUND_COLOR, lexicon));
		
		try {
			VisualPropertyUtil.getGraphObjectType(dummy, lexicon);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}

}
