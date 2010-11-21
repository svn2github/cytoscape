package org.cytoscape.editor.internal;


import java.awt.geom.Point2D;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;

import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.AbstractTask;

import org.cytoscape.editor.internal.gui.BasicCytoShapeEntity;


public class DropNodeViewTaskFactoryImpl implements DropNodeViewTaskFactory {
	private View<CyNode> nv;
	private CyNetworkView view;
	private Transferable t;
	private Point2D javaPt;
	private Point2D xformPt;

	private final CyNetworkManager netMgr;

	public DropNodeViewTaskFactoryImpl(CyNetworkManager netMgr) {
		this.netMgr = netMgr;
	}

	public void setNodeView(View<CyNode> nv, CyNetworkView view) {
		this.view = view;
		this.nv = nv;
	}

	public void setDropInformation(Transferable t, Point2D javaPt, Point2D xformPt) {
		this.javaPt = javaPt;
		this.xformPt = xformPt;
		this.t = t;
	}

	public TaskIterator getTaskIterator() {

        if ( t==null)
            return null;

		try {

        DataFlavor[] dfl = t.getTransferDataFlavors();

        if ( dfl==null)
            return null;

        for (DataFlavor d : dfl) {
            if ( d.getRepresentationClass() == BasicCytoShapeEntity.class ) {
                String myShape = t.getTransferData(d).toString();
				System.out.println("got shape drop: " + myShape);
                if ( myShape.equals("Edge") ) {
					return new TaskIterator(new AddEdgeTask(nv, view));
                } else if ( myShape.equals("Network") ) {
					return new TaskIterator(new AddNestedNetworkTask(nv, view, netMgr));
				}
            }
        }

		} catch (Exception e) {
			return new TaskIterator(new ExceptionTask(e));
		}

		return null;
	}

	private static class ExceptionTask extends AbstractTask {
		private final Exception e;
		ExceptionTask(Exception e) { this.e = e; }
		public void run(TaskMonitor tm) throws Exception { throw e; }
	}
}
