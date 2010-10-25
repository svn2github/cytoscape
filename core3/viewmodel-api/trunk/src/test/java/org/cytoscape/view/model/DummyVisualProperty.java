package org.cytoscape.view.model;

public class DummyVisualProperty extends AbstractVisualProperty<DummyObject> {

	public DummyVisualProperty(DummyObject defaultValue, String id, String name) {
		super(defaultValue, DummyObject.class, id, name);
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
