package org.cytoscape.view.model;

public class StringVisualProperty extends AbstractVisualProperty<String> {

	public StringVisualProperty(final String type, final String defaultValue,
			final String id, final String name) {
		super(type, defaultValue, id, name);
	}

	public String parseSerializableString(final String text) {
		return text;
	}

	@Override
	public String toSerializableString(String value) {
		return value;
	}
}