package cytoscape.visual.mappings;

import cytoscape.visual.customgraphic.CyCustomGraphics;

public interface CustomGraphicsRangeValueRenderer<T> {
	public CyCustomGraphics<?> create(final T value);
}
