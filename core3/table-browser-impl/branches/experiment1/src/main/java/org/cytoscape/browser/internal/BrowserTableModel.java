package org.cytoscape.browser.internal;


import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;


public class BrowserTableModel extends AbstractTableModel
	implements ColumnCreatedListener, ColumnDeletedListener
{
	private final CyTable table;

	public BrowserTableModel(final CyTable table) {
		this.table = table;
	}

	public int getRowCount() {
		return table.getRowCount();
	}

	public int getColumnCount() {
		return table.getColumnTypeMap().size();
	}

	public Object getValueAt(final int row, final int column) {
		final List primaryKeyValues = table.getColumnValues(table.getPrimaryKey(),
								    table.getPrimaryKeyType());

		// Column 0 is always the primary key:
		if (column == 0)
			return primaryKeyValues.get(row);

		final Map<String, Class<?>> columnNameToTypeMap = table.getColumnTypeMap();
		final String primaryKey = table.getPrimaryKey();
		int i = 1;
		for (String columnName : columnNameToTypeMap.keySet()) {
			if (columnName.equals(primaryKey))
				continue;
			if (column == i)
				return table.getRow(primaryKeyValues.get(row))
					.get(columnName, columnNameToTypeMap.get(columnName));
			++i;
		}

		return new IllegalStateException("We should *never* get here!");
	}

	public void handleEvent(final ColumnCreatedEvent e) {
		fireTableStructureChanged();
	}

	public void handleEvent(final ColumnDeletedEvent e) {
		fireTableStructureChanged();
	}
}
