/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.utils.StrokedPath;
import infovis.visualization.ItemRenderer;
import infovis.visualization.Orientation;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualStrokingLabel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class VisualStrokingLabel extends VisualLabel {
    public static final double POS_START = 0;
    public static final double POS_END = 1;
    public static final double POS_MIDDLE = 0.5;
    protected double position = POS_MIDDLE;
    
    public VisualStrokingLabel(ItemRenderer child, double pos) {
        this(child);
        position = pos;
    }
    
    public VisualStrokingLabel(ItemRenderer child, boolean showingLabel) {
        super(child, showingLabel);
    }

    public VisualStrokingLabel(ItemRenderer child) {
        this(child, false);
    }
    
    public double getPosition() {
        return position;
    }
    
    public void setPosition(double position) {
        this.position = position;
        invalidate();
    }
    
    public void paint(Graphics2D graphics, int row, Shape s) {
        if (!showingLabel) {
            return;
        }
        String label = getLabelAt(row);
        if (label == null) {
            return;
        }
        
        Point2D pos = StrokedPath.pointAt(s, position, null);
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);
        if (Orientation.isVertical(getOrientation())) {
            graphics = (Graphics2D)graphics.create();
            graphics.rotate(
                    Math.PI / 2, 
                    (float)(pos.getX()),
                    (float)(pos.getY()));
        }
        graphics.setColor(Color.BLACK);
        graphics.drawString(
                label, 
                (float)(pos.getX()-labelBounds.getWidth()*position),
                (float)(pos.getY()+fm.getAscent()-fm.getHeight()/2));
    }

}
