package org.cytoscape.view.vizmap;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.NullVisualProperty;
import org.cytoscape.view.vizmap.internal.VisualLexiconManager;
import org.cytoscape.view.vizmap.internal.VisualStyleFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class VisualStyleTest extends AbstractVisualStyleTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		// Create root node.
		final VisualLexiconManager lexManager = mock(VisualLexiconManager.class);
		
		
		// Create root node.
		final NullVisualProperty minimalRoot = new NullVisualProperty("MINIMAL_ROOT", "Minimal Root Visual Property");
		final MinimalVisualLexicon minimalLex = new MinimalVisualLexicon(minimalRoot);
		final Set<VisualLexicon> lexSet = new HashSet<VisualLexicon>();
		lexSet.add(minimalLex);
		final Collection<VisualProperty<?>> nodeVP = minimalLex.getAllDescendants(MinimalVisualLexicon.NODE);
		final Collection<VisualProperty<?>> edgeVP = minimalLex.getAllDescendants(MinimalVisualLexicon.EDGE);
		when(lexManager.getNodeVisualProperties()).thenReturn(nodeVP);
		when(lexManager.getEdgeVisualProperties()).thenReturn(edgeVP);
		
		when(lexManager.getAllVisualLexicon()).thenReturn(lexSet);
		
		final VisualStyleFactoryImpl visualStyleFactory = new VisualStyleFactoryImpl(lexManager);
		originalTitle = "Style 1";
		newTitle = "Style 2";
		style = visualStyleFactory.getInstance(originalTitle);
	}

	@After
	public void tearDown() throws Exception {
	}

}
