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
 * Class VisualColumnProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class VisualColumnProxy implements VisualColumnDescriptor {
    public String getName() {
        return vc.getName();
    }
    VisualColumnDescriptor vc;
    
    public VisualColumnProxy(VisualColumnDescriptor vc) {
        this.vc = vc;
    }

    public boolean isInvalidate() {
        return vc.isInvalidate();
    }

    public Column getColumn() {
        return vc.getColumn();
    }

    public ColumnFilter getFilter() {
        return vc.getFilter();
    }

    public void setInvalidate(boolean b) {
        vc.setInvalidate(b);
    }

    public void setFilter(ColumnFilter filter) {
        vc.setFilter(filter);
    }

    public void setColumn(Column column) {
        vc.setColumn(column);
    }

}
