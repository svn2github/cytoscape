package org.cytoscape.view.model;

import org.cytoscape.model.CyNode;

public class DummyVisualProperty extends AbstractVisualProperty<DummyObject> {


	public DummyVisualProperty(DummyObject defaultValue, String id, String displayName) {
		super(defaultValue, DummyObject.class, id, displayName, CyNode.class);
	}


	@Override
	public String toSerializableString(DummyObject value) {
		return "test";
	}

	@Override
	public DummyObject parseSerializableString(String value) {
		return new DummyObject();
	}

}
