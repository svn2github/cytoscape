package org.cytoscape.view.presentation.processing;

import processing.core.PApplet;

public interface CyDrawableManager {

	public void registerDrawableFactory(CyDrawableFactory<? extends CyDrawable> factory);

	public <T extends CyDrawable> T getDrawable(Class<T> drawable);

	/**
	 * Returns default factory for the graph object
	 * 
	 * @param objectType
	 *            NODE, EDGE, or NETWORK.
	 * 
	 * @return
	 */
	public CyDrawableFactory<?> getDefaultFactory(String objectType);

	public void setFactoryParent(PApplet parent);

}
