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
import infovis.column.ColumnColumn;
import infovis.utils.*;

import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

import javax.swing.event.*;


/**
 * Concrete Table.
 *
 * Implements all the methods of <code>Table</code> managing a
 * Column of Columns
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.27 $
 */
public class DefaultTable extends ColumnColumn 
    implements Table, ChangeListener, Serializable {
    /** Identifies the addtion of new rows or columns. */
    protected static final int INSERT =  TableModelEvent.INSERT;
    /** Identifies a change to existing data. */
    protected static final int UPDATE =  TableModelEvent.UPDATE;
    /** Identifies the removal of rows or columns. */
    protected static final int DELETE = TableModelEvent.DELETE;
    
    protected transient EventListenerList listenerList;

    /**
     * Creates a new DefaultTable object.
     */
    public DefaultTable() {
        super(null);
    }

    /**
     * @see Table#getColumnCount()
     */
    public int getColumnCount() {
        return size();
    }

    /**
     * @see Table#clear()
     */
    public void clear() {
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnAt(i).clear();
        }
        if (metadata != null) {
            metadata.clear();
        }
        if (clientPropery != null) {
            clientPropery.clear();
        }

        fireTableStructureChanged();
    }


    /**
     * @see Table#addColumn(Column)
     */
    public void addColumn(Column c) {
        assert(indexOf(c) == -1);
        add(c);
        if (hasTableModelListener()) {
            c.addChangeListener(this);
        }
        fireTableStructureChanged();
    }

    /**
     * @see Table#getColumnAt(int)
     */
    public Column getColumnAt(int index) {
        return get(index);
    }

    /**
     * @see Table#setColumnAt(int,Column)
     */
    public void setColumnAt(int i, Column c) {
        if (hasTableModelListener()) {
            Column old = getColumnAt(i);
            if (old != c) {
                old.removeChangeListener(this);
            }
        }
        set(i, c);
        if (hasTableModelListener()) {
            c.addChangeListener(this);
        }
        fireTableStructureChanged();
    }

    /**
     * @see Table#indexOf(String)
     */
    public int indexOf(String name) {
        for (int i = 0; i < getColumnCount(); i++) {
            Column col = getColumnAt(i);
            if (col.getName().equals(name))
                return i;
        }
        return -1;
    }

    /**
     * @see infovis.Table#indexOf(Column)
     */
    public int indexOf(Column column) {
        for (int i = 0; i < getColumnCount(); i++) {
            Column col = getColumnAt(i);
            if (col == column)
                return i;
        }
        return -1;
    }
    
    public void disableNotify() {
        super.disableNotify();
        for (int i = 0; i < size(); i++) {
            if (! isValueUndefined(i)) {
                get(i).disableNotify();
            }
        }
    }

    public void enableNotify() {
        for (int i = 0; i < size(); i++) {
            if (! isValueUndefined(i)) {
                get(i).enableNotify();
            }
        }
        super.enableNotify();
    }
    
    /**
     * @see Table#getColumn(String)
     */
    public Column getColumn(String name) {
        for (int i = 0; i < getColumnCount(); i++) {
            Column col = getColumnAt(i);
            if (col.getName().equals(name))
                return col;
        }
        return null;
    }

    /**
     * @see Table#removeColumn(Column)
     */
    public boolean removeColumn(Column c) {
        if (hasTableModelListener()) {
            c.removeChangeListener(this);
        }
        if (remove(c)) {
            fireTableStructureChanged();
            return true;
        }
        return false;
    }
    /**
     * @see infovis.Table#iterator()
     */
    public RowIterator iterator() {
        return new TableIterator(0, getRowCount(), true);
    }

    public RowIterator reverseIterator() {
        return new TableIterator(getRowCount()-1, -1, false);
    }

    /**
     * @see infovis.Table#getTable()
     */
    public Table getTable() {
        return this;
    }
    
    /**
     * @see infovis.Table#isRowValid(int)
     */
    public boolean isRowValid(int row) {
        return row >= 0 && row < getRowCount();
    }


    // interface TableModel

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        return getColumnAt(columnIndex).getName();
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        int max = 0;
        for (int index = 0; index < getColumnCount(); index++) {
            int r = getColumnAt(index).size();
            if (r > max)
                max = r;
        }

        return max;
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return getColumnAt(columnIndex).getValueAt(rowIndex);
    }

    /**
     *  <code>TableModel</code> method for editable tables.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     *
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            getColumnAt(columnIndex).setValueAt(rowIndex, (String)aValue);
        } catch (ParseException e) {
        }
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    // Implementation specific methods

    /**
     * Test if the column is internal, i&dot;e&dot; the first character of its name is a '#'.
     *
     * @param col the column.
     *
     * @return <code>true</code>
     *  if the column is internal, i.e. the first character of its name is a '#'.
     */
    public static boolean isColumnInternal(Column col) {
        return col.getName().charAt(0) == INTERNAL_PREFIX;
    }

    // TableModel implementation
    public boolean hasTableModelListener() {
        return listenerList != null
            && listenerList.getListenerCount(TableModelListener.class) != 0;
    }
    
    public Object getObjectFromRow(int row) {
        return new RowObject(this, row);
    }
    
    public int getRowFromObject(Object obj) {
        return RowObject.getRow(this, obj);
    }

    protected EventListenerList getListenerList() {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        return listenerList;
    }
    
    /**
     * @see javax.swing.table.TableModel#addTableModelListener(TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
        if (!hasTableModelListener()) {
            registerColumnListeners();
        }
        getListenerList().add(TableModelListener.class, l);
    }

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
        if (! hasTableModelListener()) return;
        getListenerList().remove(TableModelListener.class, l);
        if (! hasTableModelListener()) {
            unregisterColumnListeners();
        }
    }

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged(int firstRow, int lastRow, int type) {
        if (! hasTableModelListener()) return;
        fireTableChanged(
                new TableModelEvent(
                        this, 
                        firstRow, 
                        lastRow, 
                        TableModelEvent.ALL_COLUMNS, type));
    }
    
    public void fireTableDataChanged() {
        fireTableDataChanged(0, getRowCount(), UPDATE);
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableStructureChanged() {
        if (! hasTableModelListener()) return;
        fireTableChanged(
                new TableModelEvent(
                        this, 
                        TableModelEvent.HEADER_ROW));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
        if (! hasTableModelListener()) return;
        EventListener[] ll = listenerList.getListeners(TableModelListener.class);
        for (int i = ll.length - 1; i >= 0; i--) {
            TableModelListener l = (TableModelListener)ll[i];
            l.tableChanged(e);
        }
    }
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof Column) {
            Column c = (Column) e.getSource();
            int col = indexOf(c);
            if (hasTableModelListener() && col != -1) {
                fireTableChanged(
                        new TableModelEvent(
                                this, 
                                0, 
                                c.size(), 
                                col,
                                UPDATE));
                return;
            }
        }
    }

    protected void registerColumnListeners() {
        for (int i = 0; i < size(); i++) {
            Column c = getColumnAt(i);
            c.addChangeListener(this);
        }
    }

    protected void unregisterColumnListeners() {
        for (int i = 0; i < size(); i++) {
            Column c = getColumnAt(i);
            c.removeChangeListener(this);
        }
    }
    }
