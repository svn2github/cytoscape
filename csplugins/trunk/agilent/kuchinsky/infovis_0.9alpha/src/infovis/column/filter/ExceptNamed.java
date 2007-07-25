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
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ExceptNamed implements ColumnFilter {
    String name;
    /**
     * Constructor for ExceptNamed.
     */
    public ExceptNamed(String name) {
        this.name = name;
    }

    /**
     * @see infovis.column.ColumnFilter#filter(Column)
     */
    public boolean filter(Column column) {
        return !name.equals(column.getName());
    }

}
