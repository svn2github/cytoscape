package cytoscape.visual.mappings.custom;

import cytoscape.visual.customgraphic.CyCustomGraphics;

/**
 * Dynamic custom graphics generator interface.
 * Based on a given data set, create custom graphics.
 * 
 * @author kono
 *
 * @param <T>
 */
public interface CustomGraphicsBuilder {
	public CyCustomGraphics<?> getGraphics(Object data);
}
