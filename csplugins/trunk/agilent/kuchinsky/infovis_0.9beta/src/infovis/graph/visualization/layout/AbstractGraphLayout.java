/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Visualization;
import infovis.column.BooleanColumn;
import infovis.column.ShapeColumn;
import infovis.graph.GraphProxy;
import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;
import infovis.visualization.render.VisualSize;

import java.awt.*;
import java.awt.Component;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Abstract implementation of NodeLinkGraphLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
  */
public abstract class AbstractGraphLayout extends GraphProxy 
    implements NodeLinkGraphLayout {    
    public static final String COLUMN_FIXED = "#fixed";
    protected transient NodeLinkGraphVisualization visualization;
    protected transient Rectangle2D bounds;
    protected transient ShapeColumn shapes;
    protected transient BooleanColumn fixed;
    protected transient VisualSize vs;
    protected Dimension preferredSize;
    
    protected AbstractGraphLayout() {
        super(null);
    }

    public NodeLinkGraphVisualization getVisualization() {
        return visualization;
    }
    
    protected void setVisualization(Visualization vis) {
//        if (visualization != null) {
//            throw new RuntimeException(
//            "Reentering AbstractGraphLayout.computeShapes");
//        }
        if (visualization == null) {
            visualization =
                (NodeLinkGraphVisualization)vis.findVisualization(NodeLinkGraphVisualization.class);
            setGraph(visualization);
        }
        else {
            assert(visualization==(NodeLinkGraphVisualization)vis.findVisualization(NodeLinkGraphVisualization.class));
        }
        shapes = visualization.getShapes();
        fixed = BooleanColumn.getColumn(visualization.getVertexTable(), COLUMN_FIXED);
        vs = VisualSize.get(vis);
    }
    
    protected void unsetVisualization() {
        //visualization = null;
        //setGraph(null);
        //shapes = null;
        //fixed = null;
    }

    protected void invalidateVisualization() {
        if (visualization != null)
            visualization.invalidate();
    }
    
    protected void recomputeSizes() {
        for (RowIterator iter = vertexIterator(); iter.hasNext();) {
            int v = iter.nextRow();

            Rectangle2D.Float rect = getRectAt(v);
            if (rect == null) {
                rect = createRect();
            }
            rect = initRect(rect);
            setRectSizeAt(v, rect);
            setShapeAt(v, rect);
        }
    }

    public Rectangle2D.Float getRectAt(int row) {
        return visualization.getRectAt(row);
    }
        
    public Rectangle2D.Float createRect() {
        return RectPool.allocateRect();
    }
    
    public Rectangle2D.Float initRect(Rectangle2D.Float rect) {
        return rect;
    }
    
    public ShapeColumn getShapes() {
        return visualization.getShapes();
    }
    
    public void setRectSizeAt(int row, Rectangle2D.Float rect) {
        vs.setRectSizeAt(row, rect);
    }

//    public double getSizeAtNO(int row) {
//        return vs.getSizeAta(row);
//    }

    public void setShapeAt(int row, Shape s) {
        visualization.setShapeAt(row, s);
    }
    
    public Rectangle2D getBounds() {
        return visualization.getBounds();
    }

    public short getOrientation() {
        return visualization.getOrientation();
    }

    public Component getParent() {
        return visualization.getParent();
    }
    
    public ShapeColumn getLinkShapes() {
        return visualization.getLinkShapes();
    }
    
    public void computeShapes(
            Rectangle2D bounds,
            Visualization vis) {
        try {
            setVisualization(vis);
            this.bounds = bounds;
            computeShapes(bounds, visualization);
        }
        finally {
            unsetVisualization();
        }
    }
    
    public void computeShapes(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        recomputeSizes();
    }
    
    public void incrementLayout(Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
    }
    
    public boolean isFinished() {
        return true;
    }
    
    public Dimension getPreferredSize(Visualization vis) {
        return preferredSize;
    }
    
    public void invalidate(Visualization vis) {
        preferredSize = null;
    }
}
