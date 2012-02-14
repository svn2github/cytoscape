package org.cytoscape.task;

import org.cytoscape.model.CyTable;

public class TableTaskContextImpl implements TableTaskContext {
	/** The table that will be passed into any Task constructor.
	 */
	protected CyTable table;

	@Override
	public void setTable(final CyTable table) {
		if (table == null)
			throw new NullPointerException("CyTable is null");

		this.table = table;
	}
	
	@Override
	public CyTable getTable() {
		return table;
	}
}
