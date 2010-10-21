package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.presentation.internal.property.NullDataTypeImpl;

public class NullVisualProperty extends AbstractVisualProperty<NullDataType> {

	private static final NullDataType dummyObject = new NullDataTypeImpl();
	
	public NullVisualProperty(final String id, final String name) {
		super(dummyObject, id, name);
	}

	public String toSerializableString(final NullDataType value) {
		return value.toString();
	}

	public NullDataType parseSerializableString(final String text) {
		return dummyObject;
	}
}