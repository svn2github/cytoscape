package com.agilent.labs.excentricLabelsPlugin;

import giny.view.NodeView;
import infovis.visualization.magicLens.LabeledComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;

public class CyLabeledItem implements LabeledComponent.LabeledItem {

	private CyNetworkView view;

	private int index;

	private NodeView nv;
	
	private Shape shape;
	
	private CyLabeledComponent comp;

	private static cytoscape.data.CyAttributes nodeAttribs = Cytoscape
			.getNodeAttributes();

	public CyLabeledItem(CyNetworkView view, int idx, CyLabeledComponent comp) {
		this.view = view;
		this.index = idx;
		this.comp = comp;
		this.nv = view.getNodeView(idx);
		
		// for now, just return bounding box of NodeView
		this.shape = new Rectangle2D.Double(nv.getXPosition(), nv.getYPosition(), 
				nv.getWidth(), nv.getHeight());		
	}
	
	/**
	 * Returns the JComponent managing this LabeledComponent
	 * 
	 * @return the JComponent managing this LabeledComponent.
	 */
	public Component getComponent() {
		return Cytoscape.getDesktop().getNetworkViewManager()
//				.getComponentForView(view);
		.getInternalFrameComponent(view);
	}

	public String getLabel() {
		String id = nv.getNode().getIdentifier();
		return nodeAttribs.getStringAttribute(id, Semantics.CANONICAL_NAME);
	}

	public Shape getShape() {
		return shape;		
	};
	
	// AJK: 07/28/06 BEGIN
	//    try a different approach to getting the center point, based upon
	//    interpolation between original hitBox and transformed coordinates
   /* 
	public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut) {
		ptOut.setLocation(shape.getBounds().getCenterX(), shape.getBounds().getCenterY());
		// convert ptOut rectangle corners to node coordinates
		//    actually, try just taking the inverse of the inverse, since coords were originally xformed?

		double[] coords = new double[2];
		coords[0] = ptOut.getX();
		coords[1] = ptOut.getY();
//	    actually, try just taking the inverse of the inverse, since coords were originally xformed?
//      07/28/06 try just using coordinates of node in node coordinates
		//		((DGraphView) view).getCanvas().getM_grafx().xformNodeToImageCoords(coords);
		((DGraphView) view).xformComponentToNodeCoords(coords);
//		ptOut.setLocation(coords[0], coords[1]);
		
		// AJK: 07/28/06 BEGIN
		//   one more experiment
		AffineTransform xfrm = 
			((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas()
			.getM_grafx().getM_currXform();
		try
		{
		xfrm.inverseTransform(new Point2D.Double (coords[0], coords[1]), ptOut);
		}
		catch (Exception e) { e.printStackTrace(); }
        // AJK: 07/28/06 END
		Rectangle2D.Double inter = new Rectangle2D.Double();
//      Rectangle2D rect = shape.getBounds2D();
		
		Rectangle2D rect = comp.getHitBox();
	
		
      Rectangle2D.intersect(focus, rect, inter);
      ptOut.setLocation(
          inter.getCenterX(),
          inter.getCenterY());		
		return ptOut;
	};
	
	*/

	
	public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut) {

		Rectangle2D rectImage = comp.getHitBox();
		double oldLeft = rectImage.getMinX();
		double oldRight = rectImage.getMaxX();
		double oldWidth = rectImage.getWidth();
		double oldHeight = rectImage.getHeight();
		double oldTop = rectImage.getMinY();
		double oldBottom = rectImage.getMaxY();
		double oldDeltaX, oldDeltaY;
		
		double newLeft = comp.getTopLeft()[0];
		double newTop = comp.getTopLeft()[1];
		double newRight = comp.getBottomRight()[0];
		double newBottom = comp.getBottomRight()[1];
		double newWidth = newRight - newLeft;
		double newHeight = newBottom - newTop;		
		
		double newDeltaX = (nv.getXPosition() - newLeft) / newWidth;
		double newDeltaY = (nv.getYPosition() - newTop) / newHeight;
		
		oldDeltaX = newDeltaX * oldWidth;
		oldDeltaY = newDeltaY * oldHeight;

		// AJK: 07/29/06 BEGIN
		//    one other experiment, just return center of bounds
//		ptOut = new Point2D.Double (oldLeft + oldDeltaX, 
//				oldTop + oldDeltaY);
////        ptOut = new Point2D.Double (focus.getCenterX(), focus.getCenterY());
//		

//		ptOut = new Point2D.Double (rectImage.getCenterX(),
//				rectImage.getCenterY());
		
		
  
		Rectangle2D.Double inter = new Rectangle2D.Double();
//        Rectangle2D rect = shape.getBounds2D();
		
		Rectangle2D rect = new Rectangle2D.Double(
				oldLeft + (0.5 * oldDeltaX),
				oldTop + (0.5 * oldDeltaY),
				oldDeltaX, oldDeltaY);
	
		
        Rectangle2D.intersect(focus, rect, inter);
        ptOut.setLocation(
            inter.getCenterX(),
            inter.getCenterY());
 
//		System.out.println ("returning Center: " + ptOut);
//		System.out.println ("Within bounds:  " + focus);
		return ptOut;
	};

	// AJK: 07/28/06 END



	public Color getColor() {
//		VisualStyle visualStyle = view.getVisualStyle();
//		NodeAppearanceCalculator nodeAppearanceCalculator = visualStyle.getNodeAppearanceCalculator();
//		NodeColorCalculator nodeColorCalc = nodeAppearanceCalculator.getNodeFillColorCalculator();
//		return nodeColorCalc.calculateNodeColor(nv.getNode(), view.getNetwork());
		return Color.WHITE;
	}

}
