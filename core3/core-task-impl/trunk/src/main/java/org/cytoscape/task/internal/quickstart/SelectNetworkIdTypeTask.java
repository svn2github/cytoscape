package org.cytoscape.task.internal.quickstart;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class SelectNetworkIdTypeTask extends AbstractTask {
	
	@Tunable(description="Select Network ID Type")
	public ListSingleSelection<MajorIDSets> selection = new ListSingleSelection<MajorIDSets>(MajorIDSets.ENSEMBL,MajorIDSets.ENTREZ_GENE, MajorIDSets.UNIPROT);
	
	private final QuickStartState state;

	public SelectNetworkIdTypeTask(final QuickStartState state) {
		this.state = state;		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		System.out.println("ID type selected for network.  Selected type = " + selection.getSelectedValue());
		insertTasksAfterCurrentTask(new LoadTableTask(state));
	}

}
