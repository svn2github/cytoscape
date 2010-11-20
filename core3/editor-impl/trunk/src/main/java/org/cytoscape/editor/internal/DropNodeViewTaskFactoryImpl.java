package org.cytoscape.editor.internal;


import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskIterator;


public class DropNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory {
	private View<CyNode> nv;
	private CyNetworkView view;
	private Transferable t;
	private Point pt;

	public void setNodeView(View<CyNode> nv, CyNetworkView view) {
		this.view = view;
		this.nv = nv;
	}

	public void setDropInformation(Transferable t, Point pt) {
		this.pt = pt;
		this.t = t;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new DropNodeViewTask(nv, view, t, pt));
	}
}
