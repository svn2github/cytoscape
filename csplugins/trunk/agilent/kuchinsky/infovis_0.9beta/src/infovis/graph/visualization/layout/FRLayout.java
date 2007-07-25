/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Implements the Fruchterman-Reingold algorithm for node layout.
 * 
 * Taken almost verbatim from Jung (jung.sourceforge.net)
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 * @infovis.factory GraphLayoutFactory "Fruchterman&Reingold (Spring)"
 */
public class FRLayout extends BasicSpringLayout {
    public static final FRLayout instance = new FRLayout();
    
    protected double forceConstant;
    protected double temperature;

    protected void initializeLayout(Rectangle2D bounds) {
        super.initializeLayout(bounds);
        currentIteration = 0;
        temperature = bounds.getWidth() / 10;
        forceConstant = 
            0.75 * 
            Math.sqrt(bounds.getHeight()*bounds.getWidth()
            / getVerticesCount());
    }
    
    public String getName() {
        return "Fruchterman&Reingold (Spring)";
    }
    
    public double repulsiveForce(double length) {
        return (forceConstant * forceConstant) / length; 
    }
    
    public double attractiveForce(double length) {
        return (length * length) / forceConstant;
    }
    
    public void calcRepulsion(int v) {
        dispX.setExtend(v, 0);
        dispY.setExtend(v, 0);
        boolean dm = dontMove(v);
    
        for (RowIterator iter2 = vertexIterator(); iter2.hasNext();) {
            int u = iter2.nextRow();
            
            if (v != u) {
                if (dm && dontMove(u)) continue;
                double dx = getX(v) - getX(u);
                double dy = getY(v) - getY(u);
    
                double length = dist(dx, dy);
    
                double force = repulsiveForce(length);
    
                double ddx = (dx / length) * force;
                double ddy = (dy / length) * force;
                dispX.addExtend(v, ddx);
                dispY.addExtend(v, ddy);
            }
        }
    }
    
    public void calcAttraction(int e) {
        int v = visualization.getFirstVertex(e);
        int u = visualization.getSecondVertex(e);
        boolean dmv = dontMove(v);
        boolean dmu = dontMove(u);
        if (dmv && dmu) return;
    
        double dx = getX(v) - getX(u);
        double dy = getY(v) - getY(u);
    
        double length = dist(dx, dy);
    
        double force = attractiveForce(length);
    
        double ddx = (dx / length) * force;
        double ddy = (dy / length) * force;
        if (! dmv) {
            dispX.addExtend(v, -ddx);
            dispY.addExtend(v, -ddy);
        }
        if (! dmu) {
            dispX.addExtend(u, +ddx);
            dispY.addExtend(u, +ddy);
        }
    }

    public boolean calcPosition(int v) {
        Rectangle2D.Float r = getRectAt(v);
        double disp = dist(dispX.get(v), dispY.get(v));
        if (disp == 0) {
            return false;
        }

        double dx = dispX.get(v) / disp
                * Math.min(disp, temperature);

        double dy = dispY.get(v) / disp
                * Math.min(disp, temperature);
        
        double nx = r.x + dx;
        double ny = r.y + dy;

//        nx = Math.max(bounds.getMinX(), nx);
//        nx = Math.min(bounds.getMaxX(), nx);
//        ny = Math.max(bounds.getMinY(), ny);
//        ny = Math.min(bounds.getMaxY(), ny);
        double borderWidth = vs.getMaxSize()/2;
        //double newXPos = nx;
        if (nx< borderWidth) {
            nx = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (nx > (bounds.getWidth() - borderWidth)) {
            nx = bounds.getWidth() - borderWidth - Math.random()
                    * borderWidth * 2.0;
        }

        //double newYPos = ny;
        if (ny < borderWidth) {
            ny = borderWidth + Math.random() * borderWidth * 2.0;
        } else if (ny > (bounds.getHeight() - borderWidth)) {
            ny = bounds.getHeight() - borderWidth
                    - Math.random() * borderWidth * 2.0;
        }

        if (r.x != (float)nx || r.y != (float)ny) {
            boolean moved =
                ((int)r.x) != (int)nx
                || ((int)r.y) != (int)ny;
            r.x = (float)nx;
            r.y = (float)ny;
            setShapeAt(v, r);
            return moved;
        }
        return false;

    }

    protected void cool() {
        temperature *= (1.0 - currentIteration / (double) maxIterations);
    }

    /**
     * Returns true once the current iteration has passed the maximum count,
     * <tt>MAX_ITERATIONS</tt>.
     */
    public boolean isFinished() {
        if (currentIteration >= maxIterations) { 
//            System.out.println("Reached currentIteration =" + currentIteration + ", maxIterations=" + maxIterations);
            return true; 
        } 
        return false;
    }
}
