
package org.cytoscape.model.events;

import org.cytoscape.event.CyEvent;
import org.cytoscape.model.CyDataTable;

/**
 * This event signals that an Attribute has been deleted.
 *<p>
 * We might want to change the name of this guy to AttributeToBeDeleted so
 * that it is clear when it should be fired.
 */
public interface ColumnDeletedEvent extends CyEvent<CyDataTable> {

	/**
	 * @return The name of the attribute that has been deleted.
	 */
    public String getColumnName();

}

