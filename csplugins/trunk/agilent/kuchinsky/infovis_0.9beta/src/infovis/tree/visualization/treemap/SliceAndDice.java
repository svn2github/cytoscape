/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.visualization.treemap;

import infovis.Visualization;
import infovis.tree.visualization.TreemapVisualization;
import infovis.utils.RectPool;
import infovis.utils.RowIterator;
import infovis.visualization.Orientable;
import infovis.visualization.Orientation;

import java.awt.geom.Rectangle2D;

/**
 * Slice and Dice Algorithm.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 * 
 * @infovis.factory TreemapFactory Slice&Dice
 */
public class SliceAndDice extends Treemap implements Orientable{
    /** Instance. */
    public static final SliceAndDice instance = new SliceAndDice();
    protected short orientation;

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Slice & Dice";
    }
    
    protected void setVisualization(Visualization vis) {
        super.setVisualization(vis);
        orientation = 
            Orientation.isVertical(vis.getOrientation()) 
                ? ORIENTATION_EAST
                : ORIENTATION_NORTH;
    }

    /**
     * {@inheritDoc}
     */
    public void computeShapes(
            Rectangle2D bounds,
            TreemapVisualization vis) {
        start();
        visit(orientation, (float) bounds.getX(), (float) bounds
                .getY(), (float) bounds.getMaxX(), (float) bounds
                .getMaxY(), vis.getVisibleRoot());
        finish();
    }

    protected short flip(short orientation) {
        return orientation == ORIENTATION_EAST ? ORIENTATION_NORTH : ORIENTATION_EAST;
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short o) {
        this.orientation = o;
    }

    protected int visit(
            short direction,
            float xmin,
            float ymin,
            float xmax,
            float ymax,
            int node) {
        Rectangle2D.Float box = RectPool.allocateRect();
        box.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
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
            float tw = sizeColumn.getFloatAt(node);
            if (direction == ORIENTATION_EAST) {
                float w = box.width;
                float x = box.x;
                for (RowIterator it = visualization.childrenIterator(node); it
                        .hasNext();) {
                    int child = it.nextRow();
                    float nw = w * sizeColumn.getFloatAt(child) / tw;
                    ret += visit(flip(direction), x, box.y, x + nw,
                            box.y + box.height, child);
                    x += nw;
                }
            } else {
                float h = box.height;
                float y = box.y;
                for (RowIterator it = visualization.childrenIterator(node); it
                        .hasNext();) {
                    int child = it.nextRow();
                    float nh = h * sizeColumn.getFloatAt(child) / tw;
                    ret += visit(flip(direction), box.x, y, box.x
                            + box.width, y + nh, child);
                    y += nh;
                }
            }
            RectPool.freeRect(box);
        }
        return ret;
    }
}