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

/**
 * Class VisualColumnDescriptor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public interface VisualColumnDescriptor {
    public abstract String getName();
    public abstract void setColumn(Column column);
    public abstract Column getColumn();
    public abstract ColumnFilter getFilter();
    public abstract void setFilter(ColumnFilter filter);
    public abstract boolean isInvalidate();
    public abstract void setInvalidate(boolean b);
}