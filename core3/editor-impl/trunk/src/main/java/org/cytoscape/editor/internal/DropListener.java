package org.cytoscape.editor.internal;


import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.dnd.DropTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskIterator;


public class DropListener implements DropTaskFactory {
	private CyNetworkView view;
	private Transferable t;
	private Point pt;
	
	public void setDropInformation(CyNetworkView view, Transferable t, Point pt) {
		System.out.println("Got drop: " + t);
		this.pt = pt;
		this.t = t;
		this.view = view;
	}

	
	public TaskIterator getTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new DropListenerTask(view, t, pt));
	}

}
