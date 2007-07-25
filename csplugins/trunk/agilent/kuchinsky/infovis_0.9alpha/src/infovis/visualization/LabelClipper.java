/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Class LabelClipper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public interface LabelClipper {
    /**
     * Clip a specified label string according to the width and height of
     * its allocated bounds.
     * 
     * @param label the label
     * @param graphics the graphics
     * @param labelBounds the original bounds of the label.
     * WARNING: On exit, it should be set to the new computed bounds.
     * @param width the bounds width
     * @param height the bounds height
     * @return the string clipped.
     */
    public abstract String clip(
            String label,
            Graphics2D graphics,
            Rectangle2D labelBounds,
            double width, double height);
}
