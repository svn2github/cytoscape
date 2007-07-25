/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table;

import infovis.Column;
import infovis.Table;
import infovis.column.ColumnProxy;
import infovis.utils.RowIterator;

import javax.swing.event.*;


/**
 * A Table Proxy implements an <code>Table</code> by forwarding all
 * the methods to an internal <code>Table</code>.
 * 
 * <p>This class is useful to
 * implements higher level containers out of a standard
 * <code>DefaultTable</code>.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.23 $
 */
public class TableProxy extends ColumnProxy
    implements Table, TableModelListener {
    protected Table table;

    /**
     * Creates a new TableProxy object.
     *
     * @param table The underlying table.
     *
     * @see infovis.Table#getTable()
     */
    public TableProxy(Table table) {
        super(table);
        this.table = table;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumnCount() {
        return table.getColumnCount();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        table.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void addColumn(Column c) {
        table.addColumn(c);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumnAt(int index) {
        return table.getColumnAt(index);
    }

    /**
     * {@inheritDoc}
     */
    public void setColumnAt(int i, Column c) {
        table.setColumnAt(i, c);
    }

    /**
     * {@inheritDoc}
     */
    public int indexOf(String name) {
        return table.indexOf(name);
    }
    
    /**
     * {@inheritDoc}
     */
    public int indexOf(Column column) {
        return table.indexOf(column);
    }

    /**
     * {@inheritDoc}
     */
    public Column getColumn(String name) {
        return table.getColumn(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeColumn(Column c) {
        return table.removeColumn(c);
    }

    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        return table.getTable();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRowValid(int row) {
        return table.isRowValid(row);
    }

    // interface TableModel

    /**
     * {@inheritDoc}
     */
    public String getColumnName(int columnIndex) {
        return table.getColumnName(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return table.getRowCount();
    }

    /**
     * {@inheritDoc}
     */
    public int getLastRow() {
        return table.getLastRow();
    }

    /**
     * {@inheritDoc}
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return table.getValueAt(rowIndex, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        table.setValueAt(aValue, rowIndex, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Class getColumnClass(int columnIndex) {
        return table.getColumnClass(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return table.isCellEditable(rowIndex, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return table.reverseIterator();
    }

    /**
     * {@inheritDoc}
     */
    public void addTableModelListener(TableModelListener l) {
        EventListenerList listeners = getEventListenerList();
        if (listeners.getListenerCount() == 0) {
            table.addTableModelListener(this);
        }
        listeners.add(TableModelListener.class, l);
    }

    /**
     * {@inheritDoc}
     */
    public void removeTableModelListener(TableModelListener l) {
        if (eventListenerList == null) {
            return;
        }
        eventListenerList.remove(TableModelListener.class, l);
        if (eventListenerList.getListenerCount() == 0) { 
            table.removeTableModelListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void tableChanged(TableModelEvent e) {
        // Forward events
        if (e.getSource() == table) {
            // Invariant:
            // Never called when the listener list is null
            if (eventListenerList.getListenerCount(TableModelListener.class) == 0) {
                return;
            }
            TableModelEvent ne = new TableModelEvent(
                    this,
                    e.getFirstRow(),
                    e.getLastRow(),
                    e.getColumn(),
                    e.getType());
            Object[] ll = eventListenerList.getListenerList();
            for (int i = 0; i < ll.length; i += 2) {
                if (ll[i] == TableModelListener.class) {
                    TableModelListener l = (TableModelListener)ll[i+1];
                    l.tableChanged(ne);
                }
            }
        }
    }
}
