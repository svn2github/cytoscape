package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.presentation.internal.property.NullDataTypeImpl;

/**
 * Visual Property for root. This will not be used in actual visualization. Just
 * a marker node in the tree.
 * 
 */
public class NullVisualProperty extends AbstractVisualProperty<NullDataType> {

	private static final NullDataType dummyObject = new NullDataTypeImpl();

	public NullVisualProperty(final String id, final String name) {
		super(dummyObject, NullDataType.class, id, name, Object.class);
	}

	public String toSerializableString(final NullDataType value) {
		return value.toString();
	}

	public NullDataType parseSerializableString(final String text) {
		return dummyObject;
	}
}