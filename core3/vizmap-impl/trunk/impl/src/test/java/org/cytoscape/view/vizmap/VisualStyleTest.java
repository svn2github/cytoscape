package org.cytoscape.view.vizmap;

import static org.mockito.Mockito.mock;

import org.cytoscape.view.vizmap.internal.VisualLexiconManager;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleTest extends AbstractVisualStyleTest {

	@Before
	public void setUp() throws Exception {

		// Create root node.
		final VisualLexiconManager lexManager = mock(VisualLexiconManager.class);
		final VisualStyleFactoryImpl visualStyleFactory = new VisualStyleFactoryImpl(lexManager);
		originalTitle = "Style 1";
		newTitle = "Style 2";
		style = visualStyleFactory.getInstance(originalTitle);
	}

	@After
	public void tearDown() throws Exception {
	}

}
