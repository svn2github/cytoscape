package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;

public class CreateTable {

	private CyTable cytable;
	
	public CreateTable(CyTable cytable){			
		
		this.cytable = cytable;
	}
	
	/**
	 * Used to acquire the names of columns in the <code>CyTable</code> instance
	 *
	 * @return Vector<String> Vector of column names
	 */
	public Vector<String> getColumnVector(){
		Collection<CyColumn> cycolumns = (Collection<CyColumn>) cytable.getColumns(); 
		Vector<String> v = new Vector<String>();
		for(CyColumn cycolumn : cycolumns){
			 v.add(cycolumn.getName());
		}	
		return v;
	}
	
	/**
	 * Values corresponding to each cell in the table are acquired and set in the
	 * new JTable
	 *
	 * @param v A vector of column names
	 * @param rowCount Number of rows in the <code>CyTable</code> instance
	 */
	
	public JTable setTableValues(Vector<String> v, int rowCount){
		DefaultTableModel tablemodel = new DefaultTableModel(v, rowCount);
		JTable jtable = new JTable(tablemodel);
		Collection<CyRow> cyrows = cytable.getAllRows();
		int rowIndex=0;
		int columnIndex=0;
		for(CyRow cyrow : cyrows){
			Map<String, Object> cyrowmap = cyrow.getAllValues();
			for(String cyColumnName : v){
				jtable.getModel().setValueAt(cyrowmap.get(cyColumnName), rowIndex, columnIndex++);
			}
			rowIndex++;
			columnIndex=0;
		}
		return jtable;
	}	
}
