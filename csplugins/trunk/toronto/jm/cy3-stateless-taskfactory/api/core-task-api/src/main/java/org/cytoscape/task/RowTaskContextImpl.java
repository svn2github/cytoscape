package org.cytoscape.task;

import org.cytoscape.model.CyRow;

public class RowTaskContextImpl implements RowTaskContext {
	/** The CyRow that will be passed into any task that will be created by descendants of this class. */
	protected CyRow row;

	@Override
	public void setRow(CyRow row) {
		if (row == null)
			throw new NullPointerException("CyRow is null");

		this.row = row;
	}
	
	@Override
	public CyRow getRow() {
		return row;
	}
}
