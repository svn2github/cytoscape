package org.cytoscape.groups.results;

import giny.view.NodeView;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cytoscape.Cytoscape;
import ding.view.DGraphView;

/**
 * takes a list of nodeviews and, for each NodeView, transforms its coordinates
 * into those specifying the corresponding position in the *to* rectangle.
 * 
 * @author ajk
 * 
 */
public class NodeViewsTransformer {

	/**
	 * takes a list of nodeviews and, for each NodeView, transforms its coordinates
     * into those specifying the corresponding position in the *to* rectangle.
	 * @param nodeViews
	 * @param to
	 */
	public static void transform(List<NodeView> nodeViews, Rectangle2D to) {
		double scaleX = 1.0d;
		double scaleY = 1.0d;
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;

		double[] topLeft = new double[2];
		topLeft[0] = to.getMinX();
		topLeft[1] = to.getMinY();
		((DGraphView) Cytoscape.getCurrentNetworkView())
				.xformComponentToNodeCoords(topLeft);
		double toMinX = (topLeft[0] + 15); // adds buffer to layout region 
		double toMinY = (topLeft[1] + 30); // extra room for label

		double[] bottomRight = new double[2];
		bottomRight[0] = to.getMaxX();
		bottomRight[1] = to.getMaxY();
		((DGraphView) Cytoscape.getCurrentNetworkView())
				.xformComponentToNodeCoords(bottomRight);
		double toMaxX = (bottomRight[0] - 15); // adds buffer to layout region
		double toMaxY = (bottomRight[1] - 15);

		double toWidth = toMaxX - toMinX;
		double toHeight = toMaxY - toMinY;

		double currentX;
		double currentY;
		// first calculate the min/max x and y for the list of *relevant* nodeviews
		Iterator<NodeView> it = nodeViews.iterator();
		while (it.hasNext()) {
			NodeView nv = it.next();
			currentX = nv.getXPosition();
			currentY = nv.getYPosition();
			if ((currentX - (nv.getWidth() * 0.5)) < minX) {
				minX = currentX - (nv.getWidth() * 0.5);
			}
			if ((currentX + (nv.getWidth() * 0.5)) > maxX) {
				maxX = currentX + (nv.getWidth() * 0.5);
			}
			if ((currentY - (nv.getHeight() * 0.5)) < minY) {
				minY = currentY - (nv.getHeight() * 0.5);
			}
			if ((currentY + (nv.getHeight() * 0.5)) > maxY) {
				maxY = currentY + (nv.getHeight() * 0.5);
			}

		}

		scaleX = (maxX - minX) / toWidth;
		if (scaleX == 0.0) {
			scaleX = 1.0d;
		}
		scaleY = (maxY - minY) / toHeight;
		if (scaleY == 0.0) {
			scaleY = 1.0d;
		}

		Iterator<NodeView> it2 = nodeViews.iterator();
		while (it2.hasNext()) {
			NodeView nv =  it2.next();
			currentX = nv.getXPosition();
			currentY = nv.getYPosition();
			nv.setXPosition(toMinX + ((currentX - minX) / scaleX));
			nv.setYPosition(toMinY + ((currentY - minY) / scaleY));
		}
	}
	
	
	/**
	 * takes a list of nodeviews and, for each NodeView, transforms its coordinates
     * into those specifying the corresponding position in the *from* rectangle.
     * 
	 * @param nodeViews
	 * @param from
	 * @return NodeViews within boundary of current region (for moving and resizing)
	 */ 
	public static List bounded(List<NodeView> nodeViews, Rectangle2D from) {
		List<NodeView> boundedNodeViews = new ArrayList<NodeView>();
		double[] topLeft2 = new double[2];
		double fromMinX = 0; // no buffer here
		double fromMinY = 0;
		double[] bottomRight2 = new double[2];
		double fromMaxX = 0; // no buffer here
		double fromMaxY = 0;
		if (from != null) {
			topLeft2[0] = from.getMinX();
			topLeft2[1] = from.getMinY();
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.xformComponentToNodeCoords(topLeft2);
			fromMinX = topLeft2[0]; // no buffer here
			fromMinY = topLeft2[1];

			bottomRight2[0] = from.getMaxX();
			bottomRight2[1] = from.getMaxY();
			((DGraphView) Cytoscape.getCurrentNetworkView())
					.xformComponentToNodeCoords(bottomRight2);
			fromMaxX = bottomRight2[0]; // no buffer here
			fromMaxY = bottomRight2[1];

		}
		double currentX;
		double currentY;
		// first calculate the min/max x and y for the list of *relevant* nodeviews
		Iterator<NodeView> it = nodeViews.iterator();
		while (it.hasNext()) {
			NodeView nv = it.next();
			currentX = nv.getXPosition();
			currentY = nv.getYPosition();
			if ((from == null)
					|| ((currentX > fromMinX) && (currentX < fromMaxX)
							&& (currentY > fromMinY) && (currentY < fromMaxY))) {
				boundedNodeViews.add(nv);
			}
		}

		return boundedNodeViews;
	}

}
