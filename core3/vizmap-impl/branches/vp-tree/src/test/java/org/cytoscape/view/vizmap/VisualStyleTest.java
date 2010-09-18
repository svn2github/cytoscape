package org.cytoscape.view.vizmap;

import static org.easymock.EasyMock.*;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.vizmap.AbstractVisualStyleTest;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleTest extends AbstractVisualStyleTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		final CyEventHelper helperMock = createMock(CyEventHelper.class);
		final VisualStyleFactoryImpl visualStyleFactory = new VisualStyleFactoryImpl(helperMock);
		originalTitle = "Style 1";
		newTitle = "Style 2";
		style = visualStyleFactory.createVisualStyle(originalTitle, lexicon);
	}

	@After
	public void tearDown() throws Exception {
	}

}
