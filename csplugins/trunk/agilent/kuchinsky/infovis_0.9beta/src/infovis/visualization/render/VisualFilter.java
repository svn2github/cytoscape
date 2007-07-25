/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.*;
import infovis.column.FilterColumn;
import infovis.column.filter.NotTypedFilter;
import infovis.utils.RowFilter;
import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Class VisualFilter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class VisualFilter extends AbstractVisualColumn implements RowFilter {
    public static final String VISUAL = Visualization.VISUAL_FILTER;
    protected FilterColumn filter;
    protected RowFilter extraFilter;

    public static VisualFilter get(Visualization vis) {
        return (VisualFilter)findNamed(VISUAL, vis);
    }
    
    public VisualFilter(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        super.filter = new NotTypedFilter(FilterColumn.class);
        filter = FilterColumn.findColumn(
                    visualization.getTable(),
                    Table.FILTER_COLUMN);
        return this;
    }
    
    public Column getColumn() {
        return filter;
    }
    
    public FilterColumn getFilterColumn() {
        return filter;
    }
    
    public void setColumn(Column column) {
        if (filter == column) return;
        super.setColumn(column);
        filter = (FilterColumn)column;
        invalidate();
    }
    
    public boolean isFiltered(int row) {
        return (filter != null && filter.isFiltered(row))
            || (extraFilter != null && extraFilter.isFiltered(row));
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (! isFiltered(row))
            super.paint(graphics, row, shape);
    }

    
    public RowFilter getExtraFilter() {
        return extraFilter;
    }
    
    public void setExtraFilter(RowFilter extraFilter) {
        if (this.extraFilter == extraFilter) return;
        this.extraFilter = extraFilter;
        invalidate();
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        if (isFiltered(row)) return false;
        return super.pick(hitBox, row, shape);
    }
}
