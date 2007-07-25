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
 * Proxy of a Dynamic Table.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DynamicTableProxy extends TableProxy implements DynamicTable {
    /**
     * Constructor.
     * @param table the reference dynamic table.
     */
    public DynamicTableProxy(DynamicTable table) {
        super(table);
    }
    
    /**
     * Returns the reference dynamic table.
     * @return the reference dynamic table
     */
    public DynamicTable getDynamicTable() {
        return (DynamicTable)getTable();
    }
    
    /**
     * {@inheritDoc}
     */
    public int addRow() {
        return getDynamicTable().addRow();
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeRow(int row) {
        getDynamicTable().removeRow(row);
    }
}
