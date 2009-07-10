package org.cytoscape.view.presentation.processing.internal;

import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;
import gestalt.render.Drawable;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.P5Presentation;

public class P5NodePresentationimpl implements
		P5Presentation<CyNode>, Pickable {
		
	private Drawable top;
	private View<CyNode> viewModel;	
	
	public P5NodePresentationimpl(View<CyNode> nodeView, Drawable top) {
		this.viewModel = nodeView;
		this.top = top;
	}


	public Drawable getDrawable() {
		return top;
	}

	public View<CyNode> getViewModel() {
		return viewModel;
	}

	public void setDrawable(Drawable drawable) {
		this.top = drawable;
	}


	public void setViewModel(View<CyNode> model) {
		this.viewModel = model;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}


	public VisualLexicon getVisualLexicon() {
		// TODO Auto-generated method stub
		return null;
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
}
