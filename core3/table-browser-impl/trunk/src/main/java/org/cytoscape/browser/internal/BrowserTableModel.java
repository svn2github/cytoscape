package org.cytoscape.browser.internal;


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
	implements ColumnCreatedListener, ColumnDeletedListener, RowSetMicroListener, RowCreatedMicroListener
{
	private final CyEventHelper eventHelper;
	private final CyTable table;
	private boolean tableHasBooleanSelected;

	public BrowserTableModel(final CyEventHelper eventHelper, final CyTable table) {
		this.eventHelper = eventHelper;
		this.table = table;
		this.tableHasBooleanSelected = table.getColumnTypeMap().get(CyNetwork.SELECTED) == Boolean.class;

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, table);

		final List<CyRow> rows = table.getAllRows();
		for (final CyRow row : rows)
			eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
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
	public void handleRowSet(final String columnName, final Object value) {
		if (tableHasBooleanSelected && columnName.equals(CyNetwork.SELECTED))
			fireTableStructureChanged();
		else {
			final int changedColumn = mapColumnNameToColumnIndex(columnName);
			fireTableChanged(new TableModelEvent(this, 0, table.getRowCount(), changedColumn));
		}
	}

	@Override
	public void handleRowCreated(final Object key) {
		eventHelper.addMicroListener(this, RowSetMicroListener.class, table.getRow(key));
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
}
