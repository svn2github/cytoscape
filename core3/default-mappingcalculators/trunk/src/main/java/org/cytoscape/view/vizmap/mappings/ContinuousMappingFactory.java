package org.cytoscape.view.vizmap.mappings;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

public class ContinuousMappingFactory implements VisualMappingFunctionFactory {

	@Override
	public <K, V> VisualMappingFunction<K, V> createVisualMappingFunction(final String attributeName, 
			Class<K> attrValueType, VisualProperty<V> vp) {
		
		// Validate attribute type: Continuous Mapping is compatible with Numbers only.
		if(attrValueType.isAssignableFrom(Number.class) == false)
			throw new IllegalArgumentException("ContinuousMapping can be used for numerical attributes only.");
		
		return new ContinuousMapping<K, V>(attributeName, attrValueType,  vp);
	}
	
	@Override public String toString() {
		return ContinuousMapping.CONTINUOUS;
	}

	@Override
	public Class<?> getMappingFunctionType() {
		return ContinuousMapping.class;
	}

}
