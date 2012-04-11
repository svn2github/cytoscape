package org.cytoscape.app.internal.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This class is an implementation of the Swing TableModel that allows data to
 * added and remove dynamically on a by-row basis.
 */
public class ListBasedTableModel implements TableModel {

	private List<TableModelListener> listeners;
	private List<Object[]> rows; 
	private Class<?>[] dataTypes;
	private String[] columnNames;
	private int columnCount;
	
	public ListBasedTableModel(int columnCount, Class<?>[] dataTypes, String[] columnNames) {
		if (columnCount <= 0) {
			throw new IllegalArgumentException("Can only create a table with at least 1 column.");
		}
		
		if (columnCount != dataTypes.length) {
			throw new IllegalArgumentException("The number of columns must match the number of data types provided.");
		}
		
		if (columnCount != columnNames.length) {
			throw new IllegalArgumentException("The number of columns must match the number of column names providied.");
		}

		rows = new ArrayList<Object[]>();
		listeners = new LinkedList<TableModelListener>();
		this.dataTypes = dataTypes;
	}
	
	// TODO: Rows are added to end, need to support adding rows in middle?
	public void addRow(Object[] row) {
		if (row.length != columnCount) {
			throw new IllegalArgumentException("Need to have a number of elements equal to the number of columns in the table.");
		}
		
		rows.add(row);
		int rowIndex = rows.size() - 1;
		
		TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);
		fireTableChangedEvent(event);
	}
	
	public List<Object[]> getRows() {
		return rows;
	}
	
	public void removeRow(Object[] row) {
		int rowIndex = rows.indexOf(row);
		
		if (rowIndex == -1) {
			throw new NoSuchElementException("The requested row, " + row + " was not found in the table.");
		}
	
		rows.remove(rowIndex);
		
		TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE);
		fireTableChangedEvent(event);
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		if (listeners.contains(l)) {
			throw new IllegalArgumentException("This listener has already been added.");
		}
		
		listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return dataTypes[columnIndex];
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		checkOutOfBounds(rowIndex, columnIndex);
		
		return rows.get(rowIndex)[columnIndex];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// Don't allow cell editing
		return false;
	}

	// TODO: Implement support for TabelModelListeners
	@Override
	public void removeTableModelListener(TableModelListener l) {
		if (!listeners.contains(l)) {
			throw new NoSuchElementException("Listener " + l + " was not added to this TableModel.");
		}
		
		listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		checkOutOfBounds(rowIndex, columnIndex);
		
		rows.get(rowIndex)[columnIndex] = aValue;
		
		TableModelEvent event = new TableModelEvent(this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE);
		fireTableChangedEvent(event);
	}
	
	// Checks if the requested row index and column index are out of bounds.
	private void checkOutOfBounds(int rowIndex, int columnIndex) {
		if (rowIndex >= rows.size()) {
			throw new IndexOutOfBoundsException("Cannot obtain row " + rowIndex + ", only have " + rows.size() + " rows.");
		}
		
		if (columnIndex >= columnCount) {
			throw new IndexOutOfBoundsException("Cannot obtain column " + rowIndex + ", only have " + columnCount + " columns.");
		}
	}
	
	private void fireTableChangedEvent(TableModelEvent event) {
		
		for (TableModelListener listener : listeners) {
			listener.tableChanged(event);
		}
	}
}
