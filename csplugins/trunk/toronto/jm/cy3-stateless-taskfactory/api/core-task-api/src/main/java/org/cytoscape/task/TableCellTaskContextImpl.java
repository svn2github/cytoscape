package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public class TableCellTaskContextImpl implements TableCellTaskContext {
	/** The {@link CyColumn} of the cell that will be used to provision tasks that are being created by descendants 
	 * of this class. */
	protected CyColumn column;
	/** The primary key of the cell that will be used to provision tasks that are being created by descendants 
	 * of this class. */
	protected Object primaryKeyValue;

	@Override
	public void setColumnAndPrimaryKey(final CyColumn column, final Object primaryKeyValue) {
		if (column == null)
			throw new  NullPointerException("\"column\" parameter must *never* be null!");
		this.column = column;
		if (primaryKeyValue == null)
			throw new NullPointerException("\"primaryKeyValue\" parameter must *never* be null!");
		this.primaryKeyValue = primaryKeyValue;
	}
	
	@Override
	public CyColumn getColumn() {
		return column;
	}
	
	@Override
	public Object getPrimaryKeyValue() {
		return primaryKeyValue;
	}
}
