package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
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
 
	/**
	 * Class constructor invoked by the <code>PrintTableTaskFactory</code> class
	 * 
	 *  @param manager an instance of <code>CyApplicationManager</code> that is used to manage the current network
	 */
	public PrintTableTask(CyApplicationManager manager){			
		
		this.manager=manager;
	}
	
	/**
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) {
	
		CyTable cytable = manager.getCurrentNetwork().getDefaultNodeTable(); 		// Node Table
		//CyTable cytable = manager.getCurrentNetwork().getDefaultNetworkTable(); 	// Network Table
		//CyTable cytable = manager.getCurrentNetwork().getDefaultEdgeTable(); 		// Edge Table 
		
		if(cytable != null){
			//Collect all columns, get their column names, iterate over these
			Collection<CyColumn> cycolumns = (Collection<CyColumn>) cytable.getColumns();
			for(CyColumn cycolumn : cycolumns){
				System.out.print(cycolumn.getName()+"\t");
			}
			System.out.println();
			
			//Get all table rows, go row by row, extract values in each row using column names from above
			Collection<CyRow> cyrows = cytable.getAllRows();
			for(CyRow cyrow : cyrows){
				Map<String, Object> cyrowmap = cyrow.getAllValues();
				for(CyColumn cycolumn : cycolumns){
						System.out.print(cyrowmap.get(cycolumn.getName())+"\t");
				}
				System.out.println();
			}
		}else{
			System.out.println("Please import a network");
		}
	}
}
