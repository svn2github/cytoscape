
package org.cytoscape.model.attrs.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.attrs.CyAttributes;

/**
 * This event signals that an Attribute has been deleted.
 *<p>
 * We might want to change the name of this guy to AttributeToBeDeleted so
 * that it is clear when it should be fired.
 */
public interface AttributeDeletedEvent extends CyEvent<CyAttributes> {

	/**
	 * @return The name of the attribute that has been deleted.
	 */
    public String getAttributeName();

}

