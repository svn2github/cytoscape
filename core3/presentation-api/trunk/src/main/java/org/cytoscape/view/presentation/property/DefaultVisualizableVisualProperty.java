package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.Visualizable;

public class DefaultVisualizableVisualProperty extends AbstractVisualProperty<Visualizable> implements AppendableVisualProperty {

	public DefaultVisualizableVisualProperty(final String id, final String name, final VisualProperty<?> parent) {
		super(null, id, name, parent);
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

	@Override
	public void setParent(final VisualProperty<?> parent) {
		this.parent = parent;
	}
}