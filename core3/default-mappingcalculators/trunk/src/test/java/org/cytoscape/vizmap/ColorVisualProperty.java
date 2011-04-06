package org.cytoscape.vizmap;

import java.awt.Color;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.model.ContinuousRange;
import org.cytoscape.view.model.Range;

public class ColorVisualProperty extends AbstractVisualProperty<Color> {

	
	private static final Color MIN_COLOR = new Color(0, 0, 0);
	private static final Color MAX_COLOR = new Color(0xFF, 0xFF, 0xFF);
	private static final Range<Color> COLOR_RANGE = new ContinuousRange<Color>(Color.class, MIN_COLOR, MAX_COLOR);
	
	public ColorVisualProperty(final Color defaultValue,
			final String id, final String name) {
		super(defaultValue, COLOR_RANGE, id, name, CyNode.class);
	}

	public Color parseSerializableString(final String text) {
		// This is dummy and not work, but it's ok for mapping tests.
		return Color.decode(text);
	}

	@Override
	public String toSerializableString(Color value) {
		return value.toString();
	}
}