/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.visualization.treemap;

import infovis.tree.visualization.TreemapVisualization;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Squarified Treemap Algorithm.
 * 
 * @version $Revision: 1.24 $
 * @author Jean-Daniel Fekete
 * 
 * @infovis.factory TreemapFactory Squarified
 */
public class Squarified extends Treemap {
    /** Instance. */
    public static final Squarified instance = new Squarified();

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Squarified";
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(
            Rectangle2D bounds,
            TreemapVisualization vis) {
        start();
        visit((float)bounds.getX(), (float)bounds.getY(), 
              (float)bounds.getMaxX(), (float)bounds.getMaxY(),
              visualization.getVisibleRoot());
        finish();
    }

    protected int visit(float xmin, float ymin, float xmax,
            float ymax, int node) {
        Rectangle2D.Float box = RectPool.allocateRect();
        box.setRect(xmin, ymin, (xmax - xmin), (ymax - ymin));
        if (!beginBox(box)) {
            shapeColumn.setExtend(node, box);
            return 0;
        }

        int ret = 1;

        if (visualization.isLeaf(node)) {
            shapeColumn.setExtend(node, box);
            box = null;
        }
        else {
            shapeColumn.setExtend(node, borderShape(xmin, ymin, xmax, ymax, node));
            removeBorder(box, node);
            ret = visitStrips(
                    box.x,
                    box.y,
                    box.x + box.width,
                    box.y + box.height, 
                    node);
            RectPool.freeRect(box);
        }
        return ret;
    }

    protected boolean isVertical(float xmin, float ymin, float xmax,
            float ymax, int node) {
        return (xmax - xmin) > (ymax - ymin);
    }

    /**
     * Computes the rectangles that fill the containing rectangle allocated to
     * the specified node.
     *  
     */
    protected int visitStrips(float xmin, float ymin, float xmax,
            float ymax, int node) {
        double tw = sizeColumn.getDoubleAt(node);
        int ret = 1;

        // The sizeColumn of the current node -- sum of weights of children
        // nodes --
        // is tw. It will fill a surface of width*height so the scale is
        // surface / tw.
        double scale = ((xmax - xmin) * (ymax - ymin)) / tw;

        if (scale == 0) {
            return 0;
        }

        // Split in strips
        for (RowIterator it = visualization.childrenIterator(node); it.hasNext();) {
            if (isVertical(xmin, ymin, xmax, ymax, node)) {
                // Vertical strip case, height is fixed, compute the heights of
                // strips
                double h = ymax - ymin;
                double y = ymin; // vertical position of this stip
                RowIterator it2 = it.copy();
                // Compute the end of the strip, leaving the first of the next
                // strip
                // in the iterator.
                // invariant: squarify always advances the iterator or return 0.
                // returns the strip width, given its height.
                double width = squarify(it, h, scale);

                if (width == 0) {
                    return ret;
                }

                while (it2.peekRow() != it.peekRow()) {
                    int i = it2.nextRow();
                    // compute the node height.
                    double nh = (sizeColumn.getDoubleAt(i) * scale)
                            / width;

                    ret += visit(
                            xmin, 
                            (float)y, 
                            xmin + (float)width,
                            (float)(y + nh),
                            i);
                    y += nh;
                }

                xmin += width;
            } else {
                double w = xmax - xmin;
                double x = xmin;
                RowIterator it2 = it.copy();

                double height = squarify(it, w, scale);

                if (height == 0) {
                    return ret;
                }

                while (it2.peekRow() != it.peekRow()) {
                    int i = it2.nextRow();
                    double nw = (sizeColumn.getDoubleAt(i) * scale)
                            / height;

                    ret += visit(
                            (float)x,
                            ymin, 
                            (float)(x + nw),
                            ymin + (float)height, 
                            i);
                    x += nw;
                }

                ymin += height;
            }
        }

        //assert(Math.abs(xmin-xmax)<1 || Math.abs(ymin-ymax)<1);

        return ret;
    }

    protected double squarify(
            RowIterator it,
            double length,
            double scale) {
        double s = 0;

        // First, find an initial non-empty rectangle to start with
        while (it.hasNext() && (s == 0)) {
            s = sizeColumn.getDoubleAt(it.nextRow()) * scale;
        }

        // We have a first tentative width now
        double width = s / length;

        // We could have reached the end (the width might be zero then)
        if (!it.hasNext()) {
            return width;
        }

        // Prepare to iterate until the the worst aspect ratio stops to improve.
        double s2 = s * s;
        double min_area = s;
        double max_area = s;
        double worst = Math.max(length / width, width / length);
        double w2 = length * length;

        while (it.hasNext()) {
            // See if adding the next rectangle will improve the worst aspect
            // ratio
            double area = sizeColumn.getDoubleAt(it.peekRow()) * scale;

            // Skip empty rectangles.
            if (area == 0) {
                it.nextRow();

                continue;
            }

            s += area;
            s2 = s * s;

            double cur_min_area = (area < min_area) ? area : min_area;
            double cur_max_area = (area > max_area) ? area : max_area;

            double cur_worst = Math.max((w2 * cur_max_area) / s2, s2
                    / (w2 * cur_min_area));

            if (cur_worst > worst) {
                // If result is worst, revert to previous area and return
                s -= area;
                break;
            }
            min_area = cur_min_area;
            max_area = cur_max_area;
            worst = cur_worst;
            it.nextRow();
        }

        return s / length;
    }

}