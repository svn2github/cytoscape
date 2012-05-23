package org.cytoscape.sample.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/*
 * PrintTableTask extracts information from the table model and prints it in the console. 
 * It prints all the table records in a row-column fashion.
 */
public class PrintTableTask extends AbstractTask {

	private CyApplicationManager manager;
 
	/*
	 * Class constructor invoked by the <code>PrintTableTaskFactory</code> class
	 * 
	 *  @param manager an instance of <code>CyApplicationManager</code> that is used to manage the current network
	 */
	public PrintTableTask(CyApplicationManager manager){			
		
		this.manager=manager;
	}
	
	/*
	 * @see org.cytoscape.work.AbstractTask#run(org.cytoscape.work.TaskMonitor)
	 */
	@Override
	public void run(TaskMonitor taskMonitor) {
	
		try {
			
			CyTable cytable = manager.getCurrentNetwork().getDefaultNodeTable(); 		// Node Table
			//CyTable cytable = manager.getCurrentNetwork().getDefaultNetworkTable(); 	// Network Table
			//CyTable cytable = manager.getCurrentNetwork().getDefaultEdgeTable(); 		// Edge Table 
			//CyTable cytable = manager.getCurrentTable(); 								// Global Table
        
			//Collect all columns, get their column names, iterate over these
			Collection<CyColumn> cycolumn = (Collection<CyColumn>) cytable.getColumns();
			Iterator<CyColumn> itr = cycolumn.iterator();
			System.out.println();
			while(itr.hasNext())
				System.out.print(itr.next().getName()+"\t");
			System.out.println();
			
			int k=cycolumn.size();
			//Get all table rows, go row by row, extract values in each row using column names from above
			for(CyRow cyrow : cytable.getAllRows()){
				Map<String, Object> cyrowmap = cyrow.getAllValues();
				itr = cycolumn.iterator();
				for(int j=0; j<k; j++){
					System.out.print(cyrowmap.get(itr.next().getName())+"\t");
        	   	
				}
				System.out.println();
			}
		}catch(NullPointerException e){
			
			System.out.println("Please import a network");
		}
	}	
}
