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


/**
 * Class VisualClipBounds
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class VisualClipBounds extends AbstractItemRenderer {
    protected int clippedCount;
    
    /** The bounds of the clip when drawing. Can always be null */
    protected transient Rectangle clipBounds;
    protected boolean onStroke = false;
    
    public VisualClipBounds(ItemRenderer child, boolean onStroke) {
        super(null);
        this.onStroke = onStroke;
        addRenderer(child);
    }
    
    public VisualClipBounds(ItemRenderer child) {
        this(child, false);
    }
    
    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (graphics != null) {
            clipBounds = graphics.getClipBounds();
        }
        clippedCount = 0;
    }
    
    public boolean isVisible(Graphics2D graphics, int row, Shape shape) {
        if (clipBounds == null) return true;
        if (onStroke) {
            Object s = graphics.getStroke();
            float w = 0;
            if (s instanceof BasicStroke) {
                BasicStroke stroke = (BasicStroke) s;
                w = stroke.getLineWidth();
            }
            return shape.intersects(clipBounds.x-w/2, clipBounds.y-w/2, clipBounds.width+w, clipBounds.height+w);
        }
        return shape.intersects(clipBounds);
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (isVisible(graphics, row, shape)) {
            super.paint(graphics, row, shape);
        }
        else {
            clippedCount++;
            if (onStroke) {
                clippedCount++;
            }
        }
    }
    
    public void uninstall(Graphics2D graphics) {
        clipBounds = null;
    }
}
