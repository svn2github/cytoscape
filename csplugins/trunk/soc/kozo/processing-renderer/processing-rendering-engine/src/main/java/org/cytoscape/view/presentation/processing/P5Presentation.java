package org.cytoscape.view.presentation.processing;

import gestalt.render.Drawable;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.Renderer;

public interface P5Presentation <T> extends Renderer {
	public View<T> getViewModel();
	public void setViewModel(View<T> model);
	
	public Drawable getDrawable();
	public void setDrawable(Drawable drawable);
	
	// This is an immutable value, which represents the type of presentation.
	public PresentationType getPresentationType();
}
