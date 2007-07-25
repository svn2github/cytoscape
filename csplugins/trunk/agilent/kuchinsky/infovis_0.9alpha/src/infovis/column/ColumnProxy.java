/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;
import infovis.metadata.DependencyMetadata;
import infovis.utils.RowIterator;

import java.text.Format;
import java.text.ParseException;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class ColumnProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class ColumnProxy extends AbstractColumn implements ChangeListener {
    protected Column column;
    
    public ColumnProxy(Column column) {
        this.column = column;
        DependencyMetadata.addDependentColumn(column, this);
        column.addChangeListener(this);
    }
    
    public void dispose() {
        DependencyMetadata.removeDependentColumn(column, this);
        column.removeChangeListener(this);
    }
    
    public void stateChanged(ChangeEvent e) {
        fireColumnChanged();
    }

    public void addValue(String v) throws ParseException {
        column.addValue(v);
    }

    public boolean addValueOrNull(String v) {
        return column.addValueOrNull(v);
    }
    
    public void copyFrom(Column from) {
        column.copyFrom(from);
    }


    public int capacity() {
        return column.capacity();
    }

    public void clear() {
        column.clear();
    }

    public int compare(int row1, int row2) {
        return column.compare(row1, row2);
    }

    public void ensureCapacity(int minCapacity) {
        column.ensureCapacity(minCapacity);
    }

    public Map getClientPropery() {
        return column.getClientPropery();
    }

    public Format getFormat() {
        return column.getFormat();
    }

    public Map getMetadata() {
        return column.getMetadata();
    }

    public String getName() {
        return column.getName();
    }

    public int size() {
        return column.size();
    }
    
    public void setSize(int newSize) {
        column.setSize(newSize);
    }

    public String getValueAt(int index) {
        return column.getValueAt(index);
    }

    public Class getValueClass() {
        return column.getValueClass();
    }

    public boolean isEmpty() {
        return column.isEmpty();
    }

    public boolean isInternal() {
        return column.isInternal();
    }

    public boolean isValueUndefined(int row) {
        return column.isValueUndefined(row);
    }

    public RowIterator iterator() {
        return column.iterator();
    }

    public void setFormat(Format format) {
        column.setFormat(format);
    }

    public void setName(String name) {
        column.setName(name);
    }

    public void setValueAt(int index, String element)
        throws ParseException {
        column.setValueAt(index, element);
    }

    public boolean setValueOrNullAt(int index, String v) {
        return column.setValueOrNullAt(index, v);
    }

    public void setValueUndefined(int i, boolean undef) {
        column.setValueUndefined(i, undef);
    }
    
    public int getMaxIndex() {
        return column.getMaxIndex();
    }
    public int getMinIndex() {
        return column.getMinIndex();
    }
    
    public boolean hasUndefinedValue() {
        return column.hasUndefinedValue();
    }
    
}
