package org.cytoscape.view.vizmap;


import static org.mockito.Mockito.mock;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleFactoryTest extends AbstractVisualStyleFactoryTest {

	
	private static final VisualProperty<NullDataType> TWO_D_ROOT = new NullVisualProperty("DEFAULT_LEXICON_ROOT",
	"2D Lexicon Root Visual Property");
	
	private static final VisualLexicon DEFAULT_LEXICON = new TwoDVisualLexicon(TWO_D_ROOT);
	
	@Before
	public void setUp() throws Exception {
		final CyEventHelper eventHelper = mock(CyEventHelper.class);
		factory = new VisualStyleFactoryImpl(eventHelper);
		
		lexicon = DEFAULT_LEXICON;
	}

	@After
	public void tearDown() throws Exception {
	}

}
