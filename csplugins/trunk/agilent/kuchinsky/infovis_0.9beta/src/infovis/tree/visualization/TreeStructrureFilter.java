/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Column;
import infovis.column.ColumnFilter;
import infovis.tree.DegreeColumn;
import infovis.tree.DepthColumn;


/**
 * Filter out non tree-structural columns.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class TreeStructrureFilter implements ColumnFilter {
    static TreeStructrureFilter instance = new TreeStructrureFilter();

    /**
     * @see infovis.column.ColumnFilter#filter(Column)
     */
    public boolean filter(Column column) {
        String name = column.getName();
        return !(name.equals(DegreeColumn.DEGREE_COLUMN) ||
               name.equals(DepthColumn.DEPTH_COLUMN) || !column.isInternal());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static TreeStructrureFilter sharedInstance() {
        return instance;
    }
}
