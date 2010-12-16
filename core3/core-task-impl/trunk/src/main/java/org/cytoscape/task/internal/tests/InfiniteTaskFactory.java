
package org.cytoscape.task.internal.tests;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class InfiniteTaskFactory implements TaskFactory {
	public InfiniteTaskFactory() { }

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new InfiniteTask());
	}
}
