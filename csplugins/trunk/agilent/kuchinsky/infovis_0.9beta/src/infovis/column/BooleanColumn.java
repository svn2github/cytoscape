/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.Table;
import infovis.column.format.BooleanFormat;
import infovis.utils.RowFilter;

import java.text.ParseException;
import java.util.BitSet;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cern.colt.list.IntArrayList;

/**
 * Column of booleans.
 * 
 * <p>Implements columns of boolean values, backed on a <code>BitSet</code>.
 * A <code>BooleanColumn</code> also implements the ListSelectionModel so it
 * can be used to control a selection in Swing.
 * 
 * <p>Creation: <code>BooleanColumn c = new BooleanColumn("yesorno");</code>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.42 $
 *
 * @infovis.factory ColumnFactory "boolean" DENSE
 * @infovis.factory ColumnFactory "bool" DENSE
 */
public class BooleanColumn extends LiteralColumn implements
        ListSelectionModel, RowFilter {
    private static final long serialVersionUID = -4800193761758420834L;
    protected int size;
    protected BitSet value;
    // ListSelectionModel
    private int minModified = Integer.MAX_VALUE;
    private int maxModified = -1;
    private int anchorIndex = -1;
    private int leadIndex = -1;
    private boolean isAdjusting = false;

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     */
    public BooleanColumn(String name) {
        this(name, 10);
    }

    /**
     * Creates a new FloatColumn object.
     * 
     * @param name
     *            the column name.
     * @param reserve
     *            the initial capacity.
     */
    public BooleanColumn(String name, int reserve) {
        super(name);

        value = new BitSet(reserve);
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear() {
        if (isEmpty()) return;
        updateMinMaxModified(firstValidRow());
        updateMinMaxModified(lastValidRow());
        value.clear();
        size = 0;
        super.clear();
    }

    /**
     * Returns the element at the specified position in this column.
     * 
     * @param index
     *            index of element to return.
     * 
     * @return the element at the specified position in this column.
     */
    public boolean get(int index) {
        assert ((index >= 0) && (index < size()));
        return value.get(index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     */
    public void set(int index, boolean element) {
        assert ((index >= 0) && (index < size()));
        value.set(index, element);
        updateMinMaxModified(index);
        set(index);
    }
    
    /**
     * Returns the minimum value.
     * @return the minimum value.
     */
    public boolean getMin() {
        return get(getMinIndex());
    }

    /**
     * Returns the maximum value.
     * @return the maximum value.
     */
    public boolean getMax() {
        return get(getMaxIndex());
    }

    private void updateMinMaxModified(int index) {
        minModified = Math.min(minModified, index);
        maxModified = Math.max(maxModified, index);
    }

    /**
     * Replaces the element at the specified position in this column with the
     * specified element, growing the column if necessary.
     * 
     * @param index
     *            index of element to replace.
     * @param element
     *            element to be stored at the specified position.
     */
    public void setExtend(int index, boolean element) {
        assert (index >= 0);
        if (index >= size) {
            if (index == size) {
                size = index+1;
            }
            else
                setSize(index+1);
        }
        set(index, element);
    }
    
    /**
     * Sets a list of indexes with a specified value.
     * 
     * @param list the indexes list
     * @param element the boolean value
     */
    public void setExtend(IntArrayList list, boolean element) {
        for (int i = 0; i < list.size(); i++) {
            int index = list.getQuick(i);
            setExtend(index, element);
        }
    }

    /**
     * Adds a new element in the column.
     * 
     * @param element
     *            the element.
     */
    public final void add(boolean element) {
        setExtend(size(), element);
    }

    /**
     * Fills the column with the specified value.
     * 
     * @param v
     *            the value
     */
    public void fill(boolean v) {
        if (v) {
            value.set(0, size());
        } else {
            value.clear();
        }
        super.fill();
        minModified = 0;
        maxModified = size()-1;
        modified();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        return isValueUndefined(row);
    }

//    public void copyFrom(Column from) {
//        if (from instanceof BooleanColumn) {
//            BooleanColumn new_from = (BooleanColumn) from;
//            value = (BitSet) new_from.value.clone();
//            size = new_from.size;
//            copyUndefinedFrom(new_from);
//            updateMinMaxModified(firstValidRow());
//            updateMinMaxModified(lastValidRow());
//            modified();
//        } else {
//            super.copyFrom(from);
//        }
//    }
    
    /**
     * Parse a string and return the value for the column.
     * 
     * @param v
     *            the string representation of the value
     * 
     * @return the value
     * 
     * @throws ParseException
     *             if the value cannot be parsed
     */
    public boolean parse(String v) throws ParseException {
        if (v == null)
            return false;
        try {
            if (getFormat() != null) {
                return ((Number) getFormat().parseObject(v)).intValue() != 0;
            }

            Object b = BooleanFormat.getInstance().parseObject(v);
            if (b instanceof Boolean) {
                return ((Boolean)b).booleanValue();
            }
            throw new ParseException("Invalid boolean value "+v, 0);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
        
    }

    /**
     * Returns the string representation of a value according to the current
     * format.
     * 
     * @param v
     *            the value
     * 
     * @return the string representation.
     */
    public String format(boolean v) {
        if (getFormat() != null) {
            return getFormat().format(new Boolean(v));
        }

        return Boolean.toString(v);
    }

    /**
     * @see infovis.Column#getValueAt(int)
     */
    public String getValueAt(int i) {
        if (i < 0 || i >= size() || isValueUndefined(i))
            return null;
        return format(get(i));
    }

    /**
     * @see infovis.Column#setValueAt(int, String)
     */
    public void setValueAt(int i, String v) throws ParseException {
        if (v == null) {
            setValueUndefined(i, true);
        }
        else {
            setExtend(i, parse(v));
        }
    }

    /**
     * Returns a column as a <code>FloatColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param index
     *            index in the <code>Table</code>
     * 
     * @return a <code>FloatColumn</code> or null if no such column exists or
     *         the column is not a <code>FloatColumn</code>.
     */
    public static BooleanColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof BooleanColumn) {
            return (BooleanColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>BooleanColumn</code> from a
     * <code>Table</code>.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a <code>BooleanColumn</code> or null if no such column exists
     *         or the column is not a <code>BooleanColumn</code>.
     */
    public static BooleanColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof BooleanColumn) {
            return (BooleanColumn) c;
        } else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>FloatColumn</code> from a table, creating
     * it if needed.
     * 
     * @param t
     *            the <code>Table</code>
     * @param name
     *            the column name.
     * 
     * @return a column as a <code>FloatColumn</code> from a table,
     */
    public static BooleanColumn findColumn(Table t, String name) {
        Column c = t.getColumn(name);
        if (c == null) {
            c = new BooleanColumn(name);
            t.addColumn(c);
        }
        return (BooleanColumn) c;
    }

    /**
     * @see infovis.Column#getValueClass()
     */
    public Class getValueClass() {
        return Boolean.class;
    }

    /**
     * @see infovis.Column#compare(int,int)
     */
    public int compare(int row1, int row2) {
        int ret = super.compare(row1, row2);
        if (ret != 0)
            return ret;
        int v1 = get(row1) ? 1 : 0;
        int v2 = get(row2) ? 1 : 0;

        return v1 - v2;
    }

    /**
     * @see javax.swing.ListSelectionModel#addListSelectionListener(ListSelectionListener)
     */
    public void addListSelectionListener(ListSelectionListener x) {
        getEventListenerList().add(ListSelectionListener.class, x);
    }

    /**
     * @see javax.swing.ListSelectionModel#addSelectionInterval(int, int)
     */
    public void addSelectionInterval(int index0, int index1) {
        anchorIndex = index0;
        leadIndex = index1;
        try {
            disableNotify();
            while (index0 <= index1) {
                setExtend(index0, true);
                index0++;
            }
        } finally {
            enableNotify();
        }
    }

    /**
     * @see javax.swing.ListSelectionModel#clearSelection()
     */
    public void clearSelection() {
        anchorIndex = -1;
        leadIndex = -1;
        clear();
    }

    /**
     * @see javax.swing.ListSelectionModel#getAnchorSelectionIndex()
     */
    public int getAnchorSelectionIndex() {
        return anchorIndex;
    }

    /**
     * @see javax.swing.ListSelectionModel#getLeadSelectionIndex()
     */
    public int getLeadSelectionIndex() {
        return leadIndex;
    }

    /**
     * @see javax.swing.ListSelectionModel#getMaxSelectionIndex()
     */
    public int getMaxSelectionIndex() {
        return lastValidRow();
    }

    /**
     * @see javax.swing.ListSelectionModel#getMinSelectionIndex()
     */
    public int getMinSelectionIndex() {
        return firstValidRow();
    }

    /**
     * @see javax.swing.ListSelectionModel#getSelectionMode()
     */
    public int getSelectionMode() {
        return MULTIPLE_INTERVAL_SELECTION;
    }

    /**
     * @see javax.swing.ListSelectionModel#getValueIsAdjusting()
     */
    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }

    /**
     * @see javax.swing.ListSelectionModel#insertIndexInterval(int, int,
     *      boolean)
     */
    public void insertIndexInterval(int index, int length,
            boolean before) {
    }

    /**
     * @see javax.swing.ListSelectionModel#isSelectedIndex(int)
     */
    public boolean isSelectedIndex(int index) {
        return !isFiltered(index);
    }

    /**
     * @see javax.swing.ListSelectionModel#isSelectionEmpty()
     */
    public boolean isSelectionEmpty() {
        return isEmpty();
    }

    /**
     * Returns the number of selected items.
     * 
     * @return the number of selected items.
     */
    public int getSelectedCount() {
        int cnt = 0;
        for (int i = getMinSelectionIndex(); i <= getMaxSelectionIndex(); i++) {
            if (isSelectedIndex(i))
                cnt++;
        }
        return cnt;
    }

    /**
     * @see javax.swing.ListSelectionModel#removeIndexInterval(int, int)
     */
    public void removeIndexInterval(int index0, int index1) {
    }

    /**
     * @see javax.swing.ListSelectionModel#removeListSelectionListener(ListSelectionListener)
     */
    public void removeListSelectionListener(ListSelectionListener x) {
        eventListenerList.remove(ListSelectionListener.class, x);
    }

    /**
     * @see javax.swing.ListSelectionModel#removeSelectionInterval(int, int)
     */
    public void removeSelectionInterval(int index0, int index1) {
        while (index0 <= index1) {
            setValueUndefined(index0, true);
            index0++;
        }
    }

    /**
     * @see javax.swing.ListSelectionModel#setAnchorSelectionIndex(int)
     */
    public void setAnchorSelectionIndex(int index) {
        anchorIndex = index;
    }

    /**
     * @see javax.swing.ListSelectionModel#setLeadSelectionIndex(int)
     */
    public void setLeadSelectionIndex(int index) {
        leadIndex = index;
    }

    /**
     * @see javax.swing.ListSelectionModel#setSelectionInterval(int, int)
     */
    public void setSelectionInterval(int index0, int index1) {
        clear();
        addSelectionInterval(index0, index1);
    }

    /**
     * @see javax.swing.ListSelectionModel#setSelectionMode(int)
     */
    public void setSelectionMode(int selectionMode) {
    }

    /**
     * @see javax.swing.ListSelectionModel#setValueIsAdjusting(boolean)
     */
    public void setValueIsAdjusting(boolean valueIsAdjusting) {
        this.isAdjusting = valueIsAdjusting;
    }

    private void fireValueChanged() {
        if (maxModified == -1 || eventListenerList == null) {
            return;
        }
        int firstChanged = minModified;
        int lastChanged = maxModified;
        minModified = Integer.MAX_VALUE;
        maxModified = -1;
        Object[] listeners = eventListenerList.getListenerList();
        ListSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(this, firstChanged,
                            lastChanged, isAdjusting);
                }
                ((ListSelectionListener) listeners[i + 1])
                        .valueChanged(e);
            }
        }
    }
    
    protected void fireChanged() {
        fireValueChanged();
        super.fireChanged();
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        return get(row) ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
        set(row, v != 0);
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        boolean v = (value != 0);
        return format(v);
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return value.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
    }
    

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (newSize == size) return;
        try {
            disableNotify();
            super.setSize(newSize);
            size = newSize;
        }
        finally {
            enableNotify();
        }
    }
    
    /**
     * Returns the backing BitSet of this column (USE WITH CARE).
     * @return the backing BitSet of this column.
     */
    public BitSet getValueReference() {
        return value;
    }
   
}