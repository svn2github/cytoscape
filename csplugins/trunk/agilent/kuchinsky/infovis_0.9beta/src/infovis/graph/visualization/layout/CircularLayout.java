/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.RowIterator;

import java.awt.geom.Rectangle2D;

/**
 * Class CircularLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory GraphLayoutFactory Circular
 */
public class CircularLayout extends AbstractGraphLayout {
    public CircularLayout() {
    }
    
    public String getName() {
        return "Circular";
    }
    
    public void computeShapes(Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        super.computeShapes(bounds, vis);
        double offset = vs.getMaxSize()/2;
        double radius = 
            Math.min(bounds.getWidth(), bounds.getHeight()) / 2 - offset;
        double angle = 2*Math.PI / getVerticesCount();
        int i = 0;
        for (RowIterator iter = vertexIterator(); iter.hasNext(); ) {
            int v = iter.nextRow();
            double x = Math.sin(i*angle) * radius + radius;
            double y = Math.cos(i*angle) * radius + radius;
            Rectangle2D.Float rect = getRectAt(v);
            rect.x = (float)x;
            rect.y = (float)y;
            setShapeAt(v, rect);
            i++;
        }
    }
}
