
package org.cytoscape.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyDataTable;

/**
 * This event signals that an Attribute has been created.
 *<p>
 * This should probably return the type parameter as well. 
 */
public interface ColumnCreatedEvent extends CyEvent<CyDataTable> {

	/**
	 * @return The name of the attribute that has been deleted.
	 */
    public String getColumnName();

}

