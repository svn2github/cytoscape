package org.cytoscape.editor.internal;


import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;


public class DropNodeViewTask extends AbstractNodeViewTask {

	private final Transferable t;
	private final Point pt;
	
	public DropNodeViewTask(View<CyNode> nv, CyNetworkView view, Transferable t, Point pt) {
		super(nv,view);
		this.t = t;
		this.pt = pt;
	}

	@Override
	public void run(TaskMonitor tm) throws Exception {
		System.out.println ("DropNodeViewTask: transferrable = " + t + 
				            ", location = " + pt);
	/*	
		DataFlavor[] dfl = t.getTransferDataFlavors();

		for (DataFlavor d : dfl) {
			System.out.println("Item dropped of Mime Type: " + d.getMimeType());
			System.out.println("Mime subtype is:  " + d.getSubType());
			System.out.println("Mime class is: " + d.getRepresentationClass());

			Class<?> mimeClass = d.getRepresentationClass();
		}
		*/
	}
}
