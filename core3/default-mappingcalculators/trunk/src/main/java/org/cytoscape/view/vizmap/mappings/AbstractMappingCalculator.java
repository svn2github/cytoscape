package org.cytoscape.view.vizmap.mappings;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.MappingCalculator;

public abstract class AbstractMappingCalculator<K, V> implements
		MappingCalculator<K, V> {

	// Mapping attribute name.
	protected String attrName;
	
	// Type of attribute
	protected Class<K> attrType;

	// Visual Property used in this mapping.
	protected VisualProperty<V> vp;

	public AbstractMappingCalculator(final String attrName, final Class<K> attrType,
			final VisualProperty<V> vp) {
		this.attrType = attrType;
		this.attrName = attrName;
		this.vp = vp;
	}

	public String getMappingAttributeName() {
		return attrName;
	}

	public Class<K> getMappingAttributeType() {
		return attrType;
	}

	public VisualProperty<V> getVisualProperty() {
		return vp;
	}

	public void setMappingAttributeName(String attrName) {
		this.attrName = attrName;
	}

	public void setVisualProperty(VisualProperty<V> vp) {
		this.vp = vp;
	}
}
