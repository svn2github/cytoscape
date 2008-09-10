
package org.cytoscape.view.model2.vizmap;

import org.cytoscape.view.model2.VisualProperty;
import org.cytoscape.view.model2.View;

/**
 * This class defines how an attribute gets mapped to a visual property.
 * It takes two values: an attribute and a visual property and provides
 * the mapping function from converting the attribute to the visual
 * property.
 *
 * Or should the mapping calculator map from Attr to Class<?>?
 */
public interface MappingCalculator {

	/**
	 * The attribute to be mapped. 
	 */
	public void setMappingAttributeName(String name);
	public String getMappingAttributeName();

	/**
	 * The visual property the attribute gets mapped to.
	 */
	public void setVisualProperty(VisualProperty<?> vp);
	public VisualProperty<?> getVisualProperty();

	// ??
	public void apply(View<?> v);
}	

