package org.cytoscape.layer.internal.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class LayerConnectionTableModel extends AbstractTableModel {
	
	private List<Long[]> dataModel;
	private Object[] columnNames;
	
	public LayerConnectionTableModel(Object[] columnNames) {
		this.columnNames = columnNames;
		dataModel = new ArrayList<Long[]>();
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return dataModel.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return dataModel.get(rowIndex)[columnIndex];
	}
	
	public void setValueAt(Object value, int row, int col) {
		if(value instanceof Long == false) {
			throw new IllegalArgumentException("Invalid data type");
		}
		Long[] rowData = dataModel.get(row);
		rowData[col] = (Long) value;
		
		fireTableDataChanged();
	}

	public boolean isCellEditable(int row, int column) {
		if (column == 0 || column == 1)
			return false;
		else
			return true;
	}
	
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}
	
	public void addRow(Long[] row) {
		dataModel.add(row);
		fireTableDataChanged();
	}

}
