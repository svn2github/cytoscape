package org.cytoscape.browser.internal;


import org.cytoscape.model.CyRow;


/** Listens to row update events. */
interface TableRowChangeListener {
	enum ChangeType { ROW_UPDATED, ROW_CREATED }

	/**
	 *  @param row          the row that has been updated
	 *  @param columnName   the column that has been updated
	 *  @param newValue     the new value of the changed table entry
	 *  @param newRawValue  the new raw value of the changed table entry
	 *  @param changeType   what type of change took place
	 */
	void handleTableEntryUpdate(CyRow row, String columnName, Object newValue,
				    Object newRawValue, ChangeType changeType);
}
