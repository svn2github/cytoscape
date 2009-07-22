package org.cytoscape.view.presentation.processing;

public interface CyDrawableFactory {

	public void registerDrawable(Class<? extends CyDrawable> drawable,
			String displayName);
	
	public CyDrawable getDrawable(Class<? extends CyDrawable> drawable);
	
	
}
