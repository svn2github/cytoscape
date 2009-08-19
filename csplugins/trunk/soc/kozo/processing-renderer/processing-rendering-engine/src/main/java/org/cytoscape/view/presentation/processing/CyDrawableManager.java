package org.cytoscape.view.presentation.processing;

import java.util.List;

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

	public List<P5Shape> getP5Shapes();
	
	public void setFactoryParent(PApplet parent);

}
