package org.cytoscape.editor.internal;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.dnd.DropTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;

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

	
	public Task getTask() {
		// TODO Auto-generated method stub
		return new DropListenerTask(view, t, pt );
	}

}
