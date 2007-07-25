/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.*;
import infovis.metadata.ValueCategory;
import infovis.visualization.ColorVisualization;
import infovis.visualization.ItemRenderer;
import infovis.visualization.color.ColorVisualizationFactory;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualColor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class VisualColor extends AbstractVisualColumn {
    public static final String VISUAL = Visualization.VISUAL_COLOR;
    public static Color defaultDefaultColor = new Color(0.95f, 0.95f, 1f);

    /** The column used for color */
    protected Column colorColumn;

    /** The ColorVisualization */
    protected ColorVisualization colorVisualization;

    /** The default color */
    protected Color defaultColor;
    
    /** True if using smooth shading */
    protected boolean smooth;

    public static VisualColor get(Visualization vis) {
        return (VisualColor) findNamed(Visualization.VISUAL_COLOR, vis);
    }

    public static VisualColor get(ItemRenderer ir) {
        return (VisualColor) findNamed(Visualization.VISUAL_COLOR, ir);
    }
    
    public VisualColor(ItemRenderer child) {
        this(child, defaultDefaultColor);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        setColumn(findDefaultColorColumn());
        return this;
    }
    

    public VisualColor(
            ItemRenderer c1,
            ItemRenderer c2) {
        this(c1, defaultDefaultColor);
        addRenderer(c2);
    }

    public VisualColor(
            ItemRenderer c1,
            ItemRenderer c2,
            ItemRenderer c3) {
        this(c1, defaultDefaultColor);
        addRenderer(c2);
        addRenderer(c3);
    }

    public VisualColor(
            ItemRenderer c1,
            ItemRenderer c2,
            ItemRenderer c3,
            ItemRenderer c4) {
        this(c1, defaultDefaultColor);
        addRenderer(c2);
        addRenderer(c3);
        addRenderer(c4);
    }

    public VisualColor(ItemRenderer child, Color def) {
        super(Visualization.VISUAL_COLOR);
        this.defaultColor = def;
        addRenderer(child);
    }

    public void setColumn(Column column) {
        if (colorColumn == column) return;
        super.setColumn(column);
        colorColumn = column;
        createColorVisualization();
        invalidate();
    }
    
    public void createColorVisualization() {
        if (colorVisualization != null) {
            if (colorColumn != null
                    && colorVisualization.getClass().equals(
                    ColorVisualizationFactory.createdColorVisualization(colorColumn))) {
                colorVisualization.setColumn(colorColumn);
            }
            else {
                colorVisualization.setColumn(null);
            }
        }
        if (colorColumn != null) {
            colorVisualization = ColorVisualizationFactory
                    .createColorVisualization(colorColumn);
        }
        else {
            colorVisualization = null;
        }
    }

    public Column getColumn() {
        return colorColumn;
    }
    
    public Column findDefaultColorColumn() {
        Table t = getVisualization().getTable();
        for (int i = 0; i < t.getColumnCount(); i++) {
            Column c = t.getColumnAt(i);
            if (c.isInternal()) {
                continue;
            }
            String cat = (String)c.getMetadata().getAttribute(ValueCategory.VALUE_CATEGORY_TYPE);
            if (cat != null 
                    && cat.equals(ValueCategory.VALUE_CATEGORY_TYPE_EXPLICIT)) {
                return c;
            }
        }
        return null;
    }

    public Color getColorAt(int row) {
        if (colorColumn == null || colorVisualization == null) {
            return defaultColor;
        }
        else if (colorColumn.isValueUndefined(row)) {
            return null;
        } else {
            return colorVisualization.getColor(row);
        }
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
        Color c = getColorAt(row);
        if (c == null) return;
        if (smooth) {
            Paint saved = graphics.getPaint();
            try {
                Rectangle2D box = shape.getBounds2D();
                Color color = getColorAt(row);
                GradientPaint gradient = new GradientPaint((float) box
                        .getMinX(), (float) box.getMinY(), color
                        .brighter(), (float) box.getMaxX(), (float) box
                        .getMaxY(), color.darker(), false);
                graphics.setPaint(gradient);
                super.paint(graphics, row, shape);
            } finally {
                graphics.setPaint(saved);
            }
        } else {
            Color savedColor = graphics.getColor();
            if (c == savedColor) {
                super.paint(graphics, row, shape);
            }
            else {
                try {
                    graphics.setColor(c);
                    super.paint(graphics, row, shape);
                } finally {
                    graphics.setColor(savedColor);
                }
            }
        }
    }

    public ColorVisualization getColorVisualization() {
        return colorVisualization;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public void setColorVisualization(ColorVisualization cv) {
        colorVisualization = cv;
        invalidate();
    }

    public void setDefaultColor(Color color) {
        defaultColor = color;
        invalidate();
    }
    /**
     * Returns the smooth.
     * @return boolean
     */
    public boolean isSmooth() {
        return smooth;
    }

    /**
     * Sets the smooth.
     * @param smooth The smooth to set
     */
    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
        invalidate();
    }
}