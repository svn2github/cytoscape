/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Interface RowFilter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface RowFilter {
    public boolean isFiltered(int row);
}
