/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Class Fill
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class Fill extends StaticItemRenderer {
    public static final Fill instance = new Fill();
   
    protected Fill() {
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
        Rectangle2D bounds = shape.getBounds2D();
        if (! bounds.isEmpty()) {
            graphics.fill(shape);
        }
        
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        return shape.intersects(hitBox);
    }
}
