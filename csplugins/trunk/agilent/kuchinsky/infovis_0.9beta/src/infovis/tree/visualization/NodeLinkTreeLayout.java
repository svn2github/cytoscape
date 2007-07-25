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
 * Layout class for NodeLinkTreeVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public abstract class NodeLinkTreeLayout implements Layout, Orientable {
    protected static final Logger                 logger            = Logger
                                                                            .getLogger(NodeLinkTreeLayout.class);
    protected transient NodeLinkTreeVisualization visualization;
    protected transient NumberColumn              sizeColumn;
    protected transient ShapeColumn               shapeColumn;
    protected transient VisualSize                vs;
    protected transient Rectangle2D               bounds;

    protected float                               siblingSeparation = 3;
    protected float                               subtreeSeparation = 10;
    protected float                               levelSeparation;

    protected NodeLinkTreeLayout() {
    }

    /**
     * Sets the visualization before a layout operation.
     * 
     * @param vis
     *            the visualization.
     */
    protected void setVisualization(Visualization vis) {
        // if (visualization != null) {
        // throw new RuntimeException(
        // "Reentering NodeLinkTreeLayout.computeShapes");
        // }
        if (visualization == null) {
            visualization = (NodeLinkTreeVisualization) vis
                    .findVisualization(NodeLinkTreeVisualization.class);
        }
        else {
            assert (visualization == vis
                    .findVisualization(NodeLinkTreeVisualization.class));
        }
        sizeColumn = VisualSize.get(visualization).getSizeColumn();
        shapeColumn = visualization.getShapes();
        vs = VisualSize.get(vis);
        shapeColumn.disableNotify();
    }

    /**
     * Specify the layout is finished.
     */
    protected void unsetVisualization() {
        shapeColumn.enableNotify();
        sizeColumn = null;
        shapeColumn = null;
        // visualization = null;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
        this.bounds = null;
    }

    protected void invalidateVisualization() {
        if (visualization != null)
            visualization.invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        this.bounds = bounds;
        try {
            setVisualization(vis);
            computeShapes(bounds, visualization);
        } finally {
            unsetVisualization();
        }
    }

    /**
     * Performs the actual computation of the layout.
     * 
     * @param bounds
     *            the visualization bounds
     * @param vis
     *            the visualization
     */
    public abstract void computeShapes(
            Rectangle2D bounds,
            NodeLinkTreeVisualization vis);

    /**
     * @see infovis.column.ShapeColumn#findRect(int)
     */
    public Rectangle2D.Float findRectAt(int row) {
        return shapeColumn.findRect(row);
    }

    /**
     * @see infovis.column.ShapeColumn#getRect(int)
     */
    public Rectangle2D.Float getRectAt(int row) {
        return shapeColumn.getRect(row);
    }

    /**
     * Sets the shape in the shape column.
     * 
     * @param row
     *            the row
     * @param s
     *            the shape
     */
    public void setShapeAt(int row, Shape s) {
        shapeColumn.setExtend(row, s);
    }

    /**
     * @see infovis.Visualization#iterator()
     */
    public RowIterator iterator() {
        return visualization.iterator();
    }

    /**
     * @see infovis.Tree#childrenIterator(int)
     */
    public RowIterator childrenIterator(int node) {
        return visualization.childrenIterator(node);
    }

    /**
     * @see infovis.Tree#isLeaf(int)
     */
    public boolean isLeaf(int node) {
        return visualization.isLeaf(node);
    }

    /**
     * @see infovis.tree.visualization.TreeVisualization#getFirstChild(int)
     */
    public int getFirstChild(int node) {
        return visualization.getFirstChild(node);
    }

    /**
     * @see infovis.Tree#getParent(int)
     */
    public int getParent(int node) {
        return visualization.getParent(node);
    }

    /**
     * @see infovis.Visualization#getOrientation()
     */
    public short getOrientation() {
        return visualization.getOrientation();
    }

    /**
     * @see infovis.Visualization#setOrientation(short)
     */
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

    /**
     * Set the rectangle associated with a specified row.
     * 
     * @param row
     *            the row
     * @param rect
     *            the rectangle
     */
    public void setRectSizeAt(int row, Rectangle2D.Float rect) {
        vs.setRectSizeAt(row, rect);
    }

    /**
     * @return Returns the levelSeparation.
     */
    public float getLevelSeparation() {
        return levelSeparation;
    }

    /**
     * @param levelSeparation The levelSeparation to set.
     */
    public void setLevelSeparation(float levelSeparation) {
        this.levelSeparation = levelSeparation;
        invalidateVisualization();
    }

    /**
     * @return Returns the siblingSeparation.
     */
    public float getSiblingSeparation() {
        return siblingSeparation;
    }

    /**
     * @param siblingSeparation The siblingSeparation to set.
     */
    public void setSiblingSeparation(float siblingSeparation) {
        this.siblingSeparation = siblingSeparation;
        invalidateVisualization();
    }

    /**
     * @return Returns the subtreeSeparation.
     */
    public float getSubtreeSeparation() {
        return subtreeSeparation;
    }

    /**
     * @param subtreeSeparation The subtreeSeparation to set.
     */
    public void setSubtreeSeparation(float subtreeSeparation) {
        this.subtreeSeparation = subtreeSeparation;
        invalidateVisualization();
    }

}
