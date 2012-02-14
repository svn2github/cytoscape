package org.cytoscape.task;

import org.cytoscape.model.CyRow;

public interface RowTaskContext {

	/** Used to provision this class with a {@link CyRow} that will be passed into any task
	 *  constructed by this factory.
	 *  @param row  a non-null CyRow
	 */
	void setRow(CyRow row);

	CyRow getRow();
}
