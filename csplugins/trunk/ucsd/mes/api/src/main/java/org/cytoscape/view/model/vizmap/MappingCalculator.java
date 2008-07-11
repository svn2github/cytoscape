
package org.cytoscape.view.model.vizmap;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.View;

/**
 * This class defines how an attribute gets mapped to a visual property.
 * It takes two values: an attribute and a visual property and provides
 * the mapping function from converting the attribute to the visual
 * property.
 */
public interface MappingCalculator {

	public void setMappingAttributeName(String name);
	public String getMappingAttributeName();

	public void setVisualProperty(VisualProperty vp);
	public VisualProperty getVisualProperty();

	public void apply(View v);
}	

