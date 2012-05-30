package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


/**
 * PrintTableTask extracts information from the table model and prints it in the console. 
 * It prints all the table records in a row-column fashion.
 */
public class PrintTableTask extends AbstractTask {

	private CyApplicationManager manager;
	private CySwingApplication desktopApp;
	private CyTable cytable;
	private MyCytoPanel myCytoPanel;
	private JTable table, table2;
	
	private static boolean tableAlreadyExists = false;
 
	/**
	 * Class constructor invoked by the <code>PrintTableTaskFactory</code> class
	 * 
	 * @param manager an instance of <code>CyApplicationManager</code> that is used to manage the current network
	 */
	public PrintTableTask(CyApplicationManager manager, CySwingApplication desktopApp, MyCytoPanel myCytoPanel){			
		this.manager=manager;
		this.desktopApp=desktopApp;
		
		this.myCytoPanel = myCytoPanel;
		cytable = manager.getCurrentNetwork().getDefaultNodeTable(); 		// Node Table
		//CyTable cytable = manager.getCurrentNetwork().getDefaultNetworkTable(); 	// Network Table
		//CyTable cytable = manager.getCurrentNetwork().getDefaultEdgeTable(); 		// Edge Table
	}
	
	/**
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) {
	
		setTableValues(getColumnVector(), cytable.getRowCount());
		
		new MyDialog(table);
		if(!tableAlreadyExists)
		{
			myCytoPanel.add(new JScrollPane(table2));
			myCytoPanel.revalidate();
			tableAlreadyExists = true;
		}
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
	
	public void setTableValues(Vector<String> v, int rowCount){
		DefaultTableModel tablemodel = new DefaultTableModel(v, rowCount);
		table = new JTable(tablemodel);
		table2 = new JTable(tablemodel);
		Collection<CyRow> cyrows = cytable.getAllRows();
		int rowIndex=0;
		int columnIndex=0;
		for(CyRow cyrow : cyrows){
			Map<String, Object> cyrowmap = cyrow.getAllValues();
			for(String cyColumnName : v){
				table.getModel().setValueAt(cyrowmap.get(cyColumnName), rowIndex, columnIndex);
				table2.getModel().setValueAt(cyrowmap.get(cyColumnName), rowIndex, columnIndex++);
			}
			rowIndex++;
			columnIndex=0;
		}
	}	
}
