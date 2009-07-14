package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_STYLE;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Renderer;
import org.cytoscape.view.presentation.processing.internal.shape.GCube;
import org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon;

public class CyNodePresentationBuilder {

	private ProcessingVisualLexicon lexicon;
	
	public CyNodePresentationBuilder(ProcessingVisualLexicon lexicon) {
		this.lexicon = lexicon;
	}

	public P5Renderer<CyNode> buildPresentation(View<CyNode> nodeView) throws InstantiationException, IllegalAccessException {

		// First, check the Drawable type
		CyDrawable value = nodeView.getVisualProperty(NODE_STYLE);

		// If not available, use default
		if (value == null)
			value = new GCube(lexicon);

		VisualLexicon vpSet = lexicon.getSubLexicon(value.getClass());

		// Customize Drawable
		for (VisualProperty<?> vp : vpSet.getAllVisualProperties()) {

		}

		final P5Renderer<CyNode> presentation = new P5NodePresentation(
				nodeView, value);

		return presentation;
	}
}
