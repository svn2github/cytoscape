/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.filter;

//import column.StringColumn;
import infovis.column.NumberColumn;
import infovis.column.StringColumn;

import infovis.Column;
import infovis.column.ColumnFilter;

/**
 * Filter out non StringColumns.  Used in ScatterPlotVisualization
 * 
 * based on infovis.column.filter.NotNumberFilter by

 * @author Naomi Dushay
 * @see infovis.column.filter.NotNumberFilter
 * @version $Revision: 1.4 $
 */
public class NotStringOrNumberFilter implements ColumnFilter {
    static NotStringOrNumberFilter instance = new NotStringOrNumberFilter();

    /**
     * Avoid creating several instances since we need only one.
     *
     * @return the shared instance.
     */
    public static NotStringOrNumberFilter sharedInstance() {
        return instance;
    }

    /**
     * @see infovis.column.ColumnFilter#filter(Column)
     * 
     * @return <code>true</code> if the <code>Column</code>
     * should be filtered out.
     *
     */
    public boolean filter(Column c) {
        return c != null && ! (c instanceof StringColumn) && !(c instanceof NumberColumn);
    }
}

