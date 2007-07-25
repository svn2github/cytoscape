/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Class NoVisual
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class NoVisual extends AbstractItemRenderer {

    public NoVisual(ItemRenderer child) {
        super(null);
        addRenderer(child);
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        // do nothing
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        return false;
    }
    
    public ItemRenderer compile() {
        return null;
    }

}
