package org.cytoscape.view.model.internal;

import java.awt.Color;

import org.cytoscape.view.model.VisualPropertyDependecyCalculator;

public class ColorPassthroughConverter implements VisualPropertyDependecyCalculator<Color> {

	@Override
	public Color convert(Color value) {
		// Do nothing.  Just use the same color.
		return value;
	}

}
