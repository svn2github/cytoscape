package org.cytoscape.biopax.internal.action;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.SimpleNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

public class DisplayBioPaxXmlTaskFactory extends SimpleNodeViewTaskFactory {

	private CySwingApplication cySwingApplication;

	public DisplayBioPaxXmlTaskFactory(CySwingApplication cySwingApplication) {
		this.cySwingApplication = cySwingApplication;
	}
	
	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		return new TaskIterator(new DisplayBioPaxXmlTask(nodeView, networkView, cySwingApplication));
	}

}
