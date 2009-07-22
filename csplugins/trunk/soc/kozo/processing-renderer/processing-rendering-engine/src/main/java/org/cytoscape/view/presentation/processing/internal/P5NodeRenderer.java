package org.cytoscape.view.presentation.processing.internal;

import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Renderer;

public class P5NodeRenderer implements P5Renderer<CyNode> {

	private CyDrawable top;

	public P5NodeRenderer(View<CyNode> nodeView, CyDrawable top) {

		this.top = top;
	}

	public CyDrawable getCyDrawable() {
		// TODO Auto-generated method stub
		return null;
	}

	public VisualLexicon getVisualLexicon() {
		// TODO Auto-generated method stub
		return null;
	}




}
