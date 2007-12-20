
package org.cytoscape.view;

public interface VisualStyle {
	void setMappingCalculator(MappingCalculator c);
	MappingCalculator getMappingCalculator(VisualProperty t);

	View getDefaultView();
	void setDefaultView(View v);
}

