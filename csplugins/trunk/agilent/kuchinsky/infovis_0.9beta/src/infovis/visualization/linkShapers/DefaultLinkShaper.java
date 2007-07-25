/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.linkShapers;

import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.visualization.*;

import java.awt.Shape;
import java.awt.geom.*;

/**
 * Class DefaultLinkShaper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * @infovis.factory LinkShaperFactory "Straight Line"
 */
public class DefaultLinkShaper implements LinkShaper {
    protected Visualization visualization;
    protected ShapeColumn nodeShapes;
    protected Point2D startPos;
    protected Point2D endPos;

    public DefaultLinkShaper(
        Visualization visualization,
        ShapeColumn nodeShapes) {
        init(visualization, nodeShapes);
    }
    
    public DefaultLinkShaper() {
    }
    
    public void init(
            Visualization visualization,
            ShapeColumn nodeShapes) {
        this.visualization = visualization;
        this.nodeShapes = nodeShapes;
    }
    
    public String getName() {
        return "Straight Line";
    }

    public Shape computeLinkShape(
        int link,
        NodeAccessor accessor,
        Shape s) {
        if (nodeShapes == null)
            return null;
        int start = accessor.getStartNode(link);
        int end = accessor.getEndNode(link);
        if (start == -1 || end == -1)
            return null;
        Shape startShape = (Shape) nodeShapes.get(start);
        Shape endShape = (Shape) nodeShapes.get(end);
        if (startShape == null || endShape == null)
            return null;

        if (start == end) { // loop
            return createSelfLink(startShape);
        }

        Rectangle2D startRect = startShape.getBounds2D();
        Rectangle2D endRect = endShape.getBounds2D();

        short orientation = visualization.getOrientation();
        if (orientation == Orientation.ORIENTATION_INVALID) {
            orientation =
                Orientation.directionOrientation(
                    endRect.getCenterX() - startRect.getCenterX(),
                    endRect.getCenterY() - startRect.getCenterY());
        }
        startPos = linkStart(startShape, orientation, startPos);
        endPos = linkEnd(endShape, orientation, endPos);
        return createLink(
            startPos,
            orientation,
            endPos,
            orientation,
            s);
    }

    public Shape createSelfLink(Shape nodeShape) {
        Rectangle2D nodeRect = nodeShape.getBounds2D();
        Ellipse2D e = new Ellipse2D.Double();
        e.setFrame(nodeRect);
        return e;
//            nodeRect.getMaxX(),
//            nodeRect.getCenterY(),
//            nodeRect.getWidth() / 2,
//            nodeRect.getHeight() / 2);
    }

    public Shape createLink(
        Point2D startPos,
        int startOrientation,
        Point2D endPos,
        int endOrientation,
        Shape prevShape) {
        Line2D.Double line;
        if (prevShape instanceof Line2D.Double) {
            line = (Line2D.Double) prevShape;
        }
        else {
            line = new Line2D.Double();
        }
        line.x1 = startPos.getX();
        line.y1 = startPos.getY();
        line.x2 = endPos.getX();
        line.y2 = endPos.getY();

        return line;
    }

    public Point2D linkStart(Shape s, int orientation, Point2D ptRet) {
        return defaultLinkStart(s, orientation, ptRet);
    }

    public Point2D linkEnd(Shape s, int orientation, Point2D ptRet) {
        return defaultLinkEnd(s, orientation, ptRet);
    }
    /**
     * Computes the starting point of link.
     *
     * @param s the Node shape
     * @param orientation the overall orientation
     * @param ptRet the returned reference point
     * 
     * @return the starting point of a link
     */
    public static Point2D defaultLinkStart(
        Shape s,
        int orientation,
        Point2D ptRet) {
        if (ptRet == null) {
            ptRet = new Point2D.Float();
        }
        Rectangle2D bounds = s.getBounds2D();
        switch (orientation) {
            case Orientation.ORIENTATION_SOUTH :
                ptRet.setLocation(
                    bounds.getCenterX(),
                    bounds.getMaxY());
                break;
            case Orientation.ORIENTATION_NORTH :
                ptRet.setLocation(
                    bounds.getCenterX(),
                    bounds.getMinY());
                break;
            case Orientation.ORIENTATION_EAST :
                ptRet.setLocation(
                    bounds.getMaxX(),
                    bounds.getCenterY());
                break;
            case Orientation.ORIENTATION_WEST :
                ptRet.setLocation(
                    bounds.getMinX(),
                    bounds.getCenterY());
                break;
        }
        return ptRet;
    }

    /**
     * Compute the ending point of link.
     *
     * @param s the Node Shape
     * @param orientation the visualization orientation
     * @param ptRet the returned point
     * 
     * @return the ending point of a link
     */
    public static Point2D defaultLinkEnd(
        Shape s,
        int orientation,
        Point2D ptRet) {
        if (ptRet == null) {
            ptRet = new Point2D.Float();
        }
        Rectangle2D bounds = s.getBounds2D();
        switch (orientation) {
            case Orientation.ORIENTATION_NORTH :
                ptRet.setLocation(
                    bounds.getCenterX(),
                    bounds.getMaxY());
                break;
            case Orientation.ORIENTATION_SOUTH :
                ptRet.setLocation(
                    bounds.getCenterX(),
                    bounds.getMinY());
                break;
            case Orientation.ORIENTATION_WEST :
                ptRet.setLocation(
                    bounds.getMaxX(),
                    bounds.getCenterY());
                break;
            case Orientation.ORIENTATION_EAST :
                ptRet.setLocation(
                    bounds.getMinX(),
                    bounds.getCenterY());
                break;
        }
        return ptRet;
    }

}
