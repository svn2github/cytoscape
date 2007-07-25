/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.linkShapers;

import java.awt.Shape;
import java.awt.geom.*;

/**
 * Class CurvedLinkShaper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * @infovis.factory LinkShaperFactory "Curved"
 */
public class CurvedLinkShaper extends DefaultLinkShaper {
    protected double           angle;
    protected transient double sin;
    protected transient double cos;

    public CurvedLinkShaper(double angle) {
        setAngle(angle);
    }

    public CurvedLinkShaper() {
        this(Math.PI / 2);
    }

    public String getName() {
        return "Curved";
    }

    /**
     * Change the starting angle in radians.
     * 
     * @param angle
     *            the starting angle.
     */
    public void setAngle(double angle) {
        this.angle = angle;
        sin = Math.sin(angle);
        cos = Math.cos(angle);
    }

    /**
     * Returns the starting angle.
     * 
     * @return the starting angle.
     */
    public double getAngle() {
        return angle;
    }

    public Shape createLink(
            Point2D startPos,
            int startOrientation,
            Point2D endPos,
            int endOrientation,
            Shape prevShape) {
        QuadCurve2D.Double quad;
        if (prevShape instanceof QuadCurve2D.Double) {
            quad = (QuadCurve2D.Double) prevShape;
        }
        else {
            quad = new QuadCurve2D.Double();
        }

        quad.x1 = startPos.getX();
        quad.y1 = startPos.getY();
        quad.x2 = endPos.getX();
        quad.y2 = endPos.getY();

        double x = (quad.x2 - quad.x1) / 2;
        double y = (quad.y2 - quad.y1) / 2;
        quad.ctrlx = quad.x1 + x * cos - y * sin;
        quad.ctrly = quad.y1 + x * sin + y * cos;

        return quad;
    }

    public Point2D linkStart(Shape s, int orientation, Point2D ptRet) {
        if (ptRet == null) {
            ptRet = new Point2D.Float();
        }
        Rectangle2D bounds = s.getBounds2D();
        ptRet.setLocation(bounds.getCenterX(), bounds.getCenterY());
        return ptRet;
    }

    public Point2D linkEnd(Shape s, int orientation, Point2D ptRet) {
        return linkStart(s, orientation, ptRet);
    }
}
