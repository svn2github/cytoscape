package org.cytoscape.ding.impl;

import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;

import org.cytoscape.dnd.DropNodeViewTaskContext;
import org.cytoscape.view.model.CyNetworkView;

public class AddEdgeNodeViewTaskContext extends DropNodeViewTaskContext {
	 @Override
	public void setDropInformation(Transferable t, Point2D javaPt,
			Point2D xformPt) {
		super.setDropInformation(t, javaPt, xformPt);

		CyNetworkView view = getNetworkView();
		AddEdgeStateMonitor.setSourcePoint(view,javaPt);

		// Because the transferable may be null, we leave that
		// tracking to the AddEdgeStateMonitor.
		AddEdgeStateMonitor.setTransferable(view,t);
	}
}
