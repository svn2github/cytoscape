package org.cytoscape.editor.internal;


import java.awt.geom.Point2D;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.AbstractTask;

import org.cytoscape.dnd.GraphicalEntity;


public class DropNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory<Object> {
	private final CyNetworkManager netMgr;

	public DropNodeViewTaskFactoryImpl(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public TaskIterator createTaskIterator(Object tunableContext, View<CyNode> nv, CyNetworkView view, Transferable t, Point2D javaPt, Point2D xformPt) {
		return new TaskIterator(new AddNestedNetworkTask(nv, view, netMgr, t));
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
