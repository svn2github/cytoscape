package org.cytoscape.view.presentation.processing.internal;

import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.P5Presentation;

public class P5NodePresentation implements P5Presentation<CyNode>, Pickable {

	private CyDrawable top;
	private View<CyNode> viewModel;

	public P5NodePresentation(View<CyNode> nodeView, CyDrawable top) {
		this.viewModel = nodeView;
		this.top = top;
	}

	public CyDrawable getCyDrawable() {
		return top;
	}

	public View<CyNode> getViewModel() {
		return viewModel;
	}

	public boolean isPicked() {
		// TODO Auto-generated method stub
		return false;
	}

	public void mouseEnter() {
		// TODO Auto-generated method stub

	}

	public void mouseLeave() {
		// TODO Auto-generated method stub

	}

	public void mouseWithin() {
		// TODO Auto-generated method stub

	}

	public void pickDraw(GLContext arg0) {
		// TODO Auto-generated method stub

	}

	public VisualLexicon getVisualLexicon() {
		// TODO Auto-generated method stub
		return null;
	}
}
