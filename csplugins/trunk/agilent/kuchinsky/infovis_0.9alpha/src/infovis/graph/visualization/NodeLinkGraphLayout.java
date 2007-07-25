/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import java.awt.geom.Rectangle2D;

import infovis.visualization.Layout;

/**
 * Class NodeLinkGraphLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * TODO Add documentation
 */
public interface NodeLinkGraphLayout extends Layout {
    public abstract void incrementLayout(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis);
    public abstract boolean isFinished();
}