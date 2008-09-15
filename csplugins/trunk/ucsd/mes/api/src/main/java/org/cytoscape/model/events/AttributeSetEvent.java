
package org.cytoscape.model.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.attrs.CyAttributes;

/**
 * This event signals that an attribute has been set.
 */
public interface AttributeSetEvent extends CyEvent<CyAttributes> {

	/**
	 * @return The name of the attribute that has been set.
	 */
    public String getAttributeName();
}

