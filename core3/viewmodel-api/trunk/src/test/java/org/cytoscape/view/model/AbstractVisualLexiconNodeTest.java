package org.cytoscape.view.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractVisualLexiconNodeTest {
	
	protected VisualProperty<NullDataType> vp1;
	protected VisualProperty<Number> vp2;
	protected VisualProperty<Color> vp3;
	protected VisualProperty<Visualizable> vp4;
	
	protected VisualLexiconNode node1;
	protected VisualLexiconNode node2;
	protected VisualLexiconNode node3;
	protected VisualLexiconNode node4;
	
	protected VisualLexiconNodeFactory factory;
	

	@Before
	public void setUp() throws Exception {
		node1 = factory.createNode(vp1, null);
		node2 = factory.createNode(vp2, node1);
		node4 = factory.createNode(vp4, node1);
		node3 = factory.createNode(vp3, node4);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testVisualLexiconNodeImpl() {
		assertNotNull(node1);
		assertNotNull(node2);
		assertNotNull(node3);
		assertNotNull(node4);
	}

	@Test
	public void testGetVisualProperty() {
		assertEquals(vp1, node1.getVisualProperty());
		assertEquals(vp2, node2.getVisualProperty());
		assertEquals(vp3, node3.getVisualProperty());
		assertEquals(vp4, node4.getVisualProperty());
	}

	@Test
	public void testGetParent() {
		assertNull(node1.getParent());
		assertEquals(node1, node2.getParent());
		assertEquals(node1, node4.getParent());
		assertEquals(node4, node3.getParent());
	}

	@Test
	public void testGetChildren() {
		assertEquals(2, node1.getChildren().size());
		assertEquals(0, node2.getChildren().size());
		assertEquals(1, node4);
		assertEquals(0, node3.getChildren().size());
	}

}