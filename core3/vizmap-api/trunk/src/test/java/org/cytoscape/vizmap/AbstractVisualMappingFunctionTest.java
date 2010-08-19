package org.cytoscape.vizmap;


import static org.junit.Assert.*;

import java.util.Collection;

import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractVisualMappingFunctionTest<K, V> {
	
	protected VisualMappingFunction<K, V> mapping;
	
	// Aname of controlling attr.
	protected String attrName;
	protected Class<K> attrType;
	protected VisualProperty<V> vp;
	
	
	
	protected Collection<View<GraphObject>> views;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testMappingSettings() throws Exception {
		assertEquals(attrName, mapping.getMappingAttributeName());
		assertEquals(attrType, mapping.getMappingAttributeType());
		assertEquals(vp, mapping.getVisualProperty());
	}
	
	@Test
	public void testMappingApply() throws Exception {
		//FIXME need a real test!
		mapping.apply(views);
		
	}

}
