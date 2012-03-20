
package org.cytoscape.internal.test;

import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;


public class MultipleTaskFactory extends AbstractTaskFactory {
	public MultipleTaskFactory() { }

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SingleTask(true), new SingleTask(true), new SingleTask(false), new SingleTask(true));
	}
}
