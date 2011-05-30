package org.cytoscape.view.vizmap;


import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.cytoscape.view.vizmap.model.Vizmap;
import org.junit.Test;

public abstract class AbstractVisualStyleSerializerTest {
	
	protected VisualStyleSerializer serializer;
	
	@Test
	public void testVisualStyleCollectionNotNullForNullVizmap() throws Exception {
		assertNotNull(serializer.createVisualStyles(null));
	}

	@Test
	public void testVisualStyleCollectionNotNullForEmptyVizmap() throws Exception {
		assertNotNull(serializer.createVisualStyles(new Vizmap()));
	}

	@Test
	public void testVizmapNotNullForNullVS() throws Exception {
		assertNotNull(serializer.createVizmap(null));
	}

	@Test
	public void testVizmapNotNullForEmptyVS() throws Exception {
		assertNotNull(serializer.createVizmap(new ArrayList<VisualStyle>()));
	}
}
