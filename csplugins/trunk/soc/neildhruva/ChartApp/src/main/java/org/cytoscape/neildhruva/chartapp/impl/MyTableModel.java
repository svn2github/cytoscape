package org.cytoscape.neildhruva.chartapp.impl;

import java.util.Collection;

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
		this.columnLength = setColumnCount();
		this.columnNames = setColumnNames();
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
	 * Sets the count of columns from the <code>CyTable</code>. Only those columns that contain
	 * <code>Integer</code>, <code>Long</code> and <code>Double</code> data are added to the count.
	 *
	 */
	public int setColumnCount() {
		Collection<CyColumn> cycolumns = (Collection<CyColumn>) cytable.getColumns(); 
		int count=0;
		for(CyColumn cycolumn : cycolumns){
			if((cycolumn.getType().equals(Integer.class) || 
				cycolumn.getType().equals(Double.class)  || 
				cycolumn.getType().equals(Long.class))   &&
				!cycolumn.getName().equals("SUID")) {
				 count++;
			 }
		}	
		return count;
	}
	
	/**
	 * Sets the names of columns from the <code>CyTable</code>. Only those columns that contain
	 * <code>Integer</code>, <code>Long</code> and <code>Double</code> data are added to the array.
	 */
	public String[] setColumnNames() {
		String[] columnNameArray = new String [this.columnLength];
		Collection<CyColumn> cycolumns = (Collection<CyColumn>) cytable.getColumns(); 
		int count=0;
		for(CyColumn cycolumn : cycolumns){
			if((cycolumn.getType().equals(Integer.class) || 
				cycolumn.getType().equals(Double.class)  || 
				cycolumn.getType().equals(Long.class))   &&
				!cycolumn.getName().equals("SUID")) {
				columnNameArray[count++] = cycolumn.getName();
			}
		}
		return columnNameArray;	
	}
	
}