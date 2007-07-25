/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import infovis.*;
import infovis.visualization.*;

public class VisualLabel extends AbstractVisualColumn {
    public static final String               VISUAL        = Visualization.VISUAL_LABEL;
    /** The column used for labeling */
    protected transient Column               labelColumn;
    /** The default font */
    protected Font                           font   = new Font(
                                                                   "Dialog",
                                                                   Font.PLAIN,
                                                                   10);
    /** The orientation of labels */
    /** The default color used for drawing the fonts or NULL if automatic */
    protected Color                          defaultColor  = null;

    protected static final FontRenderContext FRC           = new FontRenderContext(
            null,
            false,
            false);

    public VisualLabel(ItemRenderer child, Color defaultColor) {
        super(VISUAL);
        this.defaultColor = defaultColor;
        addRenderer(child);
    }

    public VisualLabel(ItemRenderer child) {
        this(child, null);
    }

    public VisualLabel() {
        super(VISUAL);
    }
    
    public VisualLabel(String name) {
        super(name);
    }

    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto,
            Visualization vis) {
        super.instantiateChildren(proto, vis);
        return this;
    }

    public static VisualLabel get(Visualization vis) {
        return (VisualLabel) findNamed(VISUAL, vis);
    }

    public static VisualLabel get(ItemRenderer ir) {
        return (VisualLabel) findNamed(VISUAL, ir);
    }

    public static String getLabelAt(Visualization vis, int row) {
        VisualLabel vl = get(vis);
        if (vl == null) {
            return null;
        }
        return vl.getLabelAt(row);
    }

    public Column getColumn() {
        return labelColumn;
    }

    public void setColumn(Column column) {
        if (column == labelColumn)
            return;
        super.setColumn(column);
        this.labelColumn = column;
        invalidate();
    }

    /**
     * Returns the defaultFont.
     * 
     * @return Font
     */
    public Font getFont() {
        if (font == null && visualization.getParent() != null)
            return visualization.getParent().getFont();
        return font;
    }

    /**
     * Sets the defaultFont.
     * 
     * @param defaultFont
     *            The defaultFont to set
     */
    public void setFont(Font defaultFont) {
        this.font = defaultFont;
        invalidate();
    }

    /**
     * Returns the label associated with the specified row.
     * 
     * @param row
     *            the row.
     * 
     * @return the label associated with the specified row.
     */
    public String getLabelAt(int row) {
        if (labelColumn == null)
            return null;
        return labelColumn.getValueAt(row);
    }

    public double getWidth(String label) {
        if (label == null)
            return 0;
        return font.getStringBounds(label, FRC).getWidth();
    }

    public double getHeight(String label) {
        if (label == null)
            return 0;
        return font.getStringBounds(label, FRC).getHeight();
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        repaint();
    }

    public void paint(Graphics2D graphics, int row, Shape s) {
        String label = getLabelAt(row);
        if (label == null) {
            // Nothing to show
            return;
        }
        Rectangle2D bounds = s.getBounds2D();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (width < 3 || height < 3) {
            // too small, don't even try
            return;
        }
        graphics.setFont(getFont());
        graphics.setColor(getDefaultColor());
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);
        graphics.drawString(
                label,
                (float)(bounds.getCenterX()-labelBounds.getCenterX()),
                (float)(bounds.getCenterY()-labelBounds.getCenterY()));
    }
}
