/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;


import infovis.Visualization;
import infovis.column.ShapeColumn;

import java.awt.Shape;

/*
 * 
 * interface LinkShaper
 *
 * Computes the shape of a link in a LinkVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */

public interface LinkShaper {
    public void init(Visualization vis, ShapeColumn shapes);
    public Shape computeLinkShape(int link, NodeAccessor accessor, Shape prevLinkShape);
    public String getName();
}