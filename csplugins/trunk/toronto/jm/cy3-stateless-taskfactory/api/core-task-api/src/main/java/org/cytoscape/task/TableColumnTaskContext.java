package org.cytoscape.task;

import org.cytoscape.model.CyColumn;

public interface TableColumnTaskContext {

	/** Used to provision this factory with a {@link CyColumn} that will be used to create tasks.
	 *  @param column a non-null CyColumn.
	 */
	void setColumn(CyColumn column);

	CyColumn getColumn();
}
