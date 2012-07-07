package org.idekerlab.PanGIAPlugin;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.AbstractTaskFactory;


public class SearchTaskFactoryImpl extends AbstractTaskFactory { //implements TaskFactory {

	private SearchTask task;
	
	public SearchTaskFactoryImpl(SearchTask task) {
		this.task = task; 
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(task);
	}
}
