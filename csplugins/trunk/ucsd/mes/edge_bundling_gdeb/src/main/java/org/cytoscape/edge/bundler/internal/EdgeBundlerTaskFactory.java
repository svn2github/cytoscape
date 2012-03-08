package org.cytoscape.edge.bundler.internal;

import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.work.TaskIterator;


public class EdgeBundlerTaskFactory extends AbstractNetworkViewTaskFactory {

	public EdgeBundlerTaskFactory() {
		super();
	}
	
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new EdgeBundlerTask(view));
	}
}
