package org.cytoscape.view.model;

public class StringVisualProperty extends AbstractVisualProperty<String> {

	public StringVisualProperty(final String defaultValue, final String id,
			final String name, final VisualProperty<?> parent) {
		// isolated node. No parent/children.
		super(defaultValue, id, name, null);
	}

	public String parseSerializableString(final String text) {
		return text;
	}

	@Override
	public String toSerializableString(String value) {
		return value;
	}
}