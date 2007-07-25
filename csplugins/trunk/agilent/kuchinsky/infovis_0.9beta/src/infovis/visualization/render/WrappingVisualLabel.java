/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.utils.RectPool;
import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

/**
 * Class WrappingVisualLabel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class WrappingVisualLabel extends DefaultVisualLabel {
    public WrappingVisualLabel() {
    }

    public WrappingVisualLabel(boolean showingLabel, Color defaultColor) {
        super(showingLabel, defaultColor);
    }

    public WrappingVisualLabel(boolean showingLabel) {
        super(showingLabel);
    }

    public WrappingVisualLabel(Color defaultColor) {
        super(defaultColor);
    }

    public WrappingVisualLabel(ItemRenderer child, boolean showingLabel, Color defaultColor) {
        super(child, showingLabel, defaultColor);
    }

    public WrappingVisualLabel(ItemRenderer child, boolean showingLabel) {
        super(child, showingLabel);
    }

    public static ArrayList computeLines(
            Graphics2D g,
            String text,
            float width) {
        if (text == null || text.length() == 0) return null;
        
        FontRenderContext frc = g.getFontRenderContext();
        Font font = g.getFont();
        ArrayList lines = new ArrayList();
        AttributedString aString = new AttributedString(text);
        aString.addAttribute(TextAttribute.FONT, font);
        AttributedCharacterIterator iter = aString.getIterator();
        
        LineBreakMeasurer measurer = new LineBreakMeasurer(iter, frc);
        
        int nextBreak = text.indexOf('\n');
        if (nextBreak == -1) {
            nextBreak = Integer.MAX_VALUE;
        }
        
        while (measurer.getPosition() < iter.getEndIndex()) {
            if (nextBreak < measurer.getPosition()) {
                nextBreak = text.indexOf('\n', measurer.getPosition());
                if (nextBreak == -1) {
                    nextBreak = Integer.MAX_VALUE;
                }
            }
            
            if (nextBreak == measurer.getPosition()) {
                nextBreak++;
            }
            TextLayout layout = measurer.nextLayout(width, nextBreak, false);
            if (measurer.getPosition() == nextBreak) {
                measurer.setPosition(nextBreak+1);
            }
            lines.add(layout);
        }
        
        return lines;
    }
    
    public static Rectangle2D.Float computeLinesBounds(ArrayList lines) {
        if (lines == null) {
            return null;
        }
        Rectangle.Float rect = RectPool.allocateRect();
        rect.width = 0;
        rect.height = 0;
        for (int line = 0; line < lines.size(); line++) {
            TextLayout layout = (TextLayout)lines.get(line);
            rect.width = Math.max(layout.getAdvance(), rect.width);
            rect.height += 
                layout.getAscent() 
                + layout.getDescent() 
                + layout.getLeading();
        }
        
        return rect;
    }

    public void paint(Graphics2D graphics, int row, Shape s) {
        if (! showingLabel) {
            return;
        }
        String label = getLabelAt(row);
        if (label == null || label.length() == 0) {
            return;
        }
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D bounds = s.getBounds2D();

        Rectangle2D maxCharBounds = fm.getMaxCharBounds(graphics);
        if (maxCharBounds.getWidth() > bounds.getWidth() * 2
            || maxCharBounds.getHeight() > (bounds.getHeight() * 2)) {
            return; // no reason to try
        }

        Color savedColor = contrastColor(graphics);
        int w = fm.stringWidth(label);                
        if (label.indexOf('\n') == -1
                || w <= bounds.getWidth()
                || w <= bounds.getHeight()) {
            super.paint(graphics, row, s);
            return;
        }
        ArrayList lines = computeLines(
                graphics,
                label,
                (float)bounds.getWidth());
        Rectangle2D.Float b = computeLinesBounds(lines);
        if (bounds.getHeight() <= b.height) {
            paintLines(graphics, lines, b, bounds);
        }
        else {
            Shape saved = graphics.getClip(); 
            try {
                graphics.clip(s);
                paintLines(graphics, lines, b, bounds);
            }
            finally {
                graphics.setClip(saved);
            }
        }
        graphics.setColor(savedColor);
    }
    
    public void paintLines(
            Graphics2D graphics,
            ArrayList lines,
            Rectangle2D.Float b,
            Rectangle2D bounds) {
        float x = (float)(bounds.getX() + (bounds.getWidth() - b.width)/2);
        float y = (float)(bounds.getY() + (bounds.getHeight() - b.height)/2);
        for (int line = 0; line < lines.size(); line++) {
            TextLayout layout = (TextLayout)lines.get(line);
            y += layout.getAscent();
            layout.draw(graphics, x, y);
            y += layout.getDescent() + layout.getLeading();
        }
    }
}
