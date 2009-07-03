package org.cytoscape.view.presentation.processing;

import gestalt.render.Drawable;

import org.cytoscape.view.model.View;

public interface GestaltFactory {	
	public <T> P5Presentation<T> getPresentation(
			Class<? extends Drawable> type, View<T> viewModel);
}
