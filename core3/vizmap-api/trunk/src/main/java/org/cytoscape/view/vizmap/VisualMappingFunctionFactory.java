package org.cytoscape.view.vizmap;

import org.cytoscape.view.model.VisualProperty;

public interface VisualMappingFunctionFactory {
	
	/**
	 * Create a new VisualMapping Function.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param functionType
	 * @param attrValueType
	 * @param attributeName
	 * @param vp
	 * @return
	 */
	<K, V> VisualMappingFunction<K, V> createVisualMappingFunction(final String attributeName, final Class<K> attrValueType, final VisualProperty<V> vp);
}
