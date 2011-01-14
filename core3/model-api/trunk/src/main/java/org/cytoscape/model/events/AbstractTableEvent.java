package org.cytoscape.model.events;


import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyTable;


/**
 *  Base class for all derived concrete event classes classes in this package that require a CyTable.
 */
class AbstractTableEvent extends AbstractCyEvent<Object> {
	private final CyTable table;

	AbstractTableEvent(final Object source, final Class<?> listenerClass, final CyTable table) {
		super(source, listenerClass);

		if (table == null)
			throw new NullPointerException("the \"table\" parameter must never be null!");
		this.table = table;
	}

	public final CyTable getTable() {
		return table;
	}
}
