/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.DynamicTable;

/**
 * Class DynamicTableProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DynamicTableProxy extends TableProxy implements DynamicTable {
    public DynamicTableProxy(DynamicTable table) {
        super(table);
    }
    
    public DynamicTable getDynamicTable() {
        return (DynamicTable)getTable();
    }
    
    public int addRow() {
        return getDynamicTable().addRow();
    }
    
    public void removeRow(int row) {
        getDynamicTable().removeRow(row);
    }
}
