package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.internal.property.VisualizableImpl;

public class DefaultVisualizableVisualProperty extends AbstractVisualProperty<Visualizable> {
	
	private static final Visualizable visualizable = new VisualizableImpl();

	public DefaultVisualizableVisualProperty(final String id, final String name) {
		super(visualizable, id, name);
	}

	
	@Override
	public String toSerializableString(final Visualizable value) {
		return value.toString();
	}

	
	@Override
	public Visualizable parseSerializableString(final String text) {
		return visualizable;
	}

}