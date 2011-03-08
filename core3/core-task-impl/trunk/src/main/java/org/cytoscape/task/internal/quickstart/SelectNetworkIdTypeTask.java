package org.cytoscape.task.internal.quickstart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class SelectNetworkIdTypeTask extends AbstractTask {
	
	@Tunable(description="Select Network ID Type")
	public final ListSingleSelection<String> selection;
	
	private final QuickStartState state;

	public SelectNetworkIdTypeTask(final QuickStartState state) {
		this.state = state;	
		final List<String> values = new ArrayList<String>();
		for(MajorIDSets val: MajorIDSets.values())
			values.add(val.getDisplayName());
		selection = new ListSingleSelection<String>(values);
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		System.out.println("ID type selected for network.  Selected type = " + selection.getSelectedValue());
		insertTasksAfterCurrentTask(new LoadTableTask(state));
	}

}
