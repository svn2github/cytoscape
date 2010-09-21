package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.Visualizable;

public class DefaultVisualizableVisualProperty extends AbstractVisualProperty<Visualizable> {

	public DefaultVisualizableVisualProperty(final String id, final String name) {
		super(null, id, name);
	}

	
	@Override
	public String toSerializableString(final Visualizable value) {
		return value.toString();
	}

	
	@Override
	public Visualizable parseSerializableString(final String text) {
		// TODO: what should I return?
		return null;
	}

}