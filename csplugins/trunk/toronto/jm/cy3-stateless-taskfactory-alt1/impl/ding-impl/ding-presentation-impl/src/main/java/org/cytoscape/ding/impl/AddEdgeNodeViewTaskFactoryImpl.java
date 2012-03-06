package org.cytoscape.ding.impl; 


import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;

import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;


public class AddEdgeNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory<Object> {
	private final CyNetworkManager netMgr;

	public AddEdgeNodeViewTaskFactoryImpl(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public TaskIterator createTaskIterator(Object tunableContext, View<CyNode> nv, CyNetworkView view, Transferable t, Point2D javaPt, Point2D xformPt) {
		AddEdgeStateMonitor.setSourcePoint(view,javaPt);

		// Because the transferable may be null, we leave that
		// tracking to the AddEdgeStateMonitor.
		AddEdgeStateMonitor.setTransferable(view,t);
		
		return new TaskIterator(new AddEdgeTask(nv, view, AddEdgeStateMonitor.getTransferable(view)));
	}
	
	@Override
	public boolean isReady(Object tunableContext, View<CyNode> nodeView, CyNetworkView networkView, Transferable t, Point2D javaPt, Point2D xformPt) {
		return true;
	}
	
	@Override
	public Object createTunableContext() {
		return null;
	}
}
