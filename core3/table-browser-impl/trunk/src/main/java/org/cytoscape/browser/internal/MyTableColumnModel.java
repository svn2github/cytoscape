package org.cytoscape.browser.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class MyTableColumnModel implements TableColumnModel {
	final private List<TableColumn> tableColumns;
	final private List<TableColumnModelListener> listeners;
	private int margin;
	private ListSelectionModel selectionModel;
	private boolean columnSelectionAllowed;

	MyTableColumnModel() {
		tableColumns = new ArrayList<TableColumn>();
		listeners = new ArrayList<TableColumnModelListener>();
		margin = 1;
		selectionModel = new DefaultListSelectionModel();
		columnSelectionAllowed = true;
	}

	@Override
	public void addColumn(final TableColumn newColumn) {
		tableColumns.add(newColumn);

		// Notify all listeners:
		final TableColumnModelEvent event =
			new TableColumnModelEvent(this, tableColumns.size() - 1, tableColumns.size() - 1);
		for (final TableColumnModelListener listener : listeners)
			listener.columnAdded(event);
	}

	@Override
	public void addColumnModelListener(final TableColumnModelListener newListener) {
		listeners.add(newListener);
	}

	@Override
	public TableColumn getColumn(final int columnIndex) {
System.err.println("++X+++++++++++++++++++++++++ tableColumns.size()="+tableColumns.size());
		if (columnIndex >= tableColumns.size()) {
System.err.println("++Y+++++++++++++++++++++++++ call to getColumn("+columnIndex+") but we only have "+tableColumns.size()+" columns");
			return null;
		}

		return tableColumns.get(columnIndex);
	}

	@Override
	public int getColumnCount() {
		return tableColumns.size();
	}

	@Override
	public int getColumnIndex(Object columnIdentifier) {
		throw new IllegalStateException("MyTableColumnModel.getColumnIndex("
						+ columnIdentifier + ") has not been implemented!");
	}

	@Override
	public int getColumnIndexAtX(final int xPosition) {
		int cumulativeWidth = 0;
		for (int index = 0; index < tableColumns.size(); ++index) {
			cumulativeWidth += tableColumns.get(index).getWidth();
			if (xPosition < cumulativeWidth)
				return index;
		}

		return -1;
	}

	@Override
	public int getColumnMargin() {
		return margin;
	}

	@Override
	public Enumeration<TableColumn> getColumns() {
		return Collections.enumeration(tableColumns);
	}

	@Override
	public boolean getColumnSelectionAllowed() {
		return columnSelectionAllowed;
	}

	@Override
	public int getSelectedColumnCount() {
		int selectedCount = 0;
		for (int i = 0; i < tableColumns.size(); ++i) {
			if (selectionModel.isSelectedIndex(i))
				++selectedCount;
		}

		return selectedCount;
	}

	@Override
	public int[] getSelectedColumns() {
		int[] selectedColumns = new int[getSelectedColumnCount()];
		int k = 0;
		for (int i = 0; i < tableColumns.size(); ++i) {
			if (selectionModel.isSelectedIndex(i))
				selectedColumns[k++] = i;
		}

		return selectedColumns;
	}

	@Override
	public ListSelectionModel getSelectionModel() {
		return selectionModel;
	}

	@Override
	public int getTotalColumnWidth() {
		int totalWidth = 0;
		for (int index = 0; index < tableColumns.size(); ++index)
			totalWidth += tableColumns.get(index).getWidth();

		return totalWidth;
	}

	@Override
	public void moveColumn(final int oldColumnIndex, final int newColumnIndex) {
		if (oldColumnIndex == newColumnIndex)
			return;

		Collections.swap(tableColumns, oldColumnIndex, newColumnIndex);

		// Notify all listeners:
		final TableColumnModelEvent event =
			new TableColumnModelEvent(this, oldColumnIndex, newColumnIndex);
		for (final TableColumnModelListener listener : listeners)
			listener.columnMoved(event);
	}

	@Override
	public void removeColumn(final TableColumn column) {
		final int index = tableColumns.indexOf(column);
		if (index == -1)
			return;

		tableColumns.remove(index);

		// Notify all listeners:
		final TableColumnModelEvent event = new TableColumnModelEvent(this, index, index);
		for (final TableColumnModelListener listener : listeners)
			listener.columnRemoved(event);
	}

	@Override
	public void removeColumnModelListener(final TableColumnModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setColumnMargin(final int newMargin) {
		margin = newMargin;

		// Notify all listeners:
		final ChangeEvent event = new ChangeEvent(this);
		for (final TableColumnModelListener listener : listeners)
			listener.columnMarginChanged(event);
	}

	@Override
	public void setColumnSelectionAllowed(final boolean allowed) {
		columnSelectionAllowed = allowed;
	}

	@Override
	public void setSelectionModel(final ListSelectionModel newModel) {
		selectionModel = newModel;
	}
}