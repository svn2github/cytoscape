package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public interface TableCellTaskContext {

	/** Used to provision this factory with a {@link CyColumn} and a primary key that will be
	 *  used to create tasks.
	 *  @param column  a non-null CyColumn
	 *  @param primaryKeyValue  a non-null primary key value
	 */
	void setColumnAndPrimaryKey(CyColumn column, Object primaryKeyValue);

	CyColumn getColumn();
	Object getPrimaryKeyValue();
}
