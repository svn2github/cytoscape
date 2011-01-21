package org.cytoscape.model.internal;


import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableRowUpdateService;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.CyTableRowUpdateMicroListener;
import org.cytoscape.model.events.CyTableRowUpdateMicroListener.RowSet;
import org.cytoscape.model.events.RowsAboutToChangeEvent;
import org.cytoscape.model.events.RowsAboutToChangeListener;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;
import org.cytoscape.model.events.RowsFinishedChangingEvent;
import org.cytoscape.model.events.RowsFinishedChangingListener;
import org.cytoscape.service.util.CyServiceRegistrar;


class CyTableRowUpdateServiceImpl
	implements CyTableRowUpdateService, RowsAboutToChangeListener, RowsFinishedChangingListener
{
	private final CyEventHelper eventHelper;
	private final CyServiceRegistrar serviceRegistrar;
	private final Map<CyTable, Set<CyTableRowUpdateMicroListener>> tableToListenersMap;
	private final Map<CyTable, Integer> tableToRefCountMap;
	private final Map<CyTable, Set<RowSetMicroListenerProxy>> tableToProxyListenersMap;
	private final Map<CyTable, RowCreatedMicroListenerProxy> tableToRowCreatedMicroListenerMap;
	private final Map<CyTable, List<RowSet>> tableToRowSetsMap;
	private final Map<CyTable, List<CyRow>> tableToNewRowsMap;

	CyTableRowUpdateServiceImpl(final CyEventHelper eventHelper,
				    final CyServiceRegistrar serviceRegistrar)
	{
		this.eventHelper = eventHelper;
		this.serviceRegistrar = serviceRegistrar;
		tableToListenersMap = new HashMap<CyTable, Set<CyTableRowUpdateMicroListener>>();
		tableToRefCountMap = new HashMap<CyTable, Integer>();
		tableToProxyListenersMap = new HashMap<CyTable, Set<RowSetMicroListenerProxy>>();
		tableToRowCreatedMicroListenerMap = new HashMap<CyTable, RowCreatedMicroListenerProxy>();
		tableToRowSetsMap = new HashMap<CyTable, List<RowSet>>();
		tableToNewRowsMap = new HashMap<CyTable, List<CyRow>>();

		final Dictionary emptyProps = new Hashtable();
		serviceRegistrar.registerService(this, RowsAboutToChangeListener.class, emptyProps);
		serviceRegistrar.registerService(this, RowsFinishedChangingListener.class, emptyProps);
	}
	
	@Override
	public synchronized void startTracking(final CyTableRowUpdateMicroListener listener,
					       final CyTable table)
	{
		if (!tableToListenersMap.containsKey(table)) {
			tableToRowSetsMap.put(table, new ArrayList<RowSet>());
			tableToNewRowsMap.put(table, new ArrayList<CyRow>());
			tableToRefCountMap.put(table, new Integer(0));
			// Create proxy listeners for each row in the new table:
			final List<CyRow> rows = table.getAllRows();
			final Set<RowSetMicroListenerProxy> proxies =
				new HashSet<RowSetMicroListenerProxy>();
			for (final CyRow row : rows)
				proxies.add(new RowSetMicroListenerProxy(this, eventHelper, table, row));
			tableToProxyListenersMap.put(table, proxies);

			final RowCreatedMicroListenerProxy newProxy =
				new RowCreatedMicroListenerProxy(this, table);
			tableToRowCreatedMicroListenerMap.put(table, newProxy);
			eventHelper.addMicroListener(newProxy, RowCreatedMicroListener.class, table);
			tableToListenersMap.put(table, new HashSet<CyTableRowUpdateMicroListener>());
		}

		final Set<CyTableRowUpdateMicroListener> listeners = tableToListenersMap.get(table);
		if (listeners.contains(listener))
			return;

		eventHelper.addMicroListener(listener, CyTableRowUpdateMicroListener.class, this);
		listeners.add(listener);
	}

	@Override
	public synchronized void stopTracking(final CyTableRowUpdateMicroListener listener,
					      final CyTable table)
	{
		final Set<CyTableRowUpdateMicroListener> listeners = tableToListenersMap.get(table);
		if (listeners == null || !listeners.contains(table))
			return;

		listeners.remove(listener);
		if (listeners.isEmpty()) {
			final RowCreatedMicroListenerProxy creationListenerProxy =
				tableToRowCreatedMicroListenerMap.get(table);
			tableToRowCreatedMicroListenerMap.remove(table);
			eventHelper.removeMicroListener(creationListenerProxy,
							RowCreatedMicroListener.class, table);
			for (final RowSetMicroListenerProxy proxy : tableToProxyListenersMap.get(table))
				proxy.cleanup();
			tableToProxyListenersMap.remove(table);
			tableToListenersMap.remove(table);
			tableToRefCountMap.remove(table);
			tableToRowSetsMap.remove(table);
			tableToNewRowsMap.remove(table);
		}

		eventHelper.removeMicroListener(listener, CyTableRowUpdateMicroListener.class, this);
	}

	@Override
	public void handleEvent(final RowsAboutToChangeEvent e) {
		final CyTable table = e.getTable();
		if (!tableToListenersMap.containsKey(table))
			return;

		final int newRefCount = tableToRefCountMap.get(table) + 1;
		tableToRefCountMap.put(table, newRefCount);
	}

	@Override
	public void handleEvent(final RowsFinishedChangingEvent e) {
		final CyTable table = e.getTable();
		if (!tableToListenersMap.containsKey(table))
			return;

		final int newRefCount = tableToRefCountMap.get(table) - 1;
		if (newRefCount < 0)
			throw new IllegalStateException("reference counts should *never* drop to zero!");
		tableToRefCountMap.put(table, newRefCount);
		fireUpdateEvents(table);
	}

	private void fireUpdateEvents(final CyTable table) {
		if (tableToRefCountMap.get(table) == 0) {
			final List<CyRow> newRows = tableToNewRowsMap.get(table);
			if (!newRows.isEmpty()) {
				for (final CyTableRowUpdateMicroListener listener
					     : tableToListenersMap.get(table))
					listener.handleRowCreations(table, newRows);
				newRows.clear();
			}

			final List<RowSet> rowSets = tableToRowSetsMap.get(table);
			if (!rowSets.isEmpty()) {
				for (final CyTableRowUpdateMicroListener listener
					     : tableToListenersMap.get(table))
					listener.handleRowSets(table, rowSets);
				rowSets.clear();
			}
		}
	}

	private void rowUpdated(final CyTable table, final CyRow row, String columnName,
				final Object newValue, final Object newRawValue)
	{
 		final List<RowSet> rowSets = tableToRowSetsMap.get(table);
		rowSets.add(new RowSet(row, columnName, newValue, newRawValue));
		fireUpdateEvents(table);
	}

	private void rowCreated(final CyTable table, final CyRow row) {
		final Set<RowSetMicroListenerProxy> proxies = tableToProxyListenersMap.get(table);
		proxies.add(new RowSetMicroListenerProxy(this, eventHelper, table, row));

		final List<CyRow> newRows = tableToNewRowsMap.get(table);
		newRows.add(row);
		fireUpdateEvents(table);
	}

	private static final class RowSetMicroListenerProxy implements RowSetMicroListener {
		private final CyTableRowUpdateServiceImpl changeTracker;
		private final CyEventHelper eventHelper;
		private final CyTable table;
		private final CyRow row;

		RowSetMicroListenerProxy(final CyTableRowUpdateServiceImpl changeTracker,
					 final CyEventHelper eventHelper, final CyTable table,
					 final CyRow row)
		{
			this.changeTracker = changeTracker;
			this.eventHelper   = eventHelper;
			this.table         = table;
			this.row           = row;

			eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
		}

		@Override
		public void handleRowSet(final String columnName, final Object newValue,
					 final Object newRawValue)
		{
			changeTracker.rowUpdated(table, row, columnName, newValue, newRawValue);
		}

		void cleanup() {
			eventHelper.removeMicroListener(this, RowSetMicroListener.class, row);
		}
	}

	private static final class RowCreatedMicroListenerProxy implements RowCreatedMicroListener {
		private final CyTableRowUpdateServiceImpl tableRowUpdateServiceImpl;
		private final CyTable table;

		RowCreatedMicroListenerProxy(final CyTableRowUpdateServiceImpl tableRowUpdateServiceImpl,
					     final CyTable table)
		{
			this.tableRowUpdateServiceImpl = tableRowUpdateServiceImpl;
			this.table                     = table;
		}

		@Override
		public void handleRowCreated(final Object primaryKey) {
			tableRowUpdateServiceImpl.rowCreated(table, table.getRow(primaryKey));
		}
	}
}