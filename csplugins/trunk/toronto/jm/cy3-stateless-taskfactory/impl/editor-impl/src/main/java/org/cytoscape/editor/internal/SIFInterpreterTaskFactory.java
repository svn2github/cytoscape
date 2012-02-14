package org.cytoscape.editor.internal;


import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskContext;
import org.cytoscape.work.TaskIterator;


public class SIFInterpreterTaskFactory extends AbstractNetworkViewTaskFactory {
	public TaskIterator createTaskIterator(NetworkViewTaskContext context) {
		return new TaskIterator(new SIFInterpreterTask(context.getNetworkView()));
	}
}

