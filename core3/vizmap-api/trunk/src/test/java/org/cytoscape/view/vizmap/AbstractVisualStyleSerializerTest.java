package org.cytoscape.view.vizmap;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Properties;

public abstract class AbstractVisualStyleSerializerTest {
	
	protected VisualStyleSerializer serializer;
	
	@Test
	public void testVisualStyleCollectionNotNullForNullProperties() throws Exception {
		assertNotNull(serializer.createVisualStyles(null));
	}

	@Test
	public void testVisualStyleCollectionNotNullForEmptyProperties() throws Exception {
		assertNotNull(serializer.createVisualStyles(new Properties()));
	}

	@Test
	public void testPropertiesNotNullForNullVS() throws Exception {
		assertNotNull(serializer.createProperties(null));
	}

	@Test
	public void testPropertiesNotNullForEmptyVS() throws Exception {
		assertNotNull(serializer.createProperties(new ArrayList<VisualStyle>()));
	}
}
