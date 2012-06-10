package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

public class MyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 4744686051219349710L;
	
	private CyTable cytable;
	private String[] columnNames;
	private Object[][] data;
	private int columnLength;
	
	public MyTableModel(CyTable cytable){
		this.cytable = cytable;
		this.columnLength =cytable.getColumns().size();
		this.columnNames = new String[this.columnLength];
		this.data = new Object[cytable.getRowCount()][columnLength];
		setColumnNames();
		setDataValues();
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
	 * Stores all the data values from the <code>CyTable</code> in a 2D array
	 *
	 */
	public void setDataValues() {
		Collection<CyRow> cyrows = cytable.getAllRows();
		int rowIndex=0;
		Map<String, Object> cyrowmap;
		for(CyRow cyrow : cyrows){
			cyrowmap = cyrow.getAllValues();
			for(int columnIndex=0; columnIndex < columnLength; columnIndex++){
				data[rowIndex][columnIndex] = cyrowmap.get(columnNames[columnIndex]); 
			}
			rowIndex++;			
		}
	}
	
	@Override
	public int getColumnCount() {
		return this.columnLength;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		//the .toString() is temporary in order to display the JTable properly
		return data[rowIndex][columnIndex].toString();
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
    }
	
	@Override
	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
    }
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
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
