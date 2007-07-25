/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.awt.*;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

/**
 * 
 * Utility methods for drawing.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public abstract class InfovisUtilities {
    // Management of dilatation for outlining fonts.
    /** The kernel of the dilatation convolution. */
    public static final float[]              DILATE_KERNEL  = {
        1, 1, 1,
        1, 1, 1,
        1, 1, 1                                             };
    /** The convoluton ImageOp. */
    public static final BufferedImageOp   DILATE_OP        = new ConvolveOp(
            new Kernel(
                    3,
                    3,
                    DILATE_KERNEL));
    
    /** The Color components of the IndexColorModel. */
    public static final int [] TRANSPARENT_AND_WHITE = { 0, -1 };
    /** The transparent Color. */
    public static final Color TRANSPARENT = new Color(TRANSPARENT_AND_WHITE[0]);
    /** The WHITE outline component. */
    public static final Color WHITE = new Color(TRANSPARENT_AND_WHITE[1]); 

    public static void drawString(
            Graphics2D graphics, 
            String str,
            Rectangle2D labelBounds, 
            Rectangle2D bounds, 
            float hjustif, 
            float vjustif,
            boolean outlined) {
        double hmargin = bounds.getWidth() - labelBounds.getWidth();
        double vmargin = bounds.getHeight() - labelBounds.getHeight();
        Font font = graphics.getFont();
        int x = (int)(bounds.getX() + (hjustif < 0 ? -hjustif : hjustif * hmargin));
        int y = (int)(bounds.getY() + (vjustif < 0 ? -vjustif : vjustif * vmargin));

        if (outlined) {
          BufferedImage img = outlineString(
                  str,
                  font,
                  labelBounds.getBounds());
          graphics.drawImage(img, DILATE_OP, x-1, y-2);
          img.flush();
        }
        graphics.drawString(
                str, 
                x-(int)labelBounds.getX(),
                y-(int)labelBounds.getY());
    }

    public static void drawString(
            Graphics2D graphics, 
            String str,
            Rectangle2D bounds, 
            float hjustif, 
            float vjustif,
            boolean outlined) {
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(str, graphics);
        drawString(graphics, str, labelBounds, bounds, hjustif, vjustif, outlined);
    }
    
    public static void drawStringVertical(
            Graphics2D graphics, 
            String str,
            Rectangle2D labelBounds, 
            Rectangle2D bounds, 
            float hjustif, 
            float vjustif,
            boolean outlined) {
        AffineTransform at = graphics.getTransform();
        graphics.translate(bounds.getCenterX(), bounds.getCenterY());
        graphics.rotate(Math.PI/2);
        //graphics.translate(-bounds.getCenterX(), -bounds.getCenterY());
        Rectangle2D newBounds = new Rectangle2D.Double(
                -bounds.getHeight()/2, 
                -bounds.getWidth()/2, 
                bounds.getHeight(), 
                bounds.getWidth());
        drawString(graphics, str, labelBounds, newBounds, hjustif, vjustif, outlined);
        graphics.setTransform(at);
    }
    
    public static void drawStringVertical(
            Graphics2D graphics, 
            String str,
            Rectangle2D bounds, 
            float hjustif, 
            float vjustif,
            boolean outlined) {
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(str, graphics);
        drawStringVertical(
                graphics, 
                str, 
                labelBounds,
                bounds, 
                hjustif, 
                vjustif, 
                outlined);        
    }

    public static BufferedImage outlineString(
            String s, 
            Font font, 
            Rectangle rect) {
        int width = rect.width+2;
        int height = rect.height+2;
        IndexColorModel cm = new IndexColorModel(
                1,
                2, 
                TRANSPARENT_AND_WHITE, 
                0,
                true, 
                0, 
                DataBuffer.TYPE_BYTE);
        BufferedImage image = new BufferedImage(
                width, height, 
                BufferedImage.TYPE_BYTE_BINARY, 
                cm);
    
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        
        g2.setBackground(TRANSPARENT);
        g2.clearRect(0, 0, width, height);
        g2.setFont(font);
        g2.setColor(WHITE);
        g2.drawString(s, 1+rect.x, 1-rect.y);
        g2.dispose();
        return image;
    }
}
