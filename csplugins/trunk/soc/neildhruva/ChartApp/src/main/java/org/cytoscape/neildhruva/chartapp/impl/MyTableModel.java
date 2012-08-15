package org.cytoscape.neildhruva.chartapp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
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
	 * Returns all the rows that have valid values for all plottable columns.
	 * @return <code>List</code> of rows that have valid values for all plottable columns.
	 */
	public List<String> getPlottableRows() {
		List<String> rowNamesList = new ArrayList<String>();
		List<CyRow> cyrows = cytable.getAllRows();
		for(CyRow cyrow : cyrows) {
			int i=0;
			for(;i<columnLength;i++) {
				if(cyrow.get(getColumnName(i), getColumnClass(i))==null) {
					break;
				}
			}
			if(i==columnLength) {
				rowNamesList.add(cyrow.get(CyNetwork.NAME, String.class)); //This might not work if there are more than one
																		   //columns named CyNetwork.NAME
			}
		}
		
		return rowNamesList;
	}
	
	/**
	 * Sets the count of columns from the <code>CyTable</code>. Only those columns that contain
	 * <code>Integer</code>, <code>Long</code> and <code>Double</code> data are added to the count.
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