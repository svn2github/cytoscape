package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.AbstractVisualLexiconTest;
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
		assertEquals(36, twoDLex.getAllVisualProperties().size());
	}

	@Test
	public void testTree() throws Exception {
		testTree(twoDLex);
	}
	
	@Test
	public void testGetAllDecendents() throws Exception {
		final VisualProperty<NullDataType> root = twoDLex.getRootVisualProperty();
		Collection<VisualProperty<?>> allChildren = twoDLex.getAllDescendants(root);
		
		assertEquals(twoDLex.getAllVisualProperties().size() -1, allChildren.size());
		
		Collection<VisualLexiconNode> nodeTextChild = twoDLex.getVisualLexiconNode(TwoDVisualLexicon.NODE_TEXT).getChildren();
		assertEquals(1, nodeTextChild.size());
		
		Collection<VisualLexiconNode> nodePaintChild = twoDLex.getVisualLexiconNode(TwoDVisualLexicon.NODE_PAINT).getChildren();
		assertEquals(3, nodePaintChild.size());
		assertEquals(twoDLex.getAllDescendants(TwoDVisualLexicon.NODE_PAINT).size(), nodePaintChild.size());
		
		Collection<VisualProperty<?>> nodeChildren = twoDLex.getAllDescendants(TwoDVisualLexicon.NODE);
		assertEquals(14, nodeChildren.size());
		
		Collection<VisualProperty<?>> edgeChildren = twoDLex.getAllDescendants(TwoDVisualLexicon.EDGE);
		assertEquals(8, edgeChildren.size());
		
		Collection<VisualProperty<?>> leaf = twoDLex.getAllDescendants(TwoDVisualLexicon.EDGE_COLOR);
		assertEquals(0, leaf.size());
				
	}
}