
package org.cytoscape.internal.test;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class InfiniteTaskFactory extends AbstractTaskFactory {
	public InfiniteTaskFactory() { }

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new InfiniteTask());
	}
}
