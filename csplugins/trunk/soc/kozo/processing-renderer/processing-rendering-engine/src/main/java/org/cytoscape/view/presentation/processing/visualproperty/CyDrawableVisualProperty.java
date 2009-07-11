package org.cytoscape.view.presentation.processing.visualproperty;

import org.cytoscape.view.model.AbstractVisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;

public class CyDrawableVisualProperty extends
		AbstractVisualProperty<CyDrawable> {

	public CyDrawableVisualProperty(String ot, CyDrawable def, String id,
			String name) {
		super(ot, def, id, name);
	}

	public String getSerializableString(CyDrawable value) {
		return value.toString();
	}

	public CyDrawable parseSerializableString(String value) {
		// TODO Parser required.
		return null;
	}

}
