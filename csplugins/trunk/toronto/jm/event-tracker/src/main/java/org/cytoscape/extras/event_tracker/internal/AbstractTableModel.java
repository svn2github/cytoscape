package org.cytoscape.extras.event_tracker.internal;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public abstract class AbstractTableModel<T> implements TableModel {
	protected List<TableModelListener> listeners;
	protected List<T> rows;
	
	public AbstractTableModel() {
		listeners = new ArrayList<TableModelListener>();
		rows = new ArrayList<T>();
	}
	
	public boolean isCellEditable(int row, int column) {
		// Read-only by default
		return false;
	}
	
	public void setValueAt(Object value, int row, int column) {
		// Read-only by default
	}
	
	public void removeTableModelListener(TableModelListener listener) {
		while (!listeners.remove(listener));
	}
	
	public int getRowCount() {
		return rows.size();
	}
	
	public void addTableModelListener(TableModelListener listener) {
		listeners.add(listener);
	}
	
	public T getRow(int index) {
		return rows.get(index);
	}
	
	public void addRow(T row) {
		int index = rows.size();
		synchronized (this) {
			rows.add(row);
		}
		notifyListeners(new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
	
	public void update(T row) {
		int index = rows.indexOf(row);
		notifyListeners(new TableModelEvent(this, index));
	}
	
	protected void notifyListeners(final TableModelEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (TableModelListener listener : listeners) {
					listener.tableChanged(event);
				}
			}
		});
	}
}
