package org.cytoscape.view.vizmap.gui.internal.bypass;

import org.cytoscape.model.CyEdge;
import org.cytoscape.task.AbstractEdgeViewTaskFactory;
import org.cytoscape.work.TaskIterator;

public class EdgeBypassMenuTaskFactory extends AbstractEdgeViewTaskFactory {

	@Override
	public TaskIterator getTaskIterator() {
		return null;
		//return new TaskIterator(new BypassTask<CyEdge>(edgeView));
	}
}
