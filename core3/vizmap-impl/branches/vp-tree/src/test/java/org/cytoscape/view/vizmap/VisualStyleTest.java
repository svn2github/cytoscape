package org.cytoscape.view.vizmap;

import static org.easymock.EasyMock.*;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.internal.VisualLexiconNodeFactoryImpl;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.AbstractVisualStyleTest;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleTest extends AbstractVisualStyleTest {

	@Before
	public void setUp() throws Exception {

		// Create root node.
		final VisualProperty<NullDataType> twoDRoot = new NullVisualProperty(
				"TWO_D_ROOT", "2D Root Visual Property");

		lexicon = new TwoDVisualLexicon(twoDRoot,
				new VisualLexiconNodeFactoryImpl());

		final CyEventHelper helperMock = createMock(CyEventHelper.class);
		final VisualStyleFactoryImpl visualStyleFactory = new VisualStyleFactoryImpl(
				helperMock);
		originalTitle = "Style 1";
		newTitle = "Style 2";
		style = visualStyleFactory.createVisualStyle(originalTitle, lexicon);
	}

	@After
	public void tearDown() throws Exception {
	}

}
