package org.cytoscape.task.internal.quickstart;

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
		
		// Rename the key column to the selected type
		table.getColumn(state.getKeyColumnName()).setName(columnName);
		
		// Use it as virtual column
		
	}

}
