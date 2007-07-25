/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.*;
import infovis.utils.InfovisUtilities;
import infovis.visualization.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Default implementation of VisualLabel.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DefaultVisualLabel extends VisualLabel implements Orientable {
    /** True when painting labels */
    protected boolean      showingLabel   = true;
    /** The orientation of labels */
    protected short        orientation    = ORIENTATION_INVALID;
    /** Function to clip a string when needed */
    protected LabelClipper labelClipper   = null;
    /** Justification between 0 (left) and 1 (right). */
    protected float        justification  = 0.5f;
    /** Vertical justification between 0 (top) and 1 (bottom). */
    protected float        vjustification = 0.5f;
    /** True if clipped by the item bounds, false otherwise */
    protected boolean      clipped        = true;
    /** True if outlining the font */
    protected boolean      outlined       = false;

    /**
     * Constructor.
     * @param child first children or null.
     * @param showingLabel true if label are shown
     * @param defaultColor default color
     */
    public DefaultVisualLabel(
            ItemRenderer child,
            boolean showingLabel,
            Color defaultColor) {
        super(child);
        this.showingLabel = showingLabel;
        this.defaultColor = defaultColor;
    }

    /**
     * Constructor.
     * @param showingLabel true if label are shown
     * @param defaultColor default color
     */
    public DefaultVisualLabel(boolean showingLabel, Color defaultColor) {
        this(null, showingLabel, defaultColor);
    }

    /**
     * Constructor.
     * @param defaultColor default color
     */
    public DefaultVisualLabel(Color defaultColor) {
        this(true, defaultColor);
    }

    /**
     * Constructor.
     * @param showingLabel true if label are shown
     */
    public DefaultVisualLabel(boolean showingLabel) {
        this(showingLabel, null);
    }

    /**
     * Default constructor.
     */
    public DefaultVisualLabel() {
        this(true, null);
    }

    /**
     * Constructor.
     * @param child first children or null.
     * @param showingLabel true if label are shown
     */
    public DefaultVisualLabel(ItemRenderer child, boolean showingLabel) {
        this(child, showingLabel, null);
    }

    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto,
            Visualization vis) {
        super.instantiateChildren(proto, vis);
        if (showingLabel) {
            this.labelColumn = findDefaultLabelColumn();
        }
        return this;
    }

    /** Column names that can become label names by default. */
    public static final String[] DEFAULT_LABEL_COLUMN_NAMES = { "Name", "Title", "Label" };

    /**
     * Returns a column that will be a default label or
     * null.
     * @return a column that will be a default label or
     * null
     */
    public Column findDefaultLabelColumn() {
        return findDefaultLabelColumn(getVisualization().getTable());
    }

    /**
     * Returns a column from a table that will be a default label or
     * null.
     * @param t the table
     * @return a column from a table that will be a default label or
     * null
     */
    public static Column findDefaultLabelColumn(Table t) {
        for (int i = 0; i < DEFAULT_LABEL_COLUMN_NAMES.length; i++) {
            String name = DEFAULT_LABEL_COLUMN_NAMES[i];
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
     * {@inheritDoc}
     */
    public double getWidth(String label) {
        if (label == null)
            return 0;
        if (orientation != ORIENTATION_NORTH
                && orientation != ORIENTATION_SOUTH) {
            return font.getStringBounds(label, FRC).getWidth();
        }
        else {
            return font.getStringBounds(label, FRC).getHeight();
        }
    }

    /**
     * {@inheritDoc}
     */
    public double getHeight(String label) {
        if (label == null)
            return 0;
        if (orientation != ORIENTATION_NORTH
                && orientation != ORIENTATION_SOUTH) {
            return font.getStringBounds(label, FRC).getHeight();
        }
        else {
            return font.getStringBounds(label, FRC).getWidth();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, int row, Shape s) {
        // Find good reason to avoid doing anything
        if (!showingLabel) {
            return;
        }
        Rectangle2D bounds = s.getBounds2D();
        double width = bounds.getWidth();
        double height = bounds.getHeight();
        if (isClipped() && (width < 3 || height < 3)) {
            // too small, don't even try
            return;
        }
        String label = getLabelAt(row);
        if (label == null) {
            // Nothing to show
            return;
        }
        graphics.setFont(getFont());
        FontMetrics fm = graphics.getFontMetrics();
        Rectangle2D labelBounds = fm.getStringBounds(label, graphics);
        boolean shouldRotate = false;
        boolean shouldClip = isClipped() && !bounds.contains(labelBounds);

        double hw = Math.min(labelBounds.getWidth(), width);
        double hh = Math.min(labelBounds.getHeight(), height);
        double vw = Math.min(labelBounds.getWidth(), height);
        double vh = Math.min(labelBounds.getHeight(), width);
        if (orientation == ORIENTATION_NORTH
                || orientation == ORIENTATION_SOUTH
                || (shouldClip && orientation == ORIENTATION_INVALID && hw * hh < vw
                        * vh)) {
            hw = vw;
            hh = vh;
            shouldRotate = true;
        }
        if (shouldClip) {
            if (labelClipper != null) {
                label = labelClipper.clip(label, graphics, labelBounds, hw, hh);
                if (label == null)
                    return;
                labelBounds = fm.getStringBounds(label, graphics);
            }
            if (!bounds.contains(labelBounds)) {
                Rectangle rect = bounds.getBounds();
                Graphics2D g = (Graphics2D) graphics.create(
                        rect.x,
                        rect.y,
                        rect.width,
                        rect.height);
                graphics = g;
                bounds = rect;
                rect.x = 0;
                rect.y = 0;
            }
        }
        // if (shouldRotate) {
        // graphics.translate(bounds.getCenterX(), bounds.getCenterY());
        // graphics.rotate(Math.PI / 2);
        // graphics.translate(-bounds.getCenterX(), -bounds.getCenterY());
        // }
        setColor(graphics, row);
        if (shouldRotate) {
            InfovisUtilities.drawStringVertical(
                    graphics,
                    label,
                    labelBounds,
                    bounds,
                    justification,
                    vjustification,
                    outlined);
        }
        else {
            InfovisUtilities.drawString(
                    graphics,
                    label,
                    labelBounds,
                    bounds,
                    justification,
                    vjustification,
                    outlined);
        }
    }

    protected void setColor(Graphics2D graphics, int row) {
        if (defaultColor != null) {
            graphics.setColor(defaultColor);
        }
        else if (isOutlined()) {
            graphics.setColor(Color.BLACK);
        }
        else {
            contrastColor(graphics, row);
        }
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
        if (this.showingLabel == showingLabel)
            return;
        this.showingLabel = showingLabel;
        invalidate();
    }

    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
        if (this.orientation == orientation)
            return;
        this.orientation = orientation;
        invalidate();
    }

    /**
     * Returns the current LabelClipper.
     * @return the current LabelClipper
     */
    public LabelClipper getLabelClipper() {
        return labelClipper;
    }

    /**
     * Sets the current LabelClipper.
     * @param labelClipper the LabelClipper or null.
     */
    public void setLabelClipper(LabelClipper labelClipper) {
        this.labelClipper = labelClipper;
        repaint();
    }

    /**
     * Returns the label justification betwenn 0 (left) and 1 (right).
     * @return the label justification betwenn 0 (left) and 1 (right).
     */
    public float getJustification() {
        return justification;
    }

    /**
     * Sets the label justification betwenn 0 (left) and 1 (right).
     * @param justification the justification
     */
    public void setJustification(float justification) {
        this.justification = justification;
        repaint();
    }

    /**
     * Returns true if the label is clipped by the item shape.
     * @return true if the label is clipped by the item shape
     */
    public boolean isClipped() {
        return clipped;
    }

    /**
     * Set to true if the label is clipped by the item shape.
     * @param clipped the value
     */
    public void setClipped(boolean clipped) {
        this.clipped = clipped;
        repaint();
    }

    /**
     * Returns true if the fonts is outlined.
     * @return true if the fonts is outlined
     */
    public boolean isOutlined() {
        return outlined;
    }

    /**
     * Set to true if the fonts should be outlined.
     * @param outlined true if the fonts should be outlined.
     */
    public void setOutlined(boolean outlined) {
        this.outlined = outlined;
        repaint();
    }
}