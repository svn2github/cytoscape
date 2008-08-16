package org.cytoscape.view;

import java.util.Collection;
import java.util.Set;

public abstract class DependentVisualPropertyCallback {
	/** Called when mapping results change.
	 * @return the set of VisualProperties to hide
	 */
	public abstract Set<VisualProperty> changed(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews, Collection<VisualProperty> current_vps);
}
