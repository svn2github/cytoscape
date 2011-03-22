package org.cytoscape.task.internal.quickstart;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class MergeDataTask extends AbstractTask {
	
	final QuickStartState state;
	
	MergeDataTask(final QuickStartState state) {
		this.state = state;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// Step 1: Create new column for the network.
		final IDType type = state.getIDType();
		if(type == null)
			throw new IllegalStateException("ID type is unknown.");
		
		final String columnName = type.getDisplayName();
		
		final CyTable table = state.getImportedTable();
		
		// "Copy" Name column to ID type name.
		final CyColumn pKey = table.getPrimaryKey();
		table.addVirtualColumn(columnName, pKey.getName(), table, pKey.getName(), pKey.getName(), false);
		
		// Use it as virtual column
		
		System.out.println("********** columnName is " + columnName);
		
		taskMonitor.setStatusMessage("Finished!");
		taskMonitor.setProgress(1.0);
		
		JOptionPane.showMessageDialog(null, generateReport(table, columnName), "Network and Attributes Loaded", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String generateReport(final CyTable table, final String columnName) {

		final StringBuilder builder = new StringBuilder();
		
		builder.append("Data sets loaded:\n  Network: ");
		builder.append("\n  Data Table: " + table.getTitle());
		builder.append("\n\n  ID Type: " + columnName);
		builder.append("\n  Matched entries: ");
		
		
		// TODO: Should be done in mapping. 
		return builder.toString();
	}

}
