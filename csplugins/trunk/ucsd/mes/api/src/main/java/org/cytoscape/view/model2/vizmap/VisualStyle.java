
package org.cytoscape.view.model2.vizmap;

import org.cytoscape.view.model2.VisualProperty;
import org.cytoscape.view.model2.CyNetworkView;

/**
 * This is simply a collection of MappingCalculators that define
 * how a set of attributes modify the visual properties of a
 * View object.
 */
public interface VisualStyle {

	public void setMappingCalculator(MappingCalculator c);
	public MappingCalculator getMappingCalculator(VisualProperty<?> t);

	public <T> T getDefault(VisualProperty<T> prop);
	public <T> void setDefault(VisualProperty<T> vp, T value);

	// ??
	public void apply(CyNetworkView v);
}

