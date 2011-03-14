package org.cytoscape.task.internal.quickstart;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class SetNetworkNameTask extends AbstractTask {

	private final String newName;
	private final CyNetworkViewReader reader;
	
	public SetNetworkNameTask(final CyNetworkViewReader reader, final String name) {
		super();
		this.newName = name;
		this.reader = reader;
	}

	public void run(TaskMonitor e) {
		CyNetworkView[] networkViews = reader.getNetworkViews();
		
		if(networkViews == null || networkViews.length == 0)
			throw new IllegalStateException("Could not find network to be renamed.");
		
		networkViews[0].getModel().getCyRow().set(CyTableEntry.NAME, newName);
	} 
}
