package org.cytoscape.editor.internal;


import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.task.SimpleNetworkViewTaskFactory;
import org.cytoscape.work.TaskIterator;


public class SIFInterpreterTaskFactory extends SimpleNetworkViewTaskFactory {
	public TaskIterator createTaskIterator(CyNetworkView view) {
		return new TaskIterator(new SIFInterpreterTask(view));
	}
}

