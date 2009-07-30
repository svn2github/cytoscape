package org.cytoscape.view.presentation.processing;

import org.cytoscape.view.model.View;

public interface EdgeItem {
	public void setSource(View<?> sourceView);
	public void setTarget(View<?> targetView);
}
