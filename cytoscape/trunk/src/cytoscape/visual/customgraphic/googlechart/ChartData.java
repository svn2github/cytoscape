package cytoscape.visual.customgraphic.googlechart;

import java.util.Collection;

public interface ChartData {
	public <T extends Number> String encodeData(final Collection<T> data);
}
