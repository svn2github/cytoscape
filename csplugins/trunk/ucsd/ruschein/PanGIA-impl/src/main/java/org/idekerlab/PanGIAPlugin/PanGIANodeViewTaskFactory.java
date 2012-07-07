package org.idekerlab.PanGIAPlugin;


import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;


public class PanGIANodeViewTaskFactory extends DynamicSupport implements NodeViewTaskFactory {

	public PanGIANodeViewTaskFactory() {
		//super();
	}	
	
	
	public void setNodeView(View<CyNode> nodeView, CyNetworkView netView) {
		setViews(nodeView, netView);
	}

	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView){
		return null;//new TaskIterator(task);
	}
	
	public boolean isReady(View<CyNode> nodeView, CyNetworkView networkView){
		return true;
	}
}


