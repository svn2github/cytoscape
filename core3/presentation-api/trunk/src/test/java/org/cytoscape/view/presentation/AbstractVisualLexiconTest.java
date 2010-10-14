package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashSet;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;

public abstract class AbstractVisualLexiconTest {
	
	
	protected void testTree(VisualLexicon lexicon) throws Exception {
		final VisualProperty<NullDataType> root = lexicon.getRootVisualProperty();
		assertNotNull(root);
		assertEquals(lexicon.getRootVisualProperty(), root);
		
		final VisualLexiconNode rootNode = lexicon.getVisualLexiconNode(root);
		assertNotNull(rootNode);
		assertEquals(root, rootNode.getVisualProperty());
		
		final Collection<VisualLexiconNode> children = rootNode.getChildren();
		
		assertFalse(0 == children.size());
		traverse(children, lexicon);
	}
	

	private void traverse(final Collection<VisualLexiconNode> vpSet, VisualLexicon lexicon) {

		Collection<VisualLexiconNode> children = vpSet;
		Collection<VisualLexiconNode> nextChildren = new HashSet<VisualLexiconNode>();

		for (VisualLexiconNode child : children) {
			final VisualLexiconNode parent = child.getParent();
			
			System.out.println(parent.getVisualProperty().getDisplayName()
						+ "\thas_child\t" + child.getVisualProperty().getDisplayName());
			

			for (final VisualLexiconNode nextCh : child.getChildren())
				assertEquals(child, nextCh.getParent());

			nextChildren.addAll(child.getChildren());
			
		}

		if (nextChildren.size() == 0)
			return;
		else
			traverse(nextChildren, lexicon);
	}
	
}
