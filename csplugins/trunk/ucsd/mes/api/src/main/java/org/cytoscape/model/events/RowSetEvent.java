
package org.cytoscape.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyRow;

/**
 * This event signals that an attribute has been set.
 */
public interface RowSetEvent extends CyEvent<CyRow> {

	/**
	 * @return The name of the attribute that has been set.
	 */
    public String getColumnName();
}

