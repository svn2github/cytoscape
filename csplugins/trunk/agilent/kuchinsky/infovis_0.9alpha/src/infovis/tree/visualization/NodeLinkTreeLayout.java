/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.column.ShapeColumn;
import infovis.utils.RowIterator;
import infovis.visualization.Layout;
import infovis.visualization.Orientable;
import infovis.visualization.render.VisualSize;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

/**
 * Class NodeLinkTreeLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public abstract class NodeLinkTreeLayout implements Layout, Orientable {
    protected static final Logger                 logger = Logger
                                                                 .getLogger(NodeLinkTreeLayout.class);
    protected transient NodeLinkTreeVisualization visualization;
    protected transient NumberColumn              sizeColumn;
    protected transient ShapeColumn               shapeColumn;
    protected transient VisualSize                vs;
    protected transient Rectangle2D               bounds;

    protected NodeLinkTreeLayout() {
    }

    public void setVisualization(Visualization vis) {
        if (visualization != null) {
            throw new RuntimeException(
                    "Reentering NodeLinkTreeLayout.computeShapes");
        }
        visualization = (NodeLinkTreeVisualization) vis
                .findVisualization(NodeLinkTreeVisualization.class);
        sizeColumn = VisualSize.get(visualization).getSizeColumn();
        shapeColumn = visualization.getShapes();
        vs = VisualSize.get(vis);
        //shapeColumn.disableNotify();
    }

    public void unsetVisualization() {
        //shapeColumn.enableNotify();
        sizeColumn = null;
        shapeColumn = null;
        visualization = null;
    }

    public Dimension getPreferredSize(Visualization vis) {
        return null;
    }

    public void invalidate() {
        this.bounds = null;
    }

    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        this.bounds = bounds;
        try {
            setVisualization(vis);
            computeShapes(bounds, visualization);
        } finally {
            unsetVisualization();
        }
    }

    public abstract void computeShapes(
            Rectangle2D bounds,
            NodeLinkTreeVisualization vis);

    public Rectangle2D.Float findRectAt(int row) {
        return shapeColumn.findRect(row);
    }

    public Rectangle2D.Float getRectAt(int row) {
        return shapeColumn.getRect(row);
    }

    public void setShapeAt(int row, Shape s) {
        shapeColumn.setExtend(row, s);
    }

    public RowIterator iterator() {
        return visualization.iterator();
    }

    public RowIterator childrenIterator(int node) {
        return visualization.childrenIterator(node);
    }

    public boolean isLeaf(int node) {
        return visualization.isLeaf(node);
    }

    public int getFirstChild(int node) {
        return visualization.getFirstChild(node);
    }

    public int getParent(int node) {
        return visualization.getParent(node);
    }

    public short getOrientation() {
        return visualization.getOrientation();
    }

    public void setOrientation(short orientation) {
        visualization.setOrientation(orientation);
    }

    protected void computeSizes() {
        try {
            shapeColumn.disableNotify();
            for (RowIterator iter = iterator(); iter.hasNext();) {
                int v = iter.nextRow();

                Rectangle2D.Float rect = findRectAt(v);
                setRectSizeAt(v, rect);
                setShapeAt(v, rect);
            }
        } finally {
            shapeColumn.enableNotify();
        }
    }

    public void setRectSizeAt(int row, Rectangle2D.Float rect) {
        vs.setRectSizeAt(row, rect);
    }
}
