package org.cytoscape.io.webservice.biomart.task;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class LoadRepositoryTaskFactory implements TaskFactory {
	
	private final BiomartRestClient client;
	private Task task;
	
	public LoadRepositoryTaskFactory(final BiomartRestClient client) {
		this.client = client;
		task = null;
	}

	@Override
	public TaskIterator getTaskIterator() {
		if(task == null)
			return new TaskIterator(new LoadRepositoryTask(client));
		else
			return new TaskIterator(task);
	}
	
	public void setTask(Task task) {
		this.task = task;
	}

}
