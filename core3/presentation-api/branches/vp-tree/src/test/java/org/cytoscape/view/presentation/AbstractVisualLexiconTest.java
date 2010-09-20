package org.cytoscape.view.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashSet;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractVisualLexiconTest {

	@Before
	public void setUp() throws Exception {
		
		
	}

	
	@After
	public void tearDown() throws Exception {
		
		
	}
	
	
	protected void testTree(VisualLexicon lextcon) throws Exception {
		final VisualProperty<NullDataType> root = lextcon.getRootVisualProperty();
		assertNotNull(root);
		assertEquals(lextcon.getRootVisualProperty(), root);
		
		Collection<VisualProperty<?>> firstChildren = root.getChildren();
		assertFalse(0 == firstChildren.size());
		traverse(firstChildren);
	}
	

	private void traverse(final Collection<VisualProperty<?>> vpSet) {

		Collection<VisualProperty<?>> children = vpSet;
		Collection<VisualProperty<?>> nextChildren = new HashSet<VisualProperty<?>>();

		for (VisualProperty<?> child : children) {
			final VisualProperty<?> parent = child.getParent();
			assertNotNull(parent);
			System.out.println(child.getParent().getDisplayName()
					+ "\thas_child\t" + child.getDisplayName());

			for (final VisualProperty<?> nextCh : child.getChildren())
				assertEquals(child, nextCh.getParent());

			nextChildren.addAll(child.getChildren());
		}

		if (nextChildren.size() == 0)
			return;
		else
			traverse(nextChildren);
	}
}
