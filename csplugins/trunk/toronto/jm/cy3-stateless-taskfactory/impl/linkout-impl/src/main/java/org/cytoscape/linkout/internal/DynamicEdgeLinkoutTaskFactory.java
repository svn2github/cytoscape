package org.cytoscape.linkout.internal;


import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.TaskIterator;


public class DynamicEdgeLinkoutTaskFactory extends DynamicSupport implements EdgeViewTaskFactory<EdgeViewLinkoutTaskContext> {
	public DynamicEdgeLinkoutTaskFactory(OpenBrowser browser) {
		super(browser);
	}
	
	@Override
	public EdgeViewLinkoutTaskContext createTaskContext() {
		return new EdgeViewLinkoutTaskContext();
	}
	
	@Override
	public TaskIterator createTaskIterator(EdgeViewLinkoutTaskContext context) {
		return createTaskIterator((LinkoutTaskContext) context);
	}
}
