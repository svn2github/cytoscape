package BiNGO;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class GenericTaskFactory implements TaskFactory {

	private final Task task;
	
	public GenericTaskFactory(final Task task) {
		this.task = task;
	}
	
	public TaskIterator getTaskIterator() {
		return new TaskIterator(task);	}

}
