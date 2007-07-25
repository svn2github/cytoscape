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

import cern.colt.map.OpenIntObjectHashMap;

/**
 * Visual size for stroking visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class VisualStrokeSize extends VisualSize {
//    public static final String VISUAL = "stroke";
    public static double defaultMinSize = 1;
    public static double defaultMaxSize = 10;
    public static double defaultDefaultSize = 1;
    protected static final OpenIntObjectHashMap STROKES = new OpenIntObjectHashMap();
    
    static {
        for (int i = 0; i < 50; i++) {
            STROKES.put(i, new BasicStroke(i));
        }
    }
    
    protected transient BasicStroke stroke;
    protected transient Rectangle2D.Float pick;
    
    public VisualStrokeSize(ItemRenderer child) {
        this(child, defaultDefaultSize, defaultMinSize, defaultMaxSize);
    }
    
    public VisualStrokeSize(ItemRenderer child, double def, double min, double max) {
        super(VISUAL);
        defaultSize = def;
        minSize = min;
        maxSize = max;
        addRenderer(child);
    }
    public void install(Graphics2D graphics) {
        if (sizeColumn != null) {            
            stroke = null;
        }
        else {
            stroke = new BasicStroke((float)defaultSize);
        }
        super.install(graphics);
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        float w;
        if (stroke != null) {
            w = stroke.getLineWidth();
        }
        else {
            w = (float)getSizeAt(row);
        }
        if (pick == null) {
            pick = new Rectangle2D.Float();
        }
        if (w != 0) {
            pick.setRect(hitBox);
            pick.x -= w/2;
            pick.y -= w/2;
            pick.width += w;
            pick.height += w;
            hitBox = pick;
        }
        return super.pick(hitBox, row, shape);
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        java.awt.Stroke saved = graphics.getStroke();
        try {
            if (stroke != null) { 
                graphics.setStroke(stroke);
            }
            else {
                int s = (int)getSizeAt(row);
                if (s < 0) return;
                BasicStroke stroke = (BasicStroke)STROKES.get(s);
                if (stroke == null) {
                    stroke = new BasicStroke(s);
                    STROKES.put(s, stroke);
                }
                graphics.setStroke(stroke);
            }
            super.paint(graphics, row, shape);
        }
        finally {
            graphics.setStroke(saved);
        }
    }
}
