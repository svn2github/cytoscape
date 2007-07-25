/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.column.DoubleColumn;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Class BasicSpringLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public abstract class BasicSpringLayout extends RandomGraphLayout {
    protected int currentIteration;
    protected int maxIterations = 700;    
    protected DoubleColumn dispX;
    protected DoubleColumn dispY;
    public static final double EPSILON = 0.000001;
    protected boolean sync = true;
    
    public void computeShapes(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        super.computeShapes(bounds, vis);
        initializeLayout(bounds);
        if (sync) {
            long startTime = System.currentTimeMillis();
            long delta = startTime;
            do {
                incrementLayout(bounds);
                delta = System.currentTimeMillis() - startTime;
            }
            while(delta < 100 && !isFinished());
        }
        else {
            incrementLayout(bounds);
        }
    }
    
    protected void initializeLayout(Rectangle2D bounds) {
        currentIteration = 0;
        if (dispX == null) {
            dispX = new DoubleColumn("dispX");
            dispY = new DoubleColumn("dispY");
        }
        else {
            dispX.clear();
            dispY.clear();
        }
    }

    public void incrementLayout(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        try {
            setVisualization(vis);
            incrementLayout(bounds);
        }
        finally {
            unsetVisualization();
        }
    }
    
    protected void incrementLayout(Rectangle2D bounds) {
        if (graph.getVerticesCount() <= 1) {
            return;
        }
        currentIteration++;
        calcRepulsion();
        calcAttraction();
        boolean moved = calcPosition();
        if (! moved) {
            currentIteration = maxIterations;
        }
        cool();
    }

    protected boolean calcPosition() {
        boolean moved = false;
        for (RowIterator iter = vertexIterator(); iter.hasNext();) {
            int v = iter.nextRow();
            if (dontMove(v)) continue;
            if (calcPosition(v)) {
                moved = true;
            }
        }
        return moved;
    }

    protected void calcAttraction() {
        for (RowIterator iter = edgeIterator(); iter.hasNext();) {
            int e = iter.nextRow();
            calcAttraction(e);
        }
    }

    protected void calcRepulsion() {
        for (RowIterator iter = vertexIterator(); iter.hasNext(); ) {
            int v1 = iter.nextRow();
            calcRepulsion(v1);
        }
    }
    
    protected abstract void calcRepulsion(int v);
    protected abstract void calcAttraction(int e);
    
    /**
     * Calculates the new position of the specified vertex.
     * 
     * @param v the vertex
     * @return <code>true</code> if the node has moved, 
     * <code>false</code> otherwise.
     */
    protected abstract boolean calcPosition(int v);

    protected void cool() {
    }
    
    public static double dist(double dx, double dy) {
        return Math.max(EPSILON,
                Math.sqrt((dx * dx) + (dy * dy)));
    }
    
    public float getY(int vertex) {
        return getRectAt(vertex).y;
    }
    
    public void setY(int vertex, float val) {
        getRectAt(vertex).y = val;
    }

    public float getX(int vertex) {
        return getRectAt(vertex).x;
    }
    
    public void setX(int vertex, float val) {
        getRectAt(vertex).x = val;
    }

    public boolean dontMove(int vertex) {
        if (fixed == null) {
            return false;
        }
        if (fixed.isValueUndefined(vertex)) {
            return false;
        }
        return fixed.get(vertex);
    }    

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * This one is an incremental visualization.
     */
    public boolean isIncremental() {
        return true;
    }

}
