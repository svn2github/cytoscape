package org.cytoscape.view.presentation.processing.internal;

import gestalt.impl.jogl.shape.JoglDrawableFactory;
import gestalt.render.Drawable;

import org.cytoscape.view.presentation.processing.CyDrawableFactory;


/**
 * Factory for shapes.
 * 
 * @author kono
 *
 */
public class CyDrawableFactoryImpl extends JoglDrawableFactory implements
		CyDrawableFactory {
	
	public CyDrawableFactoryImpl() {
		super();
	}

	public Drawable getDrawable(Class<? extends Drawable> type) {
		return null;
	}

}
