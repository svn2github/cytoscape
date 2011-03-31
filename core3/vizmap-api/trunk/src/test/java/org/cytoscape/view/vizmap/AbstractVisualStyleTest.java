package org.cytoscape.view.vizmap;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.view.model.VisualLexicon;
import org.junit.After;
import org.junit.Test;

public abstract class AbstractVisualStyleTest {

	
	protected VisualStyle style;
	
	protected String originalTitle;
	protected String newTitle;
	
	protected VisualLexicon lexicon;
	

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
	}
}
