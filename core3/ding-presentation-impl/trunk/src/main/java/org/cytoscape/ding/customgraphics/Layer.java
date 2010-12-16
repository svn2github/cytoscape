package org.cytoscape.ding.customgraphics;


/**
 * Wrapper for actual implementations of layers.
 * In current version, it's always CustomGraphic 
 *
 */
public interface Layer {
	
	/**
	 * Each layer has immutable Z-Order value for rendering.
	 * This method returens the value as int.
	 * 
	 * @return
	 */
	public int getZorder();
	
	public Object getLayerObject();
}
