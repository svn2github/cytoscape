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
 * Class FilterAll
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class FilterAll implements ColumnFilter {
    public static final FilterAll instance = new FilterAll();
    
    public static FilterAll getInstance() { 
        return instance;
    }
    public FilterAll() {
    }

    public boolean filter(Column column) {
        return true;
    }

}
