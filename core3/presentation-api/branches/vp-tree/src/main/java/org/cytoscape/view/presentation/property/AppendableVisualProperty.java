package org.cytoscape.view.presentation.property;

import org.cytoscape.view.model.VisualProperty;

public interface AppendableVisualProperty {
	void setParent(final VisualProperty<?> parent);
}
