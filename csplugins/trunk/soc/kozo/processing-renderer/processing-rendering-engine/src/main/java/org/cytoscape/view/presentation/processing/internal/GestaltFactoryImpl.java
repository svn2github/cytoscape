package org.cytoscape.view.presentation.processing.internal;

import gestalt.render.Drawable;
import gestalt.shape.DrawableFactory;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.P5Renderer;

public class GestaltFactoryImpl {
	
	private DrawableFactory dFactory;

	
	public GestaltFactoryImpl(DrawableFactory dFactory) {
		this.dFactory = dFactory;
	}
	
	public <T> P5Renderer<T> getPresentation(View<T> viewModel) {
		
		
		
		
		return null;
	}
	
	private void createCyNetworkView() {
		
	}

}
