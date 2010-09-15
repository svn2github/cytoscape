package org.cytoscape.view.model.visualproperties;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.Visualizable;

public class DefaultVisualizableVisualProperty extends AbstractVisualProperty<Visualizable> {

	public DefaultVisualizableVisualProperty(final String id, final String name) {
		super(null, id, name);
	}

	public String toSerializableString(final Visualizable value) {
		return value.toString();
	}

	public Visualizable parseSerializableString(final String text) {
		// TODO: what should I return?
		return null;
	}
}