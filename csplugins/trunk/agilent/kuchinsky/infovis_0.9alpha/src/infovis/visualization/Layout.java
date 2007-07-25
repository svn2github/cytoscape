/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * Class Layout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public interface Layout {
    public abstract String getName();
    public abstract Dimension getPreferredSize(
            Visualization vis);
    public abstract void setVisualization(
            Visualization vis);
    public abstract void unsetVisualization();
    public abstract void invalidate();
    public abstract void computeShapes(
            Rectangle2D bounds, 
            Visualization vis);
}
