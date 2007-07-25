/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Column;
import infovis.column.ColumnFilter;
import infovis.column.ColumnFilterException;

/**
 * Class DefaultVisualColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */

    
public class DefaultVisualColumn implements VisualColumnDescriptor {
    protected String name;
    protected Column column;
    protected boolean invalidate;
    protected ColumnFilter filter;
        
    public DefaultVisualColumn(
            String name,
            boolean invalidate,
            ColumnFilter filter) {
        this.name = name;
        this.invalidate = invalidate;
        this.filter = filter;
    }
        
    public DefaultVisualColumn(String name, boolean invalidate) {
        this(name, invalidate, null);
    }
        
    public DefaultVisualColumn(String name) {
        this(name, true);
    }
    
    public String getName() {
        return name;
    }
    
    public void setColumn(Column column) {
        if (filter != null && filter.filter(column))
            throw new ColumnFilterException("Invalid column filter");
        this.column = column;
    }
        
    public Column getColumn() {
        return column;
    }
    public ColumnFilter getFilter() {
        return filter;
    }

    public boolean isInvalidate() {
        return invalidate;
    }

    public void setFilter(ColumnFilter filter) {
        this.filter = filter;
    }

    public void setInvalidate(boolean b) {
        invalidate = b;
    }

}