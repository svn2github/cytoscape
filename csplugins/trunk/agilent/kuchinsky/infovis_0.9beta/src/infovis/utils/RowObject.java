/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import infovis.Table;

/**
 * RowObject is used to translate InfoVis table rows into
 * Java objects when required. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */

public class RowObject {
    protected Table table;
    protected int row;
    
    public RowObject(Table table, int row) {
        this.table = table;
        this.row = row;
    }
    
    public int getRow() {
        return row;
    }
    public Table getTable() {
        return table;
    }
    
    public static int getRow(Table table, Object obj) {
        if (obj instanceof RowObject) {
            RowObject ro = (RowObject) obj;
            assert(table == null || table == ro.getTable());
            return ro.getRow();
        }
        else {
            return Table.NIL;
        }
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof RowObject) {
            RowObject ro = (RowObject) obj;
            return ro.table == table && ro.row == row;
        }
        return false;
    }
}