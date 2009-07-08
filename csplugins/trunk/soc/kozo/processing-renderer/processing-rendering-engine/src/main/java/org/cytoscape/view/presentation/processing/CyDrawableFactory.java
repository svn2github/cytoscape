package org.cytoscape.view.presentation.processing;

import gestalt.render.Drawable;

public interface CyDrawableFactory {
	
	public Drawable getDrawable(Class<? extends Drawable> type);

}
