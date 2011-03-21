package org.cytoscape.task.internal.quickstart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.task.internal.quickstart.QuickStartState.Job;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class SelectMappingKeyTypeTask extends AbstractTask {
	
	@Tunable(description="Select Network ID Type")
	public final ListSingleSelection<String> selection;
	
	@Tunable(description="Or, enter ID Type")
	public String otherIDType;
	
	private final QuickStartState state;
	
	private final Map<String, IDType> idTypeMap;
	private final ImportTaskUtil util;

	public SelectMappingKeyTypeTask(final QuickStartState state, ImportTaskUtil util) {
		this.idTypeMap = new HashMap<String, IDType>();
		this.state = state;	
		this.util = util;
		final List<String> values = new ArrayList<String>();
		for(IDType val: IDType.values()) {
			values.add(val.getDisplayName());
			this.idTypeMap.put(val.getDisplayName(), val);
		}
		selection = new ListSingleSelection<String>(values);
	}

	@Override
	public void run(TaskMonitor monitor) throws Exception {
		final String selected = selection.getSelectedValue();
		
		state.setIDType(this.idTypeMap.get(selected));
		
		System.out.println("ID type selected for network.  Selected type = " + selected);
		state.finished(Job.SELECT_NETWORK_ID_TYPE);
		
		if(state.isFinished()) {
			monitor.setStatusMessage("Finished!");
			monitor.setProgress(1.0);
		} else {
			// Need to load table.
			insertTasksAfterCurrentTask(new LoadTableTask(state, util));
		}
	}

}
