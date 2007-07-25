/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.filter;

import infovis.Column;
import infovis.column.ColumnFilter;


/**
 * Filter for composing Filters.
 *
 * Since the Filter object returns <code>true</code> if a column is
 * filtered out, the composition should perform an <code>or</code>.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class ComposeOrFilter implements ColumnFilter {
    protected ColumnFilter filter1;
    protected ColumnFilter filter2;

    /**
     * Compose two filters using an "or"
     * 
     * @param f1 first filter or null
     * @param f2 second filter or null
     * @return a filter composimg f1 and f2
     */
    public static ColumnFilter create(ColumnFilter f1, ColumnFilter f2) {
        if (f1 == null) return f2;
        if (f2 == null) return f1;
        return new ComposeOrFilter(f1, f2);
    }
    /**
     * Creates a new ComposeOrFilter object.
     *
     * @param filter1 first filter.
     * @param filter2 second filter.
     */
    public ComposeOrFilter(ColumnFilter filter1, ColumnFilter filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }

    /**
     * @see infovis.column.ColumnFilter#filter(Column)
     */
    public boolean filter(Column column) {
        return filter1.filter(column) || filter2.filter(column);
    }
}
