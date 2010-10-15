package org.cytoscape.view.vizmap.mappings;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

public class DiscreteMappingFactory implements
		VisualMappingFunctionFactory {

	@Override
	public <K, V> VisualMappingFunction<K, V> createVisualMappingFunction(final String attributeName,
			Class<K> attrValueType, final VisualProperty<V> vp) {
		
		return new DiscreteMapping<K, V>(attributeName, attrValueType, vp);
	}
	
	@Override public String toString() {
		return DiscreteMapping.DISCRETE;
	}

}
