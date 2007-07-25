/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.DynamicTable;
import infovis.utils.*;

import javax.swing.event.TableModelEvent;

/**
 * Default implementatio for dynamic tables.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class DefaultDynamicTable extends DefaultTable
    implements DynamicTable {
    protected IdManager idManager;
    
    /**
     * Constructor.
     *
     */
    public DefaultDynamicTable() {
        idManager = new IdManager();
    }

    /**
     * Constructor with a given allocated rowCount.
     * @param size the number of rows already allocated.
     */
    public DefaultDynamicTable(int size) {
        idManager = new IdManager(size);
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        idManager.clear();
        super.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return idManager.getIdCount();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getLastRow() {
        return idManager.getMaxId();
    }
    
    class Iterator extends IdManagerIterator {
            public Iterator(boolean up) {
            super(DefaultDynamicTable.this.idManager, up);
        }
        public void remove() {
            removeRow(last);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new Iterator(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return new Iterator(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isRowValid(int row) {
        return row >= 0 && !idManager.isFree(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public int addRow() {
        int size = getRowCount();
        int row = idManager.newId();
        if (hasTableModelListener()) {
            int op;
            if (row == size)
                op = TableModelEvent.INSERT;
            else
                op = TableModelEvent.UPDATE;
            fireTableChanged(
                    new TableModelEvent(
                            this,
                            row,
                            row,
                            TableModelEvent.ALL_COLUMNS,
                            op));
        }
        return row;
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeRow(int row) {
        if (! isRowValid(row)) {
            throw new RuntimeException("Row already removed");
        }
        int size = getRowCount();
        idManager.free(row);
        if (hasTableModelListener()) {
            int op;
            if (size != getRowCount()) 
                op = TableModelEvent.DELETE;
            else 
                op = TableModelEvent.UPDATE;
            fireTableChanged(
                    new TableModelEvent(
                            this,
                            row,
                            row,
                            TableModelEvent.ALL_COLUMNS,
                            op));
        }
    }
}
