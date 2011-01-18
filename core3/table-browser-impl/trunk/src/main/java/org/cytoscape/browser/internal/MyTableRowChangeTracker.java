package org.cytoscape.browser.internal;


import java.util.List;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableRowChangeTracker;
import org.cytoscape.model.CyTableRowChangeTracker.RowUpdate;
import org.cytoscape.service.util.CyServiceRegistrar;


class MyTableRowChangeTracker extends CyTableRowChangeTracker {
	private final BrowserTableModel browserTableModel;

	MyTableRowChangeTracker(final CyTable table, final CyEventHelper eventHelper,
				final CyServiceRegistrar serviceRegistrar,
				final BrowserTableModel browserTableModel)
	{
		super(table, eventHelper, serviceRegistrar);
		this.browserTableModel = browserTableModel;
	}

	public final void rowCreated(final CyRow newRow) {
		browserTableModel.rowCreated(newRow);
	}

	public final void rowsUpdated(final List<RowUpdate> updates) {
		for (final RowUpdate update : updates)
			browserTableModel.handleRowUpdate(update.getRow(), update.getColumnName(),
							  update.getNewValue(),
							  update.getNewRawValue());
	}
}