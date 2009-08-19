package org.cytoscape.view.presentation.processing.visualproperty;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.presentation.processing.P5Shape;

public class P5ShapeVisualProperty extends
		AbstractVisualProperty<P5Shape> {

	public P5ShapeVisualProperty(String ot, P5Shape def, String id,
			String name) {
		super(ot, def, id, name);
	}

	public String getSerializableString(P5Shape value) {
		return value.toString();
	}

	public P5Shape parseSerializableString(String value) {
		// TODO Parser required.
		return null;
	}

}