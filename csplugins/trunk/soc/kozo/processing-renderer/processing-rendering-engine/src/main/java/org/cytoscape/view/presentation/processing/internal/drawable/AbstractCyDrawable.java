package org.cytoscape.view.presentation.processing.internal.drawable;

import java.util.List;
import java.util.Set;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class AbstractCyDrawable implements CyDrawable {
	
	protected final PApplet p;

	public AbstractCyDrawable(final PApplet p) {
		this.p  = p;
	}
	

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub

	}

	public void draw() {
		// TODO Auto-generated method stub

	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Class<?>> getCompatibleModels() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContext(View<?> viewModel) {
		// TODO Auto-generated method stub

	}

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// TODO Auto-generated method stub

	}

	public void setDetailFlag(boolean flag) {
		// TODO Auto-generated method stub

	}

}
