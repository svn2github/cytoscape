package csplugins.jActiveModules;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.Task;


public class ActivePathsTaskFactory implements TaskFactory {
	
	private Task task;
	public ActivePathsTaskFactory(Task task){
		this.task = task;
	}
	
	public TaskIterator getTaskIterator()  {
		return new TaskIterator(task);
	}
}
