package org.cytoscape.vizmap.gui.util;

import java.util.Map;
import java.util.Set;

public interface DiscreteValueMapGenerator<V> {
	
	public Map<Object, V> generateMap(Set<Object> attributeSet);
}
