package org.cytoscape.editor.internal;


import java.awt.geom.Point2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.editor.internal.gui.BasicCytoShapeEntity;


public class DropNetworkViewTask extends AbstractNetworkViewTask {

	private final Transferable t;
	private final Point2D xformPt;
	
	public DropNetworkViewTask(CyNetworkView view, Transferable t, Point2D xformPt) {
		super(view);
		this.t = t;
		this.xformPt = xformPt;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		if ( t==null) 
			return;

		DataFlavor[] dfl = t.getTransferDataFlavors();

		if ( dfl==null) 
			return;

		for (DataFlavor d : dfl) {
			if ( d.getRepresentationClass() == BasicCytoShapeEntity.class ) {
				String myShape = t.getTransferData(d).toString();
				if ( myShape.equals("Node") ) {
					addNode();
				} 
			}
		}
	}

	private void addNode() {
		CyNetwork net = view.getModel();
		CyNode n = net.addNode();
		View<CyNode> nv = view.getNodeView(n);
		nv.setVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION,xformPt.getX());
		nv.setVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION,xformPt.getY());
		view.updateView();
	}
}
