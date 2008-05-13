
package org.cytoscape.view.vizmap;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.View;

public interface MappingCalculator {

	public void setMappingAttributeName(String name);
	public String getMappingAttributeName();

	public void setVisualProperty(VisualProperty vp);
	public VisualProperty getVisualProperty();

	public void apply(View v);
}	

