/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.awt.*;
import java.awt.geom.*;

/**
 * Class StrokedPath
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public abstract class StrokedPath implements PathIterator {
    static Point2D ignorePt = new Point2D.Double();

//    public static void intersect(
//        Shape s,
//        Rectangle2D hitbox,
//        GeneralPath path) {
//        double coords[] = new double[8];
//        Point2D ptRet = ignorePt;
//        int type = SEG_CLOSE;
//        for (PathIterator pi = s.getPathIterator(null);
//            !pi.isDone();
//            pi.next()) {
//            type = pi.currentSegment(coords);
//            Point2D inter =
//                intersectsSegment(coords, type, hitbox, ptRet);
//            if (inter != null) {
//                addIntersection(path, coords, type, hitbox);
//            }
//        }
//    }
//
//    public static void addIntersection(
//        GeneralPath path,
//        double[] coords,
//        int type,
//        Rectangle2D hitbox) {
//        switch (type) {
//            case SEG_MOVETO :
//                path.moveTo((float) coords[0], (float) coords[1]);
//                break;
//            case SEG_LINETO :
//                //TODO finish code
//                break;
//        }
//    }

    public static Point2D pointOnPathIn(
        Shape s,
        Rectangle2D hitbox,
        Point2D retPt) {
        if (retPt == null) {
            retPt = new Point2D.Double();
        }
        if (intersects(s, hitbox, retPt) != null) {
            return retPt;
        }
        else {
            return null;
        }
    }

    public boolean pointOnPathIn(
        double[] coords,
        int type,
        Rectangle2D hitbox,
        Point2D retPt) {
        switch (type) {
            case SEG_MOVETO :
                if (hitbox.contains(coords[0], coords[1])) {
                    retPt.setLocation(coords[0], coords[1]);
                    return true;
                }
                break;
            case SEG_LINETO :
                if (hitbox
                    .intersectsLine(
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3])) {
                    //TODO
                    ;
                    //Rectangle2D.intersect(hitbox, src2, dest);
                    //
                }
        }
        return false;
    }

    public static Point2D intersects(
        Shape s,
        Rectangle2D hitbox,
        Stroke stroke,
        Point2D retPt) {
        if (stroke instanceof BasicStroke) {
            BasicStroke basicStroke = (BasicStroke) stroke;
            return intersects(
                s,
                hitbox,
                basicStroke.getLineWidth(),
                retPt);
        }
        else {
            if (stroke.createStrokedShape(s).intersects(hitbox)) {
                //TODO
                // should put something in the point
                retPt.setLocation(
                    hitbox.getCenterX(),
                    hitbox.getCenterY());
                return retPt;
            }
            else {
                return null;
            }
        }
    }

    public static Point2D intersects(
        Shape s,
        Rectangle2D hitbox,
        double width,
        Point2D rtPoint) {
        double w2 = width / 2;
        Rectangle2D newHit =
            new Rectangle2D.Double(
                hitbox.getMinX() - w2,
                hitbox.getMinY() - w2,
                hitbox.getWidth() + width,
                hitbox.getHeight() + width);
        return intersects(s, newHit, rtPoint);
    }

    public static boolean intersects(Shape s, Rectangle2D hitbox) {
        return intersects(s, hitbox, ignorePt) != null;
    }

    public static Point2D intersects(
        Shape s,
        Rectangle2D hitbox,
        Point2D rtPoint) {
        double coords[] = new double[8];
        Point2D ret = null;
        for (PathIterator pi = s.getPathIterator(null);
            ret == null && !pi.isDone();
            pi.next()) {
            ret =
                intersectsSegment(
                    coords,
                    pi.currentSegment(coords),
                    hitbox,
                    rtPoint);
        }
        return ret;
    }

    public static Point2D intersectsSegment(
        double coords[],
        int type,
        Rectangle2D hitbox,
        Point2D rtPoint) {
        if (type == SEG_CLOSE)
            return null;

        if (type == SEG_MOVETO) {
            coords[6] = coords[0];
            coords[7] = coords[1];
            if (hitbox.contains(coords[0], coords[1])) {
                rtPoint.setLocation(coords[0], coords[1]);
                return rtPoint;
            }
            else
                return null;
        }
        double lastX = coords[6];
        double lastY = coords[7];
        if (type == SEG_LINETO) {
            coords[2] = lastX;
            coords[3] = lastY;
            coords[6] = coords[0];
            coords[7] = coords[1];

            if (hitbox
                .intersectsLine(
                    coords[0],
                    coords[1],
                    coords[2],
                    coords[3])) {
                return ptInRect(
                    coords[0],
                    coords[1],
                    lastX,
                    lastY,
                    hitbox,
                    rtPoint);
            }
            else {
                return null;
            }
        }

        double xmin = lastX;
        double ymin = lastY;
        double xmax = xmin;
        double ymax = ymin;

        xmin = Math.min(xmin, coords[0]);
        ymin = Math.min(ymin, coords[1]);
        xmax = Math.max(xmax, coords[0]);
        ymax = Math.max(ymax, coords[1]);

        xmin = Math.min(xmin, coords[2]);
        ymin = Math.min(ymin, coords[3]);
        xmax = Math.max(xmax, coords[2]);
        ymax = Math.max(ymax, coords[3]);

        if (type == SEG_QUADTO) {
            coords[6] = coords[2];
            coords[7] = coords[3];
            if (!hitbox
                .intersects(xmin, ymin, xmax - xmin, ymax - ymin))
                return null;
            if (hitbox.contains(lastX, lastY)) {
                rtPoint.setLocation(lastX, lastY);
                return rtPoint;
            }
            else if (hitbox.contains(coords[2], coords[3])) {
                rtPoint.setLocation(coords[2], coords[3]);
                return rtPoint;
            }
            return intersects(
                iterator(lastX, lastY, coords, type),
                coords,
                hitbox,
                rtPoint);
        }

        xmin = Math.min(xmin, coords[4]);
        ymin = Math.min(ymin, coords[5]);
        xmax = Math.max(xmax, coords[4]);
        ymax = Math.max(ymax, coords[5]);

        if (type == SEG_CUBICTO) {
            coords[6] = coords[4];
            coords[7] = coords[5];
            if (!hitbox
                .intersects(xmin, ymin, xmax - xmin, ymax - ymin))
                return null;
            if (hitbox.contains(lastX, lastY)) {
                rtPoint.setLocation(lastX, lastY);
                return rtPoint;
            }
            else if (hitbox.contains(coords[4], coords[5])) {
                rtPoint.setLocation(coords[4], coords[5]);
                return rtPoint;
            }
            return intersects(
                iterator(lastX, lastY, coords, type),
                coords,
                hitbox,
                rtPoint);
        }
        return null;
    }

    public static PathIterator iterator(
        double lastX,
        double lastY,
        double[] coords,
        int type) {
        switch (type) {
            case SEG_LINETO :
                return new Line2D
                    .Double(lastX, lastY, coords[0], coords[1])
                    .getPathIterator(null);

            case SEG_QUADTO :
                return new QuadCurve2D
                    .Double(
                        lastX,
                        lastY,
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3])
                    .getPathIterator(null, 1);

            case SEG_CUBICTO :
                return new CubicCurve2D
                    .Double(
                        lastX,
                        lastY,
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3],
                        coords[4],
                        coords[5])
                    .getPathIterator(null, 1);
        };
        return null;
    }

    public static Point2D ptInRect(
        double firstX,
        double firstY,
        double lastX,
        double lastY,
        Rectangle2D hitbox,
        Point2D rtPoint) {
        int maxIter = 20; // max # if dichotomy
        while (!hitbox.contains(firstX, firstY) && maxIter-- != 0) {
            double halfX = (firstX + lastX) / 2;
            double halfY = (firstY + lastY) / 2;
            if (hitbox.intersectsLine(firstX, firstY, halfX, halfY)) {
                lastX = halfX;
                lastY = halfY;
            }
            else {
                firstX = halfX;
                firstY = halfY;
            }
        }
        rtPoint.setLocation(firstX, firstY);
        return rtPoint;
    }

    public static Point2D intersects(
        PathIterator pi,
        double[] coords,
        Rectangle2D hitbox,
        Point2D rtPoint) {
        if (pi.currentSegment(coords) == SEG_MOVETO) {
            double lastX = coords[0];
            double lastY = coords[1];
            for (; !pi.isDone(); pi.next()) {
                pi.currentSegment(coords);
                if (hitbox
                    .intersectsLine(
                        lastX,
                        lastY,
                        coords[0],
                        coords[1])) {
                    return ptInRect(
                        coords[0],
                        coords[1],
                        lastX,
                        lastY,
                        hitbox,
                        rtPoint);
                }
                lastX = coords[0];
                lastY = coords[1];
            }
        }
        return null;
    }

    public static Line2D.Double clip(
        Line2D.Double l,
        Rectangle2D bounds) {
        double x1 = l.x1;
        double y1 = l.y1;
        double x2 = l.x2;
        double y2 = l.y2;
        int out1 = bounds.outcode(x1, y1);
        int out2 = bounds.outcode(x2, y2);
        if (out1 != 0 && out2 != 0 && (out1 & out2) != 0)
            return null;

        int cnt = 0;
        while (out1 != 0) {
            if (cnt++ > 2)
                throw new RuntimeException("too many loops in clip1");
            if ((out1 & out2) != 0) {
                return null;
            }
            if ((out1 & (Rectangle2D.OUT_LEFT | Rectangle2D.OUT_RIGHT))
                != 0) {
                double x = bounds.getX();
                if ((out1 & Rectangle2D.OUT_RIGHT) != 0) {
                    x += bounds.getWidth();
                }
                y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                x1 = x;
            }
            else {
                double y = bounds.getY();
                if ((out1 & Rectangle2D.OUT_BOTTOM) != 0) {
                    y += bounds.getHeight();
                }
                x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                y1 = y;
            }
            out1 = bounds.outcode(x1, y1);
        }

        cnt = 0;
        while (out2 != 0) {
            if (cnt++ > 2)
                throw new RuntimeException("too many loops in clip2");
            if ((out2 & (Rectangle2D.OUT_LEFT | Rectangle2D.OUT_RIGHT))
                != 0) {
                double x = bounds.getX();
                if ((out2 & Rectangle2D.OUT_RIGHT) != 0) {
                    x += bounds.getWidth();
                }
                y2 = y2 + (x - x2) * (y2 - l.y1) / (x2 - l.x1);
                x2 = x;
            }
            else {
                double y = bounds.getY();
                if ((out2 & Rectangle2D.OUT_BOTTOM) != 0) {
                    y += bounds.getHeight();
                }
                x2 = x2 + (y - y2) * (x2 - l.x1) / (y2 - l.y1);
                y2 = y;
            }
            out2 = bounds.outcode(x2, y2);
        }
        l.x1 = x1;
        // reload because it could have been modified the first loop
        l.y1 = y1;
        l.x2 = x2;
        l.y2 = y2;

        return l;
    }

    public static Line2D.Double clip(
        Line2D.Double l,
        Shape shape) {
        
        if (shape instanceof Rectangle2D) {
            return clip(l, (Rectangle2D)shape);
        }
        if (! shape.contains(l.getX2(), l.getY2()))
            return l;
        double[] coords = new double[6];
        double prev_x = 0;
        double prev_y = 0;
        for (PathIterator iter = shape.getPathIterator(null, 1); ! iter.isDone(); ) {
            switch(iter.currentSegment(coords)) {
            case SEG_LINETO:
                if (l.intersectsLine(prev_x, prev_y, coords[0], coords[1])) {
                    ;//TODO+++
                }
                // fall through
            case SEG_MOVETO:
                prev_x = coords[0];
                prev_y = coords[1];
                break;
            }
        }
        return l;
    }
    
    public static double dist(double dx, double dy) {
        return Math.sqrt(dx*dx+dy*dy);
    }
    
    public static double computePathLength(Shape shape) {
        PathIterator pi = shape.getPathIterator(null, 1);
        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        double firstX = 0, firstY = 0;
        double length = 0;
        
        while(! pi.isDone()) {
            int type = pi.currentSegment(coords);
            switch(type) {
            case PathIterator.SEG_MOVETO:
                firstX = lastX = coords[0];
                firstY = lastY = coords[1];
                break;
           case PathIterator.SEG_LINETO:
               length += dist(lastX - coords[0], lastY - coords[1]);
               lastX = coords[0];
               lastY = coords[1];
               break;
           case PathIterator.SEG_CLOSE:
               length += dist(lastX - firstX, lastY - firstY);
               break;
            }
        }
        return length;
    }
    
    public static Point2D computePosAtLength(Shape shape, double l, Point2D pt) {
        PathIterator pi = shape.getPathIterator(null, 1);
        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        double firstX = 0, firstY = 0;
        double length = 0;
        double d;
        
        while(! pi.isDone()) {
            int type = pi.currentSegment(coords);
            switch(type) {
            case PathIterator.SEG_MOVETO:
                firstX = lastX = coords[0];
                firstY = lastY = coords[1];
                break;
            case PathIterator.SEG_CLOSE:
                coords[0] = firstX;
                coords[1] = firstY;
                // fall though
           case PathIterator.SEG_LINETO:
               d = dist(lastX - coords[0], lastY - coords[1]);
               if ((length+d)>l) {
                   double frac = (l - length) / d;
                   lastX = lastX * (1-frac) + coords[0] * frac;
                   lastY = lastY * (1-frac) + coords[1] * frac;
               }
               else {
                   lastX = coords[0];
                   lastY = coords[1];
               }
               length += d;
               break;
            }
            if (length >= l) {
                break;
            }
        }
        pt.setLocation(lastX, lastY);
        return pt;
    }
    
    public static Point2D computeGeneralPointAt(Shape shape, double pos, Point2D pt) {
        if (pos == 0) {
            return computePosAtLength(shape, 0, pt);
        }
        double length = computePathLength(shape);
        return computePosAtLength(shape, pos*length, pt);
    }
    
    public static Point2D pointAt(Shape shape, double pos, Point2D retPt) {
        if (retPt == null) {
            retPt = new Point2D.Double();
        }
        if (shape instanceof Line2D) {
            Line2D line = (Line2D) shape;
            if (pos == 0) {
                retPt.setLocation(line.getP1());
            }
            else if (pos == 1) {
                retPt.setLocation(line.getP2());
            }
            else {
                retPt.setLocation(
                        line.getX1()*(1-pos)+line.getX2()*pos, 
                        line.getY1()*(1-pos)+line.getY2()*pos);
            }
        }
        else if (shape instanceof CubicCurve2D) {
            CubicCurve2D curve = (CubicCurve2D) shape;
            if (pos == 0) {
                retPt.setLocation(curve.getP1());
            }
            else if (pos == 1) {
                retPt.setLocation(curve.getP2());
            }
            else {
                computeGeneralPointAt(shape, pos, retPt);
            }
        }
        else {
            computeGeneralPointAt(shape, pos, retPt);
        }
        return retPt;
    }
}
