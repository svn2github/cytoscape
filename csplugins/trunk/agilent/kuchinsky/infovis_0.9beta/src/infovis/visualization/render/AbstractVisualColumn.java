/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.column.ColumnFilterException;
import infovis.visualization.VisualColumnDescriptor;
import infovis.visualization.color.Colors;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Abstract class for item renderers that are also Visual Column Descriptors.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public abstract class AbstractVisualColumn extends AbstractItemRenderer
        implements VisualColumnDescriptor, ColumnFilter {
    protected boolean invalidate;
    protected ColumnFilter filter;

    public AbstractVisualColumn(String name) {
        super(name);
    }

    public void setColumn(Column column) {
        if (column == null) return;
        if (filter != null && filter.filter(column))
            throw new ColumnFilterException("Invalid column filter");
    }

    public ColumnFilter getFilter() {
        return filter;
    }
    public void setFilter(ColumnFilter filter) {
        this.filter = filter;
    }
    
    public boolean filter(Column column) {
        if (filter == null) return false;
        return filter.filter(column);
    }

    public boolean isInvalidate() {
        return invalidate;
    }

    public void setInvalidate(boolean b) {
        invalidate = b;
    }
    
    public void invalidate() {
        if (getVisualization() == null) {
            return;
        }
        getVisualization().fireVisualColumnDescriptorChanged(getName());
    }
    
    public Color contrastColor(Graphics2D graphics, int row) {
        Visualization vis = getVisualization();
        if (vis == null)
            return contrastColor(graphics);
        VisualColor vc = VisualColor.get(vis);
        if (vc == null)
            return contrastColor(graphics);
        Color c = contrastColor(vc.getColorAt(row));
        graphics.setColor(c);
        return c;
    }
    
    public static Color contrastColor(Graphics2D graphics) {
        Color c = graphics.getColor();

        c = contrastColor(c);
        graphics.setColor(c);
        return c;
    }

    private static Color contrastColor(Color c) {
        float luminance;
        if (c == null) {
            luminance = 1;
        }
        else {
            luminance = Colors.getLuminance(c);
        }

        if (luminance < 0.2) {
            return Color.WHITE;
        }
        else {
            return Color.BLACK;
        }
    }

}