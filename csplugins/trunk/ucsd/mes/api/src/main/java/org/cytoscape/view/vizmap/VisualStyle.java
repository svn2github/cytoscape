
package org.cytoscape.view.vizmap;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.View;

/**
 * This is simply a collection of MappingCalculators that define
 * how a set of attributes modify the visual properties of a
 * View object.
 */
public interface VisualStyle {
	public void setMappingCalculator(MappingCalculator c);
	public MappingCalculator getMappingCalculator(VisualProperty t);

	public View getDefaultView();
	public void setDefaultView(View v);

	public void apply(View v);
}

