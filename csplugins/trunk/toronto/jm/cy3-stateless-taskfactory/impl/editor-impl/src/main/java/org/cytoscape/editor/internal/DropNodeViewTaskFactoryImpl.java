package org.cytoscape.editor.internal;


import java.awt.geom.Point2D;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

import org.cytoscape.dnd.DropNodeViewTaskContext;
import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.AbstractTask;

import org.cytoscape.dnd.GraphicalEntity;


public class DropNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory<DropNodeViewTaskContext> {
	private final CyNetworkManager netMgr;

	public DropNodeViewTaskFactoryImpl(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public TaskIterator createTaskIterator(DropNodeViewTaskContext context) {
		return new TaskIterator(new AddNestedNetworkTask(context, netMgr));
	}
	
	@Override
	public DropNodeViewTaskContext createTaskContext() {
		return new DropNodeViewTaskContext();
	}
}
