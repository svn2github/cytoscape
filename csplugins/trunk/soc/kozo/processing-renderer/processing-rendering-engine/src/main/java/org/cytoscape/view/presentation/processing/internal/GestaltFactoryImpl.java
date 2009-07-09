package org.cytoscape.view.presentation.processing.internal;

import gestalt.render.Drawable;
import gestalt.shape.DrawableFactory;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.GestaltFactory;
import org.cytoscape.view.presentation.processing.P5Presentation;

public class GestaltFactoryImpl implements GestaltFactory {
	
	private DrawableFactory dFactory;

	
	public GestaltFactoryImpl(DrawableFactory dFactory) {
		this.dFactory = dFactory;
	}
	
	public <T> P5Presentation<T> getPresentation(
			Class<? extends Drawable> type, View<T> viewModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
