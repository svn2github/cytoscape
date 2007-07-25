/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.*;
import infovis.column.BooleanColumn;

import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Table Model for displaying the details of the selected rows of a
 * visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class DetailTable 
    implements TableModel, TableModelListener, ChangeListener {
    protected Table         table;
    protected BooleanColumn selection;
    protected ArrayList     listenerList;

    /**
     * Constructor for DetailTable.
     * 
     * Build a table showing all the attributes of selected rows.
     * 
     * @param table the specified <code>Table</code> to manage
     * @param selection the column containing the selected items to manage in the table
     */
    public DetailTable(Table table, BooleanColumn selection) {
        this.table = table;
        if (selection == null) {
            selection = BooleanColumn.findColumn(
                    table,
                    Visualization.VISUAL_SELECTION);
        }
        this.selection = selection;
    }
    
    /**
     * Resize a specified JTable column to fit its natural size.
     * 
     * Asserts that the table only contains strings.
     * 
     * @param table the JTable
     * @param i the column 
     */
    public static void sizeColumn(JTable table, int i) {
        TableColumn column = table.getColumnModel().getColumn(i);
        FontMetrics fm = table.getFontMetrics(table.getFont());
        int width = 75;
        for (int row = 0; row < table.getRowCount(); row++) {
            String s = (String)table.getModel().getValueAt(row, i);
            if (s != null)
                width = Math.max(width, SwingUtilities.computeStringWidth(fm,s)+2);
        }
        column.setWidth(width);
        column.setPreferredWidth(width);
        
    }
    
    /**
     * Creates a scrollable <code>JTable</code> with convenient default
     * setting for showing the selected items of a specified InfoVis 
     * <code>Table</code>.
     * 
     * <p>Gets updated when the selection changes.
     *  
     * @param table the specified <code>Table</code>
     * @param selection the selection column
     * 
     * @return A <code>JScrollPane</code> containing a <code>JTable</code>
     * showing the details of the items selected in the table.
     */
    public static JScrollPane createDetailJTable(
            final Table table,
            BooleanColumn selection) {
        DetailTable dt = new DetailTable(table, selection);        
        final JTable jtable = new JTable(dt);
        final TableColumnModel tcm = jtable.getColumnModel();
        tcm.addColumnModelListener(
                new TableColumnModelListener() {
                    {
                        for (int i = 0; i < tcm.getColumnCount(); i++) {
                            sizeColumn(jtable, i);
                        }
                    }
                    public void columnMarginChanged(ChangeEvent e) {}
                    public void columnSelectionChanged(ListSelectionEvent e) {}
                    public void columnAdded(TableColumnModelEvent e) {
                        for (int i = e.getFromIndex(); i <= e.getToIndex(); i++) {
                            sizeColumn(jtable, i);
                        }
                    }
                    public void columnMoved(TableColumnModelEvent e) { }
                    public void columnRemoved(TableColumnModelEvent e) { }
                });
        // Force tooltips
        jtable.setDefaultRenderer(
                String.class, 
                new DefaultTableCellRenderer() {
            public String getToolTipText() {
                return getText();
            }

            public String getToolTipText(MouseEvent event) {
                return getToolTipText();
            }
        });
        
        // jtable.setToolTipText("Attribute values for selected items");
        jtable.setPreferredScrollableViewportSize(jtable.getPreferredSize());
        jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // jtable.setPreferredScrollableViewportSize(new Dimension(450, 200));
        JScrollPane jscroll = new JScrollPane(jtable);
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ArrayList labels = new ArrayList(table.getColumnCount());
                for (int i = 0; i < table.getColumnCount(); i++) {
                    labels.add(table.getColumnAt(i).getName());
                }
                TableRowHeader.setRowHeader(jtable, labels);
            }
        };
        listener.stateChanged(null);
        table.addChangeListener(listener);
        jscroll.setCorner(
                JScrollPane.UPPER_LEFT_CORNER,
                new TableAttributesHeader(table, selection));
        return jscroll;
    }

    /**
     * @see javax.swing.table.TableModel#addTableModelListener(TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
        if (listenerList == null) {
            listenerList = new ArrayList();
            table.addTableModelListener(this);
            // table implements the TableModel so it
            // propagates UPDATE events.  No need to
            // track change events on the selection
            // column when it is in the table
            if (table.indexOf(selection) == -1) {
                selection.addChangeListener(this);
            }
        }
        listenerList.add(l);
    }

    /**
     * @see javax.swing.table.TableModel#removeTableModelListener(TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
        listenerList.remove(l);
        if (listenerList.size() == 0) {
            listenerList = null;
            if (table.indexOf(selection) == -1) {
                selection.removeChangeListener(this);
            }
            table.removeTableModelListener(this);
        }
    }

    /**
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return Math.max(selection.getSelectedCount(), 1);
    }

    /**
     * Returns the row of the InfoVis column corresponding to the specified
     * column index for the JTable
     * 
     * @param columnIndex
     *            the column index in the JTable
     * 
     * @return the row of the InfoVis column
     */
    public int getRowAt(int columnIndex) {
        for (int index = selection.getMinSelectionIndex(); index <= selection
                .getMaxSelectionIndex(); index++) {
            if (selection.isSelectedIndex(index) && columnIndex-- == 0) {
                return index;
            }
        }
        return -1;
    }

    /**
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex) {
        int index = getRowAt(columnIndex);
        if (index == -1)
            return "No selection";
        return "Row " + index;
    }

    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return table.getColumnCount();
    }

    public Column getColumnAt(int index) {
        return table.getColumnAt(index);
    }

    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        int index = getRowAt(columnIndex);
        if (index == -1) {
            return "";
        }
        else {
            return getColumnAt(rowIndex).getValueAt(index);
        }
    }

    /**
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        // The selection has changed.
        fireTableStructureChanged();
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        // The underlying Table has changed.
        fireTableStructureChanged();
    }

    /**
     * Notifies all listeners that all cell values in the table's rows may have
     * changed. The number of rows may also have changed and the
     * <code>JTable</code> should redraw the table from scratch. The structure
     * of the table (as in the order of the columns) is assumed to be the same.
     * 
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed. The number
     * of columns in the table, and the names and types of the new columns may
     * be different from the previous state. If the <code>JTable</code>
     * receives this event and its <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the sizeModel. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     * 
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered themselves as
     * listeners for this table sizeModel.
     * 
     * @param e
     *            the event to be forwarded
     * 
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see javax.swing.event.EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
        for (int i = listenerList.size() - 1; i >= 0; i--) {
            TableModelListener l = (TableModelListener) listenerList.get(i);
            l.tableChanged(e);
        }
    }
}
