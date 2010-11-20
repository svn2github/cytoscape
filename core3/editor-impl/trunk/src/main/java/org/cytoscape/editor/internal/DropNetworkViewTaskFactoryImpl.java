package org.cytoscape.editor.internal;


import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.dnd.DropNetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;


public class DropNetworkViewTaskFactoryImpl implements DropNetworkViewTaskFactory {
	private CyNetworkView view;
	private Transferable t;
	private Point pt;

	public void setNetworkView(CyNetworkView view) {
		this.view = view;
	}

	public void setDropInformation(Transferable t, Point pt) {
		this.pt = pt;
		this.t = t;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new DropNetworkViewTask(view, t, pt));
	}
}
