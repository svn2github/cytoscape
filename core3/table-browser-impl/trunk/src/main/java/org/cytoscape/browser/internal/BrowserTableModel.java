package org.cytoscape.browser.internal;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.RowCreatedMicroListener;
import org.cytoscape.model.events.RowSetMicroListener;


public class BrowserTableModel extends AbstractTableModel
	implements ColumnCreatedListener, ColumnDeletedListener, RowCreatedMicroListener
{
	private final CyEventHelper eventHelper;
	private final CyTable table;
	private boolean tableHasBooleanSelected;
	private Map<CyRow, RowSetMicroListenerProxy> rowToListenerProxyMap;

	public BrowserTableModel(final CyEventHelper eventHelper, final CyTable table) {
		this.eventHelper = eventHelper;
		this.table = table;
		this.tableHasBooleanSelected = table.getColumnTypeMap().get(CyNetwork.SELECTED) == Boolean.class;
		this.rowToListenerProxyMap = new HashMap<CyRow, RowSetMicroListenerProxy>();

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, table);

		final List<CyRow> rows = table.getAllRows();
		for (final CyRow row : rows)
			rowToListenerProxyMap.put(row, new RowSetMicroListenerProxy(this, eventHelper, row));
	}

	@Override
	public int getRowCount() {
		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		if (columnNameToTypeMap.isEmpty())
			return 0;

		if (!tableHasBooleanSelected)
			return table.getRowCount();

		final List<CyRow> rows = table.getAllRows();

		int selectedCount = 0;
		for (final CyRow row : rows) {
			if (row.get(CyNetwork.SELECTED, Boolean.class))
				++selectedCount;
		}

		return selectedCount;
	}

	@Override
	public int getColumnCount() {
		return table.getColumnTypeMap().size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		final String columnName = getColumnName(column);

		if (tableHasBooleanSelected) {
			final Set<CyRow> selectedRows = table.getMatchingRows(CyNetwork.SELECTED, true);
			int count = 0;
			CyRow cyRow = null;
			for (final CyRow selectedRow : selectedRows) {
				if (count == row) {
					cyRow = selectedRow;
					break;
				}

				++count;
			}

			// Column 0 is always the primary key:
			if (column == 0)
				return cyRow.get(table.getPrimaryKey(), table.getPrimaryKeyType());

			return getValidatedObjectAndEditString(cyRow, columnName);
		} else {
			final List primaryKeyValues =
				table.getColumnValues(table.getPrimaryKey(),
						      table.getPrimaryKeyType());

			// Column 0 is always the primary key:
			if (column == 0)
				return primaryKeyValues.get(row);

			return getValidatedObjectAndEditString(table.getRow(primaryKeyValues.get(row)),
							       columnName);
		}
	}

	/**
	 *  @return the row index for "cyRow" or -1 if there is no matching row.
	 */
	int mapRowToRowIndex(final CyRow cyRow) {
		int index = 0;
		if (tableHasBooleanSelected) {
			final Set<CyRow> selectedRows = table.getMatchingRows(CyNetwork.SELECTED, true);
			for (final CyRow selectedRow : selectedRows) {
				if (cyRow == selectedRow)
					return index;
				++index;
			}

			return -1; // Most likely the passed in row was not a selected row!
		} else {
			final List primaryKeyValues =
				table.getColumnValues(table.getPrimaryKey(),
						      table.getPrimaryKeyType());
			for (final Object primaryKey : primaryKeyValues) {
				if (cyRow == table.getRow(primaryKey))
					return index;
				++index;
			}

			throw new IllegalStateException("we should *never* get here!");
		}
	}

	private ValidatedObjectAndEditString getValidatedObjectAndEditString(final CyRow row,
									     final String columnName)
	{
		final Object raw = row.getRaw(columnName);
		if (raw == null)
			return null;

		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		final Object cooked = row.get(columnName, columnNameToTypeMap.get(columnName));
		if (cooked != null)
			return new ValidatedObjectAndEditString(cooked, raw.toString());

		final String lastInternalError = table.getLastInternalError();
		return new ValidatedObjectAndEditString(cooked, raw.toString(), lastInternalError);
	}

	@Override
	public void handleEvent(final ColumnCreatedEvent e) {
		fireTableStructureChanged();
	}

	@Override
	public void handleEvent(final ColumnDeletedEvent e) {
		fireTableStructureChanged();
	}

	@Override
	public String getColumnName(final int column) {
		return mapColumnIndexToColumnName(column);
	}

	@Override
	public void handleRowCreated(final Object key) {
		final CyRow newRow = table.getRow(key);
		rowToListenerProxyMap.put(newRow, new RowSetMicroListenerProxy(this, eventHelper, newRow));
		fireTableStructureChanged();
	}

	private int mapColumnNameToColumnIndex(final String columnName) {
		final String primaryKey = table.getPrimaryKey();
		if (columnName.equals(primaryKey))
			return 0;

		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		int index = 1;
		for (final String name : columnNameToTypeMap.keySet()) {
			if (name.equals(columnName))
				return index;

			if (!name.equals(primaryKey))
				++index;
		}

		throw new IllegalStateException("We should *never* get here!");
	}

	private String mapColumnIndexToColumnName(final int index) {
		final String primaryKey = table.getPrimaryKey();
		if (index == 0)
			return primaryKey;


		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		int i = 1;
		for (final String name : columnNameToTypeMap.keySet()) {
			if (name.equals(primaryKey))
				continue;

			if (index == i)
				return name;

			++i;
		}

		throw new IllegalStateException("We should *never* get here!");
	}

	void handleRowValueUpdate(final CyRow row, final String columnName, final Object newValue,
				  final Object newRawValue)
	{
		if (tableHasBooleanSelected && columnName.equals(CyNetwork.SELECTED))
			fireTableStructureChanged();
		else {
			final int changedColumn = mapColumnNameToColumnIndex(columnName);
			fireTableChanged(new TableModelEvent(this, 0, table.getRowCount(), changedColumn));
		}
	}

	public void cleanup() {
		eventHelper.removeMicroListener(this, RowCreatedMicroListener.class, table);
		for (final RowSetMicroListenerProxy proxy : rowToListenerProxyMap.values())
			proxy.cleanup();
	}
}


class RowSetMicroListenerProxy implements RowSetMicroListener {
	private final BrowserTableModel tableModel;
	private final CyEventHelper eventHelper;
	private final CyRow row;

	RowSetMicroListenerProxy(final BrowserTableModel tableModel, final CyEventHelper eventHelper,
				 final CyRow row)
	{
		this.tableModel = tableModel;
		this.eventHelper = eventHelper;
		this.row = row;

		eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
	}

	@Override
	public void handleRowSet(final String columnName, final Object newValue, final Object newRawValue) {
		tableModel.handleRowValueUpdate(row, columnName, newValue, newRawValue);
	}

	void cleanup() {
		eventHelper.removeMicroListener(this, RowSetMicroListener.class, row);
	}
}
