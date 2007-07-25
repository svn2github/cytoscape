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
 * Class RTPolar
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory TreeLayoutFactory "Reingold&Tilford Polar"
 */
public class RTPolar extends RTLayout {
    public RTPolar() {
        super();
    }
    
    public String getName() {
        return super.getName()+" Polar";
    }

    /**
     * Recompute the tree scale so that it fits inside
     * its specified bounds.
     * Transform the tree into a radial layout while
     * resizing it.
     */
    protected void rescaleTree() {
        Rectangle2D.Float newBBox = RectPool.allocateRect();
        newBBox.setRect(0, 0, 0, 0);
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
            if (newBBox.isEmpty()) {
                newBBox.setRect(rect);
            }
            else {
                newBBox.add(rect);
            }
        }
        RectPool.freeRect(bbox);
        bbox = newBBox;
        super.rescaleTree();
    }
}
