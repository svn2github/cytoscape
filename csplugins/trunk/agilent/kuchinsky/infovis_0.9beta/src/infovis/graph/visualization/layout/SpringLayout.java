/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.column.DoubleColumn;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Class SpringLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 * @infovis.factory GraphLayoutFactory "Spring"
 */
public class SpringLayout extends BasicSpringLayout {
    public static final int RANGE = 100;
    protected double FORCE_CONSTANT = 1.0 / 3.0;
    public static final double STRETCH = 7.0;
    protected LengthFunction lengthFunction;
    protected DoubleColumn edgeLength;
    protected DoubleColumn edgedx;
    protected DoubleColumn edgedy;
    protected DoubleColumn repulsiondx;
    protected DoubleColumn repulsiondy;

    public SpringLayout() {
        this(UNITLENGTHFUNCTION);
    }
    
    public SpringLayout(LengthFunction fn) {
        super();
        this.lengthFunction = fn;
    }
    
    public String getName() {
        return "Spring";
    }
    
    public void initializeLayout(Rectangle2D bounds) {
        super.initializeLayout(bounds);
        if (edgeLength == null) {
            edgeLength = new DoubleColumn("edgeLength", graph.getEdgesCount());
            edgedx = new DoubleColumn("edgedx", graph.getEdgesCount());
            edgedy = new DoubleColumn("edgedy", graph.getEdgesCount());
            repulsiondx = new DoubleColumn("repulsiondx", graph.getEdgesCount());
            repulsiondy = new DoubleColumn("repulsiondy", graph.getEdgesCount());
        }
        else {
            dispX.clear();
            dispY.clear();
        }
        initializeEdges();
    }

    public boolean isFinished() {
        return false;
    }
    public void initializeEdges() {
        for (RowIterator iter = edgeIterator(); iter.hasNext(); ) {
            int edge = iter.nextRow();
            edgeLength.setExtend(edge, lengthFunction.getLength(edge));
        }    
    }
    
    protected void incrementLayout(Rectangle2D bounds) {
        for (RowIterator iter = vertexIterator(); iter.hasNext(); ) {
            int v = iter.nextRow();
            if (dispX.isValueUndefined(v)) {
                dispX.setExtend(v, 0);
                dispY.setExtend(v, 0);
            }
            else {
                dispX.set(v, dispX.get(v)/4);
                dispY.set(v, dispY.get(v)/4);
            }
            edgedx.setExtend(v, 0);
            edgedy.setExtend(v, 0);
            repulsiondx.setExtend(v, 0);
            repulsiondy.setExtend(v, 0);
        }
        super.incrementLayout(bounds);
    }
    
    public void calcRepulsion(int v1) {
        double dx = 0;
        double dy = 0;
        for (RowIterator iter2 = vertexIterator(); iter2.hasNext();) {
            int v2 = iter2.nextRow();
            if (dontMove(v2)) continue;
            if (v1 != v2) {
                double vx = getX(v1) - getX(v2);
                double vy = getY(v1) - getY(v2);
                double distance = (vx * vx) + (vy * vy);
                
                if (distance == 0) {
                    dx += Math.random();
                    dy += Math.random();
                }
                else if (distance < RANGE * RANGE) {
                    dx += vx / (distance * distance); 
                    dy += vy / (distance * distance);
                }
            }
        }
        double len = dx*dx + dy*dy;
        if (len > 0) {
            len = Math.sqrt(len) / 2;
            repulsiondx.addExtend(v1, dx / len);
            repulsiondy.addExtend(v1, dy / len);
        }
    }
    
    public void calcAttraction(int e) {
        int v1 = visualization.getFirstVertex(e);
        int v2 = visualization.getSecondVertex(e);
    
        double vx = getX(v1) - getX(v2);
        double vy = getY(v1) - getY(v2);
        double len = Math.max(EPSILON, dist(vx , vy));
        double desiredLen = lengthFunction.getLength(e);
        double deltaLength = len == 0 ? 0 : (desiredLen - len) / len;
    
        double force = deltaLength / FORCE_CONSTANT;
        force = force * Math.pow(
                STRETCH / 100.0,
                getDegree(v1) + getDegree(v2) - 2);
    
        double dx = force * vx;
        double dy = force * vy;
        edgedx.addExtend(v1, dx);
        edgedy.addExtend(v1, dy);
        edgedx.addExtend(v2, -dx);
        edgedy.addExtend(v2, -dy);
    }

    public boolean calcPosition(int v) {
        Rectangle2D.Float r = getRectAt(v);
        double newXDisp = dispX.addExtend(
                v, 
                repulsiondx.get(v) + edgedx.get(v));
        double newYDisp = dispY.addExtend(
                v, 
                repulsiondy.get(v) + edgedy.get(v));
        
        r.x += (float)Math.max(-5, Math.min(5, newXDisp));
        r.y += (float)Math.max(-5, Math.min(5, newYDisp));

        double borderWidth = bounds.getWidth() / 50.0;
        double newXPos = r.x;
        if (newXPos < borderWidth) {
            newXPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newXPos > (bounds.getWidth() - borderWidth)) {
            newXPos = bounds.getWidth() - borderWidth - Math.random()
                    * borderWidth * 2.0;
        }

        double newYPos = r.y;
        if (newYPos < borderWidth) {
            newYPos = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (newYPos > (bounds.getHeight() - borderWidth)) {
            newYPos = bounds.getHeight() - borderWidth
                    - Math.random() * borderWidth * 2.0;
        }

        if (r.x != newXPos || r.y != newYPos) {
            r.x = (float)newXPos;
            r.y = (float)newYPos;
            setShapeAt(v, r); // force notification
            return true;
        }
        return false;
    }
        
    /* ---------------Length Function------------------ */

    /**
     * If the edge is weighted, then override this method to show what the
     * visualized length is.
     * 
     * @author Danyel Fisher
     */
    public static interface LengthFunction {
        public double getLength(int e);
    }

    /**
     * Returns all edges as the same length: the input value
     * @author danyelf
     */
    public static final class UnitLengthFunction implements LengthFunction {

        int length;

        public UnitLengthFunction(int length) {
            this.length = length;
        }

        public double getLength(int e) {
            return length;
        }
    }

    public static final LengthFunction UNITLENGTHFUNCTION = new UnitLengthFunction(
            30);

}
