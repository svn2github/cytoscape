package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public class TableColumnTaskContextImpl implements TableColumnTaskContext {
	protected CyColumn column;

	@Override
	public void setColumn(final CyColumn column) {
		this.column = column;
	}
	
	@Override
	public CyColumn getColumn() {
		return column;
	}
}
