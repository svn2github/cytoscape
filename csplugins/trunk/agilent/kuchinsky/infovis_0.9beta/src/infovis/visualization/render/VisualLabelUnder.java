/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualLabelUnder
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class VisualLabelUnder extends DefaultVisualLabel {

    public VisualLabelUnder(ItemRenderer child, boolean showingLabel) {
        super(child, showingLabel);
    }
    
    public VisualLabelUnder(ItemRenderer child) {
        this(child, true);
    }

    public void paint(Graphics2D graphics, int row, Shape s) {
        if (!showingLabel) {
            return;
        }
        String label = getLabelAt(row);
        if (label == null) {
            return;
        }
        Rectangle2D bounds = s.getBounds2D();
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);

        Color savedColor = contrastColor(graphics);

        graphics.drawString(label,
                (float) (bounds.getCenterX() - labelBounds.getWidth()/2),
                (float) (bounds.getMaxY() + labelBounds.getHeight()));
        graphics.setColor(savedColor);
    }
    
}
