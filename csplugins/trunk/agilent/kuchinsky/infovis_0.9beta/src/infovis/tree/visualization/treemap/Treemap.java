/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.column.ShapeColumn;
import infovis.tree.visualization.TreemapVisualization;
import infovis.visualization.Layout;

import java.awt.Dimension;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

/**
 * Base class for all Treemap algorithms.
 * 
 * @version $Revision: 1.23 $
 * @author Jean-Daniel Fekete
 */
public abstract class Treemap implements Layout {
    protected static final Logger            logger = Logger
                                                            .getLogger(Treemap.class);
    protected transient TreemapVisualization visualization;
    protected transient NumberColumn         sizeColumn;
    protected transient ShapeColumn          shapeColumn;
    protected float                          left   = 3;
    protected float                          right  = 3;
    protected float                          top    = 20;
    protected float                          bottom = 3;

    /**
     * Method start should be called before starting to draw shapes.
     */
    public void start() {
        shapeColumn.clear();
    }

    /**
     * Method start should be called before starting to draw shapes.
     */
    public void finish() {
    }

    /**
     * Checks whether the specified box should be drawn and initialize the
     * node's Shape to its final bounds.
     * 
     * @param box
     *            the bounding box
     * @return true if the treemap algorithm should continue to go deeper, false
     *         otherwise.
     */
    public boolean beginBox(Rectangle2D.Float box) {
        // No need to draw if less than one pixel appears
        return box.width >= (left + right + 1)
                && box.height >= (top + bottom + 1);
    }

    /**
     * Method removeBorder only removes the border from the given box.
     * 
     * @param box
     *            the rectangle containing the box whose border should be drawn.
     *            It is modified by the method and will hold the remaining part
     *            of the box after the border is drawn.
     * @param node
     *            the tree node to draw.
     */
    public void removeBorder(Rectangle2D.Float box, int node) {
        box.x += left;
        box.y += top;
        box.width -= left + right;
        box.height -= top + bottom;
    }

    /**
     * Computes the shape of the border.
     * 
     * @param xmin
     *            left coord
     * @param ymin
     *            upper coord
     * @param xmax
     *            right coord
     * @param ymax
     *            lower coord
     * @param node
     *            the node
     * @return a shape for the border
     */
    public GeneralPath borderShape(
            float xmin,
            float ymin,
            float xmax,
            float ymax,
            int node) {

        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        addRect(p, xmin, ymin, xmax, ymax);
        addRect(p, xmin + left, ymin + top, xmax - right, ymax - bottom);

        return p;
    }

    protected void addRect(
            GeneralPath p,
            float xmin,
            float ymin,
            float xmax,
            float ymax) {
        p.moveTo(xmin, ymin);
        p.lineTo(xmax, ymin);
        p.lineTo(xmax, ymax);
        p.lineTo(xmin, ymax);
        p.closePath();
    }

    protected void setVisualization(Visualization vis) {
        // if (visualization != null) {
        // logger.error("Reentering Treemap.computeShapes");
        // throw new RuntimeException(
        // "Reentering Treemap.computeShapes");
        // }
        if (visualization == null) {
            visualization = (TreemapVisualization) vis
                    .findVisualization(TreemapVisualization.class);
        }
        else {
            assert (visualization == vis
                    .findVisualization(TreemapVisualization.class));
        }
        if (visualization == null) {
            logger.error("No TreemapVisualization found in Visualization");
            throw new RuntimeException(
                    "No TreemapVisualization found in Visualization");
        }
        sizeColumn = visualization.getWeightColumn();
        shapeColumn = visualization.getShapes();
    }

    protected void unsetVisualization() {
        sizeColumn = null;
        shapeColumn = null;
        // visualization = null;
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        try {
            setVisualization(vis);
            computeShapes(bounds, visualization);
        } finally {
            unsetVisualization();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        try {
            setVisualization(vis);
            if (sizeColumn.getMinIndex()==-1) return null;
            double min = sizeColumn.getDoubleMin();
            double max = sizeColumn.getDoubleMax();
            if (min != 0) {
                // Assume we have a square ratio for items
                // and reserve 3 pixels for the smalest item edge
                double side = Math.sqrt(max / min) * 3;
                if (side > 100 && side < 6000) {
                    return new Dimension((int) side, (int) side);
                }
                else if (side >= 6000) {
                    return new Dimension(6000, 6000);
                }
            }
            return null;
        } finally {
            unsetVisualization();
        }
    }

    /**
     * Computes the shapes of the treemap.
     * 
     * @param vis
     *            the TreemapVisualization
     * @param bounds
     *            the external bounds of the treemap.
     * 
     */
    public abstract void computeShapes(
            Rectangle2D bounds,
            TreemapVisualization vis);
}