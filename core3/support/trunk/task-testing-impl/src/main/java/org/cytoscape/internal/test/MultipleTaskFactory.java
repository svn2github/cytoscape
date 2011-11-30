
package org.cytoscape.internal.test;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class MultipleTaskFactory implements TaskFactory {
	public MultipleTaskFactory() { }

	public TaskIterator createTaskIterator() {
		return new TaskIterator(new SingleTask(true), new SingleTask(true), new SingleTask(false), new SingleTask(true));
	}
}
