package org.cytoscape.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.events.RowSetAboutToBeChangedEvent;
import org.cytoscape.model.events.RowSetAboutToBeChangedListener;
import org.cytoscape.model.events.RowSetChangedEvent;
import org.cytoscape.model.events.RowSetChangedListener;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;


/** This class simplifies tracking of CyRow creation and update events for a single CyTable.  In
 *  order to utilise it, you must override the rowCreated() and rowsUpdated() methods.
 */
public abstract class CyTableRowChangeTracker
	implements RowCreatedMicroListener, RowSetAboutToBeChangedListener, RowSetChangedListener
{
	public static class RowUpdate {
		private final CyRow row;
		private final String columnName;
		private final Object newValue;
		private final Object newRawValue;

		RowUpdate(final CyRow row, final String columnName, final Object newValue,
			  final Object newRawValue)
		{
			this.row = row;
			this.columnName = columnName;
			this.newValue = newValue;
			this.newRawValue = newRawValue;
		}

		public final CyRow getRow() { return row; }
		public final String getColumnName() { return columnName; }
		public final Object getNewValue() { return newValue; }
		public final Object getNewRawValue() { return newRawValue; }
	}

	private final CyTable table;
	private final Map<CyRow, RowSetMicroListenerProxy> rowToListenerProxyMap;
	private final CyEventHelper eventHelper;
	private List<RowUpdate> rowUpdates;
	private int numConcurrentUpdaters;

	/** @param table        the table whose row updates we're forwarding
	 *  @param eventHelper  used to set up event tracking
	 */
	public CyTableRowChangeTracker(final CyTable table, final CyEventHelper eventHelper) {
		this.table = table;
		this.eventHelper = eventHelper;
		this.rowToListenerProxyMap = new HashMap<CyRow, RowSetMicroListenerProxy>();
		this.rowUpdates = new ArrayList<RowUpdate>();
		this.numConcurrentUpdaters = 0;

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, table);

		final List<CyRow> rows = table.getAllRows();
		for (final CyRow row : rows)
			rowToListenerProxyMap.put(row, new RowSetMicroListenerProxy(this, eventHelper, row));
	}

	private final synchronized void rowUpdated(final CyRow row, final String columnName,
						   final Object newValue, final Object newRawValue)
	{
		rowUpdates.add(new RowUpdate(row, columnName, newValue, newRawValue));
		if (numConcurrentUpdaters == 0) {
			rowsUpdated(rowUpdates);
			rowUpdates.clear();
		}
	}

	@Override
	public final void handleRowCreated(final Object key) {
		final CyRow newRow = table.getRow(key);
		rowToListenerProxyMap.put(
			newRow, new RowSetMicroListenerProxy(this, eventHelper, newRow));
		rowCreated(newRow);
	}

	@Override
	public final synchronized void handleEvent(final RowSetAboutToBeChangedEvent e) {
		if (e.getTable() == table)
			++numConcurrentUpdaters;
	}

	@Override
	public final synchronized void handleEvent(final RowSetChangedEvent e) {
		if (e.getTable() == table) {
			--numConcurrentUpdaters;
			if (numConcurrentUpdaters == 0) {
				rowsUpdated(rowUpdates);
				rowUpdates.clear();
			}
		}
	}

	/** Override this to be notified of newly created rows.
	 *
	 *  @param newRow  the newly created row
	 */
	public abstract void rowCreated(CyRow newRow);

	/** Override this to be notified of row updates.
	 *
	 *  @param updates  information about all the recent row changes
	 */
	public abstract void rowsUpdated(List<RowUpdate> updates);

	/** Unregisters all listeners. */
	public final void cleanup() {
		eventHelper.removeMicroListener(this, RowCreatedMicroListener.class, table);
		for (final RowSetMicroListenerProxy proxy : rowToListenerProxyMap.values())
			proxy.cleanup();
	}


	private static class RowSetMicroListenerProxy implements RowSetMicroListener {
		private final CyTableRowChangeTracker changeTracker;
		private final CyEventHelper eventHelper;
		private final CyRow row;

		RowSetMicroListenerProxy(final CyTableRowChangeTracker changeTracker,
					 final CyEventHelper eventHelper, final CyRow row)
		{
			this.changeTracker = changeTracker;
			this.eventHelper = eventHelper;
			this.row = row;

			eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
		}

		@Override
		public void handleRowSet(final String columnName, final Object newValue,
					 final Object newRawValue)
		{
			changeTracker.rowUpdated(row, columnName, newValue, newRawValue);
		}

		void cleanup() {
			eventHelper.removeMicroListener(this, RowSetMicroListener.class, row);
		}
	}
}
