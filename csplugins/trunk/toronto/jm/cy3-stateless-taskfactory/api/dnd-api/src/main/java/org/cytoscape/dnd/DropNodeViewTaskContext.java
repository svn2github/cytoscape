package org.cytoscape.dnd;

import java.awt.datatransfer.Transferable;
import java.awt.geom.Point2D;

import org.cytoscape.task.NodeViewTaskContextImpl;

public class DropNodeViewTaskContext extends NodeViewTaskContextImpl {

	private Transferable transferable;
	private Point2D point;
	private Point2D transformedPoint;

	/**
	 * Sets the drop information for a TaskFactory. 
	 * @param t The transferable object that was dropped.
	 * @param javaPt The raw coordinate point at which the object was dropped.
	 * @param xformPt The drop point transformed into Cytoscape coordinates. 
	 */
	public void setDropInformation (Transferable t, Point2D javaPt, Point2D xformPt) {
		transferable = t;
		point = javaPt;
		transformedPoint = xformPt;
	}

	public Transferable getTransferable() {
		return transferable;
	}
}
