package org.cytoscape.editor.internal;


import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;


public class DropListenerTask extends AbstractNetworkViewTask {
	Transferable t;
	Point pt;
	
	public DropListenerTask(final CyNetworkView view) {
		super(view);
	}

	public DropListenerTask(CyNetworkView view, Transferable t, Point pt) {
		super(view);
		this.t = t;
		this.pt = pt;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		System.out.println ("DropListenerTask: transferrable = " + t + 
				", location = " + pt);
		
		DataFlavor[] dfl = t.getTransferDataFlavors();

		for (DataFlavor d : dfl) {
			System.out.println("Item dropped of Mime Type: " + d.getMimeType());
			System.out.println("Mime subtype is:  " + d.getSubType());
			System.out.println("Mime class is: " + d.getRepresentationClass());

			Class<?> mimeClass = d.getRepresentationClass();
		}
	}
	@Override
	public void cancel() {
	}
}
