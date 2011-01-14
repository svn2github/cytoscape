package org.cytoscape.browser.internal;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.browser.internal.TableRowChangeListener.ChangeType;
import org.cytoscape.browser.internal.TableRowChangeListener.ChangeType.*;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;


public final class TableRowChangeTracker implements RowCreatedMicroListener {
	private final CyTable table;
	private final TableRowChangeListener listener;
	private final Map<CyRow, RowSetMicroListenerProxy> rowToListenerProxyMap;
	private final CyEventHelper eventHelper;

	/** @param table     the table whose row updates we're forwarding
	 *  @param listener  the object whose handleTableEntryUpdate() method we're calling for each
	 *                   row update
	 */
	public TableRowChangeTracker(final CyTable table, final TableRowChangeListener listener,
				     final CyEventHelper eventHelper)
	{
		this.table = table;
		this.listener = listener;
		this.eventHelper = eventHelper;
		this.rowToListenerProxyMap = new HashMap<CyRow, RowSetMicroListenerProxy>();

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, table);

		final List<CyRow> rows = table.getAllRows();
		for (final CyRow row : rows)
			rowToListenerProxyMap.put(row, new RowSetMicroListenerProxy(listener, eventHelper, row));
	}

	@Override
	public void handleRowCreated(final Object key) {
		final CyRow newRow = table.getRow(key);
		rowToListenerProxyMap.put(
			newRow, new RowSetMicroListenerProxy(listener, eventHelper, newRow));
		listener.handleTableEntryUpdate(newRow, null, null, null, ChangeType.ROW_CREATED);
	}

	/** Unregisters all listeners. */
	public void cleanup() {
		eventHelper.removeMicroListener(this, RowCreatedMicroListener.class, table);
		for (final RowSetMicroListenerProxy proxy : rowToListenerProxyMap.values())
			proxy.cleanup();
	}


	private static class RowSetMicroListenerProxy implements RowSetMicroListener {
		private final TableRowChangeListener listener;
		private final CyEventHelper eventHelper;
		private final CyRow row;

		RowSetMicroListenerProxy(final TableRowChangeListener listener,
					 final CyEventHelper eventHelper, final CyRow row)
		{
			this.listener = listener;
			this.eventHelper = eventHelper;
			this.row = row;

			eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
		}

		@Override
			public void handleRowSet(final String columnName, final Object newValue, final Object newRawValue) {
			listener.handleTableEntryUpdate(row, columnName, newValue, newRawValue,
							ChangeType.ROW_UPDATED);
		}

		void cleanup() {
			eventHelper.removeMicroListener(this, RowSetMicroListener.class, row);
		}
	}
}
