package org.cytoscape.browser.internal;


import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.cytoscape.event.CyEventHelper;
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

	public BrowserTableModel(final CyEventHelper eventHelper, final CyTable table) {
		this.eventHelper = eventHelper;
		this.table = table;

		eventHelper.addMicroListener(this, RowCreatedMicroListener.class, table);

		final List<CyRow> rows = table.getAllRows();
		for (final CyRow row : rows)
			eventHelper.addMicroListener(this, RowSetMicroListener.class, row);
	}

	@Override
	public int getRowCount() {
		return table.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return table.getColumnTypeMap().size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		final List primaryKeyValues = table.getColumnValues(table.getPrimaryKey(),
								    table.getPrimaryKeyType());

		// Column 0 is always the primary key:
		if (column == 0)
			return primaryKeyValues.get(row);

		final String columnName = mapColumnIndexToColumnName(column);
		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		return table.getRow(primaryKeyValues.get(row))
			.get(columnName, columnNameToTypeMap.get(columnName));
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
		final int changedColumn = mapColumnNameToColumnIndex(columnName);
		fireTableChanged(new TableModelEvent(this, 0, table.getRowCount(), changedColumn));
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
