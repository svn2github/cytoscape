package org.cytoscape.view.presentation.processing;

public interface CyDrawableManager {

	public void registerDrawable(Class<? extends CyDrawable> drawable,
			String displayName);
	
	public CyDrawable getDrawable(Class<? extends CyDrawable> drawable);
	
	
}
