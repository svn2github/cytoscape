package csplugins.jActiveModules;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.Task;


public class ActivePathsTaskFactory extends AbstractTaskFactory {
	
	private Task task;
	public ActivePathsTaskFactory(Task task){
		this.task = task;
	}
	
	public TaskIterator createTaskIterator()  {
		return new TaskIterator(task);
	}
}
