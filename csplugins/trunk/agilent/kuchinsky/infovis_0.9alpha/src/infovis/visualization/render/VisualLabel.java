/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.*;
import infovis.visualization.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualLabel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class VisualLabel extends AbstractVisualColumn
    implements Orientable {
    public static final String VISUAL = Visualization.VISUAL_LABEL;
    /** The column used for labeling */
    protected transient Column labelColumn;

    /** The default font */
    protected Font defaultFont = new Font("Dialog", Font.PLAIN, 10);
    
    protected static final FontRenderContext FRC = new FontRenderContext(null, false, false);

    /** True when painting labels */
    protected boolean showingLabel = true;

    /** The orientation of labels */
    protected short orientation = ORIENTATION_INVALID;

    /** Function to clip a string when needed */
    protected LabelClipper labelClipper = null;

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

    public VisualLabel(ItemRenderer child, boolean showingLabel) {
        super(VISUAL);
        this.showingLabel = showingLabel;
        addRenderer(child);
    }
    
    public VisualLabel(ItemRenderer child) {
        this(child, true);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        if (showingLabel) {
            setColumn(findDefaultLabelColumn());
        }
        return this;
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

    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (graphics != null) {
            graphics.setFont(defaultFont);
        }
    }
    
    static public String[] defaultLabelColumnNames = {
            "Name",
            "Title",
            "Label"
    };
    
    public Column findDefaultLabelColumn() {
        Table t = getVisualization().getTable();
        for (int i = 0; i < defaultLabelColumnNames.length; i++) {
            String name = defaultLabelColumnNames[i];
            Column c = t.getColumn(name);
            if (c != null) {
                return c;
            }
            name = name.toUpperCase();
            c = t.getColumn(name);
            if (c != null) {
                return c;
            }
            name = name.toLowerCase();
            c = t.getColumn(name);
            if (c != null) {
                return c;
            }
        }
        return null;
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
        if (label == null) return 0;
        return defaultFont.getStringBounds(label, FRC).getWidth();
    }

    public double getHeight(String label) {
        if (label == null) return 0;
        return defaultFont.getStringBounds(label, FRC).getHeight();
    }

    public void paint(Graphics2D graphics, int row, Shape s) {
        if (!showingLabel) {
            return;
        }
        Rectangle2D bounds = s.getBounds2D();
        if (bounds.getWidth() < 3 || bounds.getHeight() < 3) {
            return;
        }
        String label = getLabelAt(row);
        if (label == null) {
            return;
        }
        FontMetrics fm = graphics.getFontMetrics();

        Rectangle2D maxCharBounds = fm.getMaxCharBounds(graphics);
        boolean invisibleH = 
            (maxCharBounds.getWidth() > (bounds.getWidth() * 2))
          || (maxCharBounds.getHeight() > (bounds.getHeight() * 2));
        if (invisibleH
                && (orientation == ORIENTATION_EAST || orientation == ORIENTATION_WEST)) {
            return;
        }
        boolean invisibleV = 
                (maxCharBounds.getHeight() > (bounds.getWidth() * 2))
             || (maxCharBounds.getWidth() > (bounds.getHeight() * 2));
        if (invisibleV
                && (orientation == ORIENTATION_NORTH 
                   || orientation == ORIENTATION_SOUTH)) {
            return;
        }
        if (invisibleH && invisibleV) {
            return;
        }
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);

        double hw = Math.min(
                labelBounds.getWidth(), 
                bounds.getWidth());
        double hh = Math.min(
                labelBounds.getHeight(), 
                bounds.getHeight());
        double vw = Math.min(
                labelBounds.getWidth(),
                bounds.getHeight());
        double vh = Math.min(
                labelBounds.getHeight(), 
                bounds.getWidth());

        Graphics2D g = (Graphics2D)graphics.create(
                (int)bounds.getX(), (int)bounds.getY(),
                (int)bounds.getWidth(), (int)bounds.getHeight());
        g.translate(bounds.getWidth()/2, bounds.getHeight()/2);
        contrastColor(g);

        if (orientation == ORIENTATION_NORTH
                || orientation == ORIENTATION_SOUTH
                || (orientation == ORIENTATION_INVALID 
                        && hw * hh < vw * vh)) {
            hw = vw;
            hh = vh;
            g.rotate(Math.PI / 2);
            if (labelClipper != null) {
                label = labelClipper.clip(
                        label, g, labelBounds, 
                        hw, hh);
                if (label == null)
                    return;
            }
        }        
        g.drawString(label,
                (float) (-labelBounds.getWidth() / 2),
                (float) -(labelBounds.getHeight() / 2 
                        + labelBounds.getY()));
    }

    /**
     * Returns the defaultFont.
     * 
     * @return Font
     */
    public Font getDefaultFont() {
        if (defaultFont == null && visualization.getParent() != null)
            return visualization.getParent().getFont();
        return defaultFont;
    }

    /**
     * Sets the defaultFont.
     * 
     * @param defaultFont
     *            The defaultFont to set
     */
    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
        invalidate();
    }

    /**
     * Returns the showingLabel.
     * 
     * @return boolean
     */
    public boolean isShowingLabel() {
        return showingLabel;
    }

    /**
     * Sets the showingLabel.
     * 
     * @param showingLabel
     *            The showingLabel to set
     */
    public void setShowingLabel(boolean showingLabel) {
        this.showingLabel = showingLabel;
        invalidate();
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short orientation) {
        if (this.orientation == orientation)
            return;
        this.orientation = orientation;
        invalidate();
    }
    
    
    public LabelClipper getLabelClipper() {
        return labelClipper;
    }
    public void setLabelClipper(LabelClipper labelClipper) {
        this.labelClipper = labelClipper;
        invalidate();
    }
}