package org.idekerlab.PanGIAPlugin;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class SearchTaskFactoryImpl implements TaskFactory {

	private SearchTask task;
	
	public SearchTaskFactoryImpl(SearchTask task) {
		this.task = task; 
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(task);
	}
}
