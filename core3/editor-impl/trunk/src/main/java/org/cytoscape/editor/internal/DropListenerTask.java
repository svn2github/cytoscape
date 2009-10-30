package org.cytoscape.editor.internal;

import java.awt.Point;
import java.awt.datatransfer.Transferable;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class DropListenerTask extends AbstractNetworkViewTask {

	
	CyNetworkView view;
	Transferable t;
	Point pt;
	CyNetwork network;
	
	public DropListenerTask(CyNetworkView view) {
		super(view);
		this.view = view;
		network = view.getSource();
	}

	public DropListenerTask(CyNetworkView view, Transferable t, Point pt) {
		super(view);
		this.view = view;
		this.t = t;
		this.pt = pt;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		System.out.println ("DropListenerTask: transferrable = " + t + 
				", location = " + pt);
		
		
	}




}
