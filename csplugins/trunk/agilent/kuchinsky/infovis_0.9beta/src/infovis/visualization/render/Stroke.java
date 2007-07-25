/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.utils.StrokedPath;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Class Stroke
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class Stroke extends StaticItemRenderer {
    public static final Stroke instance = new Stroke();
    
    private Stroke() {
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        graphics.draw(shape);
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape s) {
        if (s == null) {
            return false;
        }
        Rectangle2D rect = s.getBounds2D();
        if (rect.isEmpty()
            && hitBox.contains(rect.getX(), rect.getY()))
            return true;
        return StrokedPath.intersects(s, hitBox);
    }
}
