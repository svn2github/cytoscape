package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.NullDataType;

public class NullVisualProperty extends AbstractVisualProperty<NullDataType> {

	public NullVisualProperty(final String id, final String name) {
		super(null, id, name, null);
	}

	public String toSerializableString(final NullDataType value) {
		return value.toString();
	}

	public NullDataType parseSerializableString(final String text) {
		return null;
	}
}