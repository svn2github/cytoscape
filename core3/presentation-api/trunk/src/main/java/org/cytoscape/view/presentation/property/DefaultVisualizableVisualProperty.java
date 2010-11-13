package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.Visualizable;
import org.cytoscape.view.presentation.internal.property.VisualizableImpl;

/**
 * Visual Property to represent abstract concept such as Node or Edge. If
 * rendering engine have this visual property in the lexicon tree and if it's a
 * leaf, it should render it with default settings.
 * 
 */
public class DefaultVisualizableVisualProperty extends
		AbstractVisualProperty<Visualizable> {

	private static final Visualizable visualizable = new VisualizableImpl();

	public DefaultVisualizableVisualProperty(final String id, final String name, final Class<?> targetDataType) {
		super(visualizable, Visualizable.class, id, name, targetDataType);
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