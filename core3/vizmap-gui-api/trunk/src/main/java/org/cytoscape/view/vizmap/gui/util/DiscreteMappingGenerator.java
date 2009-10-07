package org.cytoscape.view.vizmap.gui.util;

import java.util.Map;
import java.util.Set;

public interface DiscreteMappingGenerator<V> {
	
	public <T> Map<T, V> generateMap(Set<T> attributeSet);
}
