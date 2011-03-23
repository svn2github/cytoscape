package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.AbstractVisualLexiconTest;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.ThreeDVisualLexicon;
import org.junit.Before;
import org.junit.Test;

public class ThreeDVisualLexiconTest extends AbstractVisualLexiconTest {

	private VisualLexicon threeDLex;
	private VisualProperty<NullDataType> threeDRoot;

	@Before
	public void setUp() throws Exception {

		// Create root node.
		threeDRoot = new NullVisualProperty("THREE_D_ROOT", "3D Root Visual Property");

		threeDLex = new ThreeDVisualLexicon(threeDRoot);
	}

	@Test
	public void testLexiconInstances() {
		assertNotNull(threeDLex);
	}

	@Test
	public void test2DLexicon() throws Exception {
		assertEquals(38, threeDLex.getAllVisualProperties().size());
	}

	@Test
	public void testTree() throws Exception {
		testTree(threeDLex);
	}

}
