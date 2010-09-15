package org.cytoscape.view.model;


import static org.junit.Assert.*;

import java.util.Set;

import org.cytoscape.view.model.internal.MinimalVisualLexicon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VisualLexiconTest {
	
	private VisualLexicon minimalLex;
	private VisualLexicon twoDLex;
	private VisualLexicon threeDLex;

	@Before
	public void setUp() throws Exception {
		minimalLex = new MinimalVisualLexicon();
		twoDLex = new TwoDVisualLexicon();
		threeDLex = new ThreeDVisualLexicon();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testLexiconInstances() {
		assertNotNull(minimalLex);
		assertNotNull(twoDLex);
		assertNotNull(threeDLex);
	}
	
	@Test
	public void testMinimalLexicon() {
		final Set<VisualProperty<?>> all = minimalLex.getAllVisualProperties();
		
		assertEquals(1, all.size());
		assertEquals(38, twoDLex.getAllVisualProperties().size());
		minimalLex.mergeLexicon(twoDLex);
		assertEquals(39, minimalLex.getAllVisualProperties().size());
	}

}
