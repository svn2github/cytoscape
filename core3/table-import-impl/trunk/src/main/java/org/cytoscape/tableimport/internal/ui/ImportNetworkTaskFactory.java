package org.cytoscape.tableimport.internal.ui;


import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ImportNetworkTaskFactory implements TaskFactory
{
	ImportNetworkTask task;
	
	public ImportNetworkTaskFactory(ImportNetworkTask task){
		this.task = task;
	}
	public TaskIterator getTaskIterator() {
		return new TaskIterator(task);
	}
	
}
