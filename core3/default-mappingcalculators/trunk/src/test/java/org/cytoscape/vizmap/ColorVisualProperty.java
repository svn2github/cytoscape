package org.cytoscape.vizmap;

import java.awt.Color;

import org.cytoscape.view.model.AbstractVisualProperty;

public class ColorVisualProperty extends AbstractVisualProperty<Color> {

	public ColorVisualProperty(final Color defaultValue,
			final String id, final String name) {
		super(defaultValue, id, name);
	}

	public Color parseSerializableString(final String text) {
		// This is dummy and not work, but it's ok for mapping tests.
		return Color.decode(text);
	}

	@Override
	public String toSerializableString(Color value) {
		return value.toString();
	}
}