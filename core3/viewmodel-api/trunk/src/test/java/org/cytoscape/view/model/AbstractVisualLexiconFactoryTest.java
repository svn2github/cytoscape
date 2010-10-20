package org.cytoscape.view.model;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractVisualLexiconFactoryTest {
	
	protected VisualLexiconNodeFactory nodeFactory;
	
	
	
	protected VisualProperty<?> parentVisualProp;
	
	protected VisualProperty<?> vp1;
	protected VisualProperty<?> vp2;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateNode() {
		assertNotNull(nodeFactory);
		
		final VisualLexiconNode parentNode = nodeFactory.createNode(parentVisualProp, null);
		Collection<VisualLexiconNode> children = parentNode.getChildren();
		assertEquals(0, children.size());
		
		final VisualLexiconNode vp1Node = nodeFactory.createNode(vp1, parentNode);
		final VisualLexiconNode vp2Node = nodeFactory.createNode(vp2, parentNode);
		
		assertEquals(2, parentNode.getChildren().size());
		
	}

}
