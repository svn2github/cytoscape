package org.cytoscape.view.presentation.processing.internal;

import gestalt.context.GLContext;
import gestalt.extension.picking.Pickable;
import gestalt.render.Drawable;
import gestalt.shape.AbstractShape;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.P5Presentation;
import org.cytoscape.view.presentation.processing.PresentationType;

import static org.cytoscape.view.presentation.processing.PresentationType.*;

public class P5NodePresentationimpl extends AbstractShape implements
		P5Presentation<CyNode>, Pickable {

	public Drawable getDrawable() {
		// TODO Auto-generated method stub
		return null;
	}

	public PresentationType getPresentationType() {
		return NODE;
	}

	public View<CyNode> getViewModel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDrawable(Drawable drawable) {
		// TODO Auto-generated method stub

	}

	public void setViewModel(View<CyNode> model) {
		// TODO Auto-generated method stub

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

	public void draw(GLContext arg0) {
		// TODO Auto-generated method stub

	}

}
