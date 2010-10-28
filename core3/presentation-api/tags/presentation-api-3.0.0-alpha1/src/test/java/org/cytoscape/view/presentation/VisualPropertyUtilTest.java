package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.DoubleVisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.presentation.property.VisualPropertyUtil;
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

		lexicon = new TwoDVisualLexicon(twoDRoot);
	}

	@Test
	public void testIsChildOf1() {
		assertTrue(VisualPropertyUtil.isChildOf(twoDRoot, TwoDVisualLexicon.EDGE, lexicon));
		
		try {
			VisualPropertyUtil.isChildOf(twoDRoot, dummy, lexicon);
		} catch(Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		
		assertFalse(VisualPropertyUtil.isChildOf(TwoDVisualLexicon.NODE, TwoDVisualLexicon.EDGE, lexicon));
	}
	
	@Test
	public void testIsChildOf2() {
		assertEquals(false, VisualPropertyUtil.isChildOf(twoDRoot, twoDRoot, lexicon));
	}
	
	@Test
	public void testIsChildOf3() {
		assertEquals(true, VisualPropertyUtil.isChildOf(TwoDVisualLexicon.NODE_COLOR, TwoDVisualLexicon.NODE_COLOR, lexicon));
	}
	
	@Test
	public void testIsChildOf4() {
		assertEquals(true, VisualPropertyUtil.isChildOf(TwoDVisualLexicon.NODE_PAINT, TwoDVisualLexicon.NODE_COLOR, lexicon));
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsChildOfInvalid1() {
		VisualPropertyUtil.isChildOf(twoDRoot, null, lexicon);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsChildOfInvalid2() {
		VisualPropertyUtil.isChildOf(twoDRoot, dummy, null);
	}
	
	

	@Test
	public void testGetGraphObjectType1() {
		assertEquals("NODE", VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.NODE_COLOR, lexicon));
	}
	
	@Test
	public void testGetGraphObjectType2() {
		assertEquals("EDGE", VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.EDGE_COLOR, lexicon));
	}
	
	@Test
	public void testGetGraphObjectType3() {
		final String type = VisualPropertyUtil.getGraphObjectType(TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT, lexicon);
		assertEquals("NETWORK", type);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetGraphObjectTypeInvalid() {
		VisualPropertyUtil.getGraphObjectType(dummy, lexicon);
	}

}
