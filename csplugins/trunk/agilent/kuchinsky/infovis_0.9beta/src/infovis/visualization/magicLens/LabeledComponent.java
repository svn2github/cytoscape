/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import javax.swing.JComponent;

/**
 * Interface LabeledComponent declares all the methods required by
 * ExcentricLabels. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public interface LabeledComponent {
    /**
     * Returns a list of LabeledItems under a specified Rectangle.
     * 
     * @param hitBox the rectangle that the items should intersect
     * @param bounds the bounds of the visualization
     * @param pick an Set to use or null if a new one has be be allocated.
     * 
     * @return the Set of LabeledItems under the specified Rectangle.
     */
    public Set pickAll(
        Rectangle2D hitBox,
        Rectangle2D bounds,
        Set pick);

    /**
     * Returns the JComponent managing this LabeledCompent
     * @return  the JComponent managing this LabeledCompent.
     */
    public JComponent getComponent();

    public interface LabeledItem {
        public Component getComponent();
        public String getLabel();
        public Shape getShape();
        public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut);
        public Color getColor();
    }
}
