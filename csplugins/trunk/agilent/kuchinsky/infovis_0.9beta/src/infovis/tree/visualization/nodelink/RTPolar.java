/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.nodelink;

import infovis.utils.RectPool;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Use the Reingold and Tilford Tree Layout and transforms it in
 * polar coordinates.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory TreeLayoutFactory "Reingold&Tilford Polar"
 */
public class RTPolar extends RTLayout {
    /**
     * Constructor.
     */
    public RTPolar() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return super.getName()+" Polar";
    }

    /**
     * Recompute the tree scale so that it fits inside
     * its specified bounds.
     * Transform the tree into a radial layout while
     * resizing it.
     */
    protected void centerTree() {
        Rectangle2D.Float rootRect = getRectAt(visualization.getVisibleRoot());
        assert(rootRect != null);
        float cx = rootRect.x;
        float cy = rootRect.y;

        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            Rectangle2D.Float rect = getRectAt(row);
            if (rect == null) {
                continue;
            }
            
            double r = (rect.y - cy);
            double t = (rect.x - cx) * 2 * Math.PI / bbox.width;

            rect.x = (float)(r * Math.cos(t) + bbox.height);
            rect.y = (float)(r * Math.sin(t) + bbox.height);
            setShapeAt(row, rect);
        }
        RectPool.freeRect(bbox);
        bbox = shapeColumn.getBounds();
        super.centerTree();
    }
}
