package org.cytoscape.browser.internal;


import java.util.List;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableRowChangeTracker;
import org.cytoscape.model.CyTableRowChangeTracker.RowUpdate;


class MyTableRowChangeTracker extends CyTableRowChangeTracker {
	private final BrowserTableModel browserTableModel;

	MyTableRowChangeTracker(final CyTable table, final CyEventHelper eventHelper,
				final BrowserTableModel browserTableModel)
	{
		super(table, eventHelper);
		this.browserTableModel = browserTableModel;
	}

	public void rowCreated(final CyRow newRow) {
		browserTableModel.rowCreated(newRow);
	}

	public void rowsUpdated(final List<RowUpdate> updates) {
		for (final RowUpdate update : updates)
			browserTableModel.handleRowUpdate(update.getRow(), update.getColumnName(),
							  update.getNewValue(),
							  update.getNewRawValue());
	}
}