package org.idekerlab.PanGIAPlugin;

import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.work.TaskIterator;

public class PanGIANodeViewTaskFactory extends AbstractNodeViewTaskFactory {
	public PanGIANodeViewTaskFactory() {
		super();
	}
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new PanGIANodeViewTask(this.netView, this.nodeView));
	}
}
