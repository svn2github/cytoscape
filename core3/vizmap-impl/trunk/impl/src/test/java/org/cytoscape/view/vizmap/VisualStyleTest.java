package org.cytoscape.view.vizmap;

import static org.mockito.Mockito.*;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
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
