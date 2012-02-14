package org.cytoscape.task;

import org.cytoscape.model.CyTable;

public interface TableTaskContext {

	/** Provisions this factory with a table that will be used to construct tasks.
	 *  @param table the {@link CyTable} to be passed into <code>Task</code> constructors; <b>must</b> not be null!
	 */
	void setTable(CyTable table);

	CyTable getTable();
}
