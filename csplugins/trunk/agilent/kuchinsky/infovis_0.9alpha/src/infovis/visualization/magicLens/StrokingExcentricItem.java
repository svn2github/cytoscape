/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Visualization;
import infovis.utils.StrokedPath;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class StrokingExcentricItem
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class StrokingExcentricItem extends ExcentricItem {
    public StrokingExcentricItem(
        Visualization visualization,
        int index) {
        super(visualization, index);
    }
    
    public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut) {
        Shape s = visualization.getShapeAt(index);
        if (s == null) {
            return null;
        }
        return StrokedPath.pointOnPathIn(s, focus, ptOut);
    }    

}
