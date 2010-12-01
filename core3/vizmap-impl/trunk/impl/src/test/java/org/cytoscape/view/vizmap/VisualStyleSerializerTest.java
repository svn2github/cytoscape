package org.cytoscape.view.vizmap;


import static org.mockito.Mockito.*;

import org.cytoscape.view.vizmap.internal.VisualStyleSerializerImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleSerializerTest extends AbstractVisualStyleSerializerTest {
	
	@Before
	public void setUp() throws Exception {
		serializer = new VisualStyleSerializerImpl();	
	}
}
