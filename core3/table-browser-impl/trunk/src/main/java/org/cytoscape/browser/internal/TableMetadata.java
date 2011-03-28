package org.cytoscape.browser.internal;


import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


class TableMetadata {
	final List<ColumnDescriptor> columnDescriptors;

	TableMetadata(final TableColumnModel columnModel, final TableModel tableModel) {
		columnDescriptors = new ArrayList<ColumnDescriptor>();
		final Enumeration<TableColumn> tableColumnsEnumeration = columnModel.getColumns();
		while (tableColumnsEnumeration.hasMoreElements()) {
			final TableColumn column = tableColumnsEnumeration.nextElement();
			final int columnIndex = column.getModelIndex();
			final String columnName = tableModel.getColumnName(columnIndex);
			columnDescriptors.add(new ColumnDescriptor(columnName, columnIndex, column.getWidth()));
		}
	}

	Iterator<ColumnDescriptor> getColumnDescriptors() {
		return columnDescriptors.listIterator();
	}
}
