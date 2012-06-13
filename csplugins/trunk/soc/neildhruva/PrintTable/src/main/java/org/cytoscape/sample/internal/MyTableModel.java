package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;

public class MyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4744686051219349710L;
	
	private CyTable cytable;
	private String[] columnNames;
	private int columnLength;
	
	public MyTableModel(CyTable cytable){
		this.cytable = cytable;
		this.columnLength =cytable.getColumns().size();
		this.columnNames = new String[this.columnLength];
		setColumnNames();
	}
	
	@Override
	public int getColumnCount() {
		return this.columnLength;
	}

	@Override
	public int getRowCount() {
		return cytable.getRowCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return cytable.getAllRows().get(rowIndex).get(getColumnName(columnIndex), getColumnClass(columnIndex));
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
    }
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return cytable.getColumn(getColumnName(columnIndex)).getType();
    }
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	/**
	 * Sets the names of columns from the <code>CyTable</code>
	 *
	 */
	public void setColumnNames() {
		Collection<CyColumn> cycolumns = (Collection<CyColumn>) cytable.getColumns(); 
		int count=0;
		for(CyColumn cycolumn : cycolumns){
			 columnNames[count] = cycolumn.getName();
			 count++;
		}	
	}
	
	/**
	 * Returns a vector of names of columns from the <code>CyTable</code> that are of the type Integer, Long or Double.
	 * 
	 * @return Vector containing names of columns that are of the type Integer, Long or Double.
	 */
	public Vector<String> getPlottableColumns() {
		Vector<String> v = new Vector<String>();
		for(int columnIndex=0; columnIndex < columnLength; columnIndex++){
			if(getColumnClass(columnIndex).equals(Integer.class) || getColumnClass(columnIndex).equals(Double.class) || getColumnClass(columnIndex).equals(Long.class)) {
				v.add(columnNames[columnIndex]);
			}
		}
		return v;	
	}
	
}
