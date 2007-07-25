/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;


import infovis.visualization.MagicLens;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface ExcentricLabels extends MagicLens {
    public void setVisualization(LabeledComponent labeledComponent);
    public void paint(Graphics2D graphics, Rectangle2D bounds);
    public boolean isVisible();
    public void setVisible(boolean set);
    public int getMaxLabels();
    public void setMaxLabels(int maxLabels);
    public boolean isOpaque();
    public void setOpaque(boolean set);
}
