/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import javax.swing.event.TableModelEvent;

import infovis.DynamicTable;
import infovis.utils.*;
import infovis.utils.IdManager;
import infovis.utils.RowIterator;

/**
 * Default implementatio for dynamic tables.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class DefaultDynamicTable extends DefaultTable
    implements DynamicTable {
    protected IdManager idManager;
    
    public DefaultDynamicTable() {
        idManager = new IdManager();
    }

    public DefaultDynamicTable(int size) {
        idManager = new IdManager(size);
    }
    
    public void clear() {
        idManager.clear();
        super.clear();
    }
    
    public int getRowCount() {
        return idManager.getIdCount();
    }
    
    class Iterator extends IdManagerIterator {
            public Iterator(boolean up) {
            super(DefaultDynamicTable.this.idManager, up);
        }
        public void remove() {
            removeRow(last);
        }
}
    
    public RowIterator iterator() {
        return new Iterator(true);
    }
    
    public RowIterator reverseIterator() {
        return new Iterator(false);
    }
    
    public boolean isRowValid(int row) {
        return row >= 0 && !idManager.isFree(row);
    }
    
    public int addRow() {
        int row = idManager.newId();
        if (hasTableModelListener()) {
            fireTableChanged(
                    new TableModelEvent(
                            this,
                            row,
                            row,
                            TableModelEvent.ALL_COLUMNS,
                            TableModelEvent.INSERT));
        }
        return row;
    }
    
    public void removeRow(int row) {
        if (! isRowValid(row)) {
            throw new RuntimeException("Row already removed");
        }
        idManager.free(row);
        if (hasTableModelListener()) {
            fireTableChanged(
                    new TableModelEvent(
                            this,
                            row,
                            row,
                            TableModelEvent.ALL_COLUMNS,
                            TableModelEvent.DELETE));
        }
    }
}
