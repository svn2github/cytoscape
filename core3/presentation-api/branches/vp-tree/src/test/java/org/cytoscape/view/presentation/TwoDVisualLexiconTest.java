package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.junit.Before;
import org.junit.Test;

public class TwoDVisualLexiconTest extends AbstractVisualLexiconTest {

	private VisualLexicon twoDLex;
	private VisualProperty<NullDataType> twoDRoot;

	@Before
	public void setUp() throws Exception {

		// Create root node.
		twoDRoot = new NullVisualProperty("TWO_D_ROOT",
				"2D Root Visual Property");

		twoDLex = new TwoDVisualLexicon(twoDRoot);
	}

	@Test
	public void testLexiconInstances() {
		assertNotNull(twoDLex);
	}

	@Test
	public void test2DLexicon() throws Exception {
		assertEquals(38, twoDLex.getAllVisualProperties().size());
	}

	@Test
	public void testTree() throws Exception {
		testTree(twoDLex);
	}
}