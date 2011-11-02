
package org.cytoscape.internal.test;

import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class MultipleTaskFactory implements TaskFactory {
	public MultipleTaskFactory() { }

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new SingleTask(), new SingleTask(), new SingleTask(), new SingleTask());
	}
}
