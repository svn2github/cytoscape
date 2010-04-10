package cytoscape.visual.mappings.custom;

import cytoscape.visual.customgraphic.CyCustomGraphics;

public interface CustomGraphicsBuilder<T> {
	public CyCustomGraphics<?> getGraphics(T data);
}
