package org.cytoscape.linkout.internal;


import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.TaskIterator;


public class DynamicNodeLinkoutTaskFactory extends DynamicSupport implements NodeViewTaskFactory<NodeViewLinkoutTaskContext> {
	public DynamicNodeLinkoutTaskFactory(OpenBrowser browser) {
		super(browser);
	}
	
	@Override
	public NodeViewLinkoutTaskContext createTaskContext() {
		return new NodeViewLinkoutTaskContext();
	}
	
	@Override
	public TaskIterator createTaskIterator(NodeViewLinkoutTaskContext context) {
		return createTaskIterator((LinkoutTaskContext) context);
	}
}
