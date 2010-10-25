package org.cytoscape.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cytoscape.ding.impl.DVisualLexicon;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.internal.VisualLexiconNodeFactoryImpl;
import org.cytoscape.view.presentation.property.AbstractVisualLexiconTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DVisualLexiconTest extends AbstractVisualLexiconTest {

	private VisualLexicon dLexicon;

	@Before
	public void setUp() throws Exception {
		dLexicon = new DVisualLexicon(new VisualLexiconNodeFactoryImpl());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDVisualLexicon() throws Exception {
		assertNotNull(dLexicon);

		VisualProperty<NullDataType> root = dLexicon.getRootVisualProperty();
		assertNotNull(root);
		assertEquals(DVisualLexicon.DING_ROOT, root);

		assertEquals(1, dLexicon.getVisualLexiconNode(root).getChildren().size());
		
		assertEquals(68, dLexicon.getAllVisualProperties().size());
	}

	@Test
	public void testDVisualLexiconTree() throws Exception {
		this.testTree(dLexicon);
	}

}
