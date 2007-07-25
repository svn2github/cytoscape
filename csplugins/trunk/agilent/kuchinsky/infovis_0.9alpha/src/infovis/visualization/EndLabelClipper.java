/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Class EndLabelClipper
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class EndLabelClipper implements LabelClipper {
    private static EndLabelClipper instance;

    public static EndLabelClipper getInstance() {
        if (instance == null) {
            instance = new EndLabelClipper();            
        }
        return instance;
    }
    
    public String clip(String label, Graphics2D graphics,
            Rectangle2D labelBounds, double width, double height) {
        FontMetrics fm = graphics.getFontMetrics();
        int l = label.length();
        for (int i = 0; i < l; i++) {
            Rectangle2D bounds = fm.getStringBounds(label, i, l, graphics); 
            if (bounds.getWidth() <= width) {
                labelBounds.setRect(bounds);
                return label.substring(i);
            }
        }
        return null;
    }
}
