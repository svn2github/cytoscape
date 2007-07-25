/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.column.event.ColumnChangeEvent;
import infovis.metadata.DependencyMetadata;
import infovis.utils.RowIterator;

import java.text.Format;
import java.text.ParseException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.MutableAttributeSet;

/**
 * Column forwarding all its methods to a backing column.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class ColumnProxy extends AbstractColumn implements ChangeListener {
    protected Column column;
    
    /**
     * Creates a ColumnProxy.
     * @param column the backing column.
     */
    public ColumnProxy(Column column) {
        this.column = column;
        DependencyMetadata.addDependentColumn(column, this);
        column.addChangeListener(this);
    }
    
    /**
     * Releases all the resources maintained by this proxy column.
     */
    public void dispose() {
        DependencyMetadata.removeDependentColumn(column, this);
        column.removeChangeListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e instanceof ColumnChangeEvent) {
            ColumnChangeEvent cole = (ColumnChangeEvent) e;
            modified(cole.getDetail());
        }
        else {
            modified();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void addValue(String v) throws ParseException {
        column.addValue(v);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addValueOrNull(String v) {
        return column.addValueOrNull(v);
    }
    
//    public void copyFrom(Column from) {
//        column.copyFrom(from);
//    }
    
    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return column.capacity();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        column.clear();
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        return column.compare(row1, row2);
    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
        column.ensureCapacity(minCapacity);
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getClientProperty() {
        return column.getClientProperty();
    }

    /**
     * {@inheritDoc}
     */
    public Format getFormat() {
        return column.getFormat();
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getMetadata() {
        return column.getMetadata();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return column.getName();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return column.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        column.setSize(newSize);
    }

    /**
     * {@inheritDoc}
     */
    public String getValueAt(int index) {
        return column.getValueAt(index);
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return column.getValueClass();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return column.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInternal() {
        return column.isInternal();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int row) {
        return column.isValueUndefined(row);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return column.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void setFormat(Format format) {
        column.setFormat(format);
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        column.setName(name);
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element)
        throws ParseException {
        column.setValueAt(index, element);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setValueOrNullAt(int index, String v) {
        return column.setValueOrNullAt(index, v);
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
        column.setValueUndefined(i, undef);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMaxIndex() {
        return column.getMaxIndex();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMinIndex() {
        return column.getMinIndex();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasUndefinedValue() {
        return column.hasUndefinedValue();
    }
    
}
