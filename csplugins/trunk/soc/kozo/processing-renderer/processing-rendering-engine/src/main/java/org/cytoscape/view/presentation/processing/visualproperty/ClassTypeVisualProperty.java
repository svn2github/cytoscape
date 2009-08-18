package org.cytoscape.view.presentation.processing.visualproperty;

import org.cytoscape.view.model.AbstractVisualProperty;

public class ClassTypeVisualProperty extends
		AbstractVisualProperty<Class<?>> {

	public ClassTypeVisualProperty(String ot, Class<?> def, String id,
			String name) {
		super(ot, def, id, name);
	}

	public String getSerializableString(Class<?> value) {
		return value.toString();
	}

	public Class<?> parseSerializableString(String value) {
		// TODO Parser required.
		return null;
	}

}