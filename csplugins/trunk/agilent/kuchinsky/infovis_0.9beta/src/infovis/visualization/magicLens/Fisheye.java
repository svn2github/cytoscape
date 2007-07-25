/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.visualization.MagicLens;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Class Fisheye
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface Fisheye extends MagicLens {
    public static final String PROPERTY_FOCUS_HEIGHT = "focusHeight";
    /**
     * Transforms the specified shape, returning its deformation
     * through the Fisheye.
     *
     * @param s the initial shape
     *
     * @return the transformed shape
     */
    public abstract Shape transform(Shape s);
    
    public abstract Point2D transform(Point2D pt);
    
    public abstract void transform(Point2D src, Point2D dst);
    
    public abstract void transform(float[] coords, int npoints);

    /**
     * Returns the height of the focus area.
     * 
     * @return the height of the focus area
     */
    public abstract float getFocusHeight();
    
    /**
     * Sets the height of the focus area.
     * 
     * @param h the height of the focus area.
     */
    public abstract void setFocusHeight(float h);
}