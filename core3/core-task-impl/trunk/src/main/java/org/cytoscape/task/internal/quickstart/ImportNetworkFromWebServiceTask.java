package org.cytoscape.task.internal.quickstart;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class ImportNetworkFromWebServiceTask extends AbstractTask {
	
	@Tunable(description = "Select Species")
	public final ListSingleSelection<String> species;
	
	ImportNetworkFromWebServiceTask() {
		super();
		
		// TODO: Load list of species from property file.
		this.species = new ListSingleSelection<String>("Human", "Mouse", "Fly", "Yeast");

	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		System.out.println("This function is not implemented yet.");
	}

}
