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
 * Interface for layouts.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public interface Layout {
    /**
     * Returns the name of the layout algorithm.
     * @return the name of the layout algorithm
     */
    String getName();
    
    /**
     * Computes and return the preferred size
     * for this layout and this visualization.
     * @param vis the visualization
     * @return the preferred size
     * for this layout and this visualization.
     */
    Dimension getPreferredSize(
            Visualization vis);
    /**
     * Invalidate the cached state of the
     * layout.
     * 
     * <p>Call this method from the visualization
     * when it gets invalidated.
     * @param vis the visualization.
     */
    void invalidate(
            Visualization vis);
    /**
     * Computes the layout.
     * @param bounds the allocated bounds of the
     * visualization.
     * @param vis the visualization.
     */
    void computeShapes(
            Rectangle2D bounds, 
            Visualization vis);
}
