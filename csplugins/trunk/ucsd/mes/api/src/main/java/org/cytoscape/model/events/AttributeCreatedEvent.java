
package org.cytoscape.model.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.attrs.CyAttributesManager;

/**
 * This event signals that an Attribute has been created.
 *<p>
 * This should probably return the type parameter as well. 
 */
public interface AttributeCreatedEvent extends CyEvent<CyAttributesManager> {

	/**
	 * @return The name of the attribute that has been deleted.
	 */
    public String getAttributeName();

}

