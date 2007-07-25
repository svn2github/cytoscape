/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.*;
import infovis.column.BooleanColumn;
import infovis.column.filter.NotTypedFilter;
import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualSelection
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class VisualSelection extends AbstractVisualColumn {
    public static final String VISUAL = Visualization.VISUAL_SELECTION;
    public static final Color CONTRAST = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    public final static float DASH1[] = {10.0f};
    public static final BasicStroke DASHED1 = 
        new BasicStroke(
                1.0f, 
                BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_MITER, 
                10.0f, 
                DASH1, 
                0.0f);
    public static final BasicStroke DASHED2 = 
        new BasicStroke(
                1.0f, 
                BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_MITER, 
                10.0f, 
                DASH1, 
                10.0f);
    
    /** The color of selected items */
    public static Color defaultSelectedColor = Color.RED;
    /** The color of unselected items */
    protected static Color defaultUnselectedColor = Color.BLACK;

    /** The color of selected items */
    protected Color selectedColor;
    /** The color of unselected items */
    protected Color unselectedColor;
    
    /** The boolean column containing and managing the selection */
    protected BooleanColumn selection;

    public static VisualSelection get(Visualization vis) {
        return (VisualSelection)findNamed(VISUAL, vis);
    }

    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        if (filter == null) {
            filter = new NotTypedFilter(BooleanColumn.class);
        }
        selection = BooleanColumn.findColumn(
                visualization.getTable(),
                Table.SELECTION_COLUMN);
        return this;
    }
    public VisualSelection(ItemRenderer child) {
        this(child, defaultSelectedColor, defaultUnselectedColor);
    }

    public VisualSelection(
            ItemRenderer child, 
            Color selectedColor,
            Color unselectedColor) {
        super(VISUAL);
        addRenderer(child);
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
    }
    
    public Column getColumn() {
        return selection;
    }
    
    public BooleanColumn getSelection() {
        return selection;
    }
    
    public void setColumn(Column column) {
        if (selection == column) return;
        super.setColumn(column);
        selection = (BooleanColumn)column;
        invalidate();
    }
    
    public Color getColorAt(int row) {
        if (selection == null) {
            return unselectedColor;
        }
        return selection.isValueUndefined(row) ? unselectedColor : selectedColor;
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
        Color color = getColorAt(row);
        if (color == null) {
            super.paint(graphics, row, shape);
            return;
        }
        if (color != selectedColor) {
            Rectangle2D b = shape.getBounds2D();
            if (b.getWidth() < 3 || b.getHeight() < 3) {
                //super.paint(graphics, row, shape);
                return;
            }
        }
        Color saved = graphics.getColor();
        //java.awt.Stroke saved = graphics.getStroke();
        
        try {
            if (color == CONTRAST) {
                saved = contrastColor(graphics);
            }
            else {
                graphics.setColor(color);
            }
            //graphics.setStroke(dashed1);
            super.paint(graphics, row, shape);
        }
        finally {
            graphics.setColor(saved);
//            graphics.setStroke(saved);
        }
    }

    /**
     * Returns the selectedColor.
     *
     * @return Color
     */
    public Color getSelectedColor() {
        return selectedColor;
    }

    /**
     * Sets the selectedColor.
     *
     * @param selectedColor The selectedColor to set
     */
    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
        invalidate();
    }

    /**
     * Returns the unselectedColor.
     *
     * @return Color
     */
    public Color getUnselectedColor() {
        return unselectedColor;
    }

    /**
     * Sets the unselectedColor.
     *
     * @param unselectedColor The unselectedColor to set
     */
    public void setUnselectedColor(Color unselectedColor) {
        this.unselectedColor = unselectedColor;
        invalidate();
    }
}
