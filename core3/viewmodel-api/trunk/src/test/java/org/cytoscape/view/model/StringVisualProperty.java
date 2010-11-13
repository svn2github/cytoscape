package org.cytoscape.view.model;

import org.cytoscape.model.CyNode;

public class StringVisualProperty extends AbstractVisualProperty<String> {

	public StringVisualProperty() {
		// isolated node. No parent/children.
		super("test", String.class, "string", "String Visual Property", CyNode.class);
	}

	public String parseSerializableString(final String text) {
		return text;
	}

	@Override
	public String toSerializableString(String value) {
		return value;
	}
}