/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowIterator;
import infovis.utils.TableIterator;

import java.text.Format;
import java.text.ParseException;

import javax.swing.event.ChangeListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * 
 * Column containing constant values for each rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ConstantColumn implements NumberColumn {
    /**
     * Metadata associated with this column.
     */
    protected MutableAttributeSet metadata;

    /**
     * User's client properties.
     */
    protected transient MutableAttributeSet clientProperty;

    /**
     * Format used to convert from the internal representation to a
     * comprehensible readable user's representation.  When null, a
     * default format is used.
     */
    protected Format format;
    
    /**
     * {@inheritDoc}
     */
    public int getIntAt(int row) {
        return (int)getDoubleAt(row);
    }

    /**
     * {@inheritDoc}
     */
    public float getFloatAt(int row) {
        return (float)getDoubleAt(row);
    }

    /**
     * {@inheritDoc}
     */
    public long getLongAt(int row) {
        return (long)getDoubleAt(row);
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleAt(int row) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void setIntAt(int row, int v) {
    }

    /**
     * {@inheritDoc}
     */
    public void setFloatAt(int row, float v) {
    }

    /**
     * {@inheritDoc}
     */
    public void setLongAt(int row, long v) {
    }

    /**
     * {@inheritDoc}
     */
    public void setDoubleAt(int row, double v) {
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMin() {
        return getDoubleAt(getMinIndex());
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleMax() {
        return getDoubleAt(getMaxIndex());
    }

    /**
     * {@inheritDoc}
     */
    public double round(double value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public String format(double value) {
        if (format != null) {
            return format.format(new Double(value));
        }
        return ""+value;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "#constant";
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInternal() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueUndefined(int i) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setValueUndefined(int i, boolean undef) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasUndefinedValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Format getFormat() {
        return format;
    }

    /**
     * {@inheritDoc}
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        metadata = null;
        clientProperty = null;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public void copyFrom(Column from) {
//    }

    /**
     * {@inheritDoc}
     */
    public void ensureCapacity(int minCapacity) {
    }

    /**
     * {@inheritDoc}
     */
    public int capacity() {
        return Integer.MAX_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public String getValueAt(int index) {
        return format(getDoubleAt(index));
    }

    /**
     * {@inheritDoc}
     */
    public void setValueAt(int index, String element) throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    public boolean setValueOrNullAt(int index, String v) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void addValue(String v) throws ParseException {
    }

    /**
     * {@inheritDoc}
     */
    public boolean addValueOrNull(String v) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int getMinIndex() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxIndex() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Class getValueClass() {
        return Double.class;
    }

    /**
     * {@inheritDoc}
     */
    public void disableNotify() {
    }

    /**
     * {@inheritDoc}
     */
    public void enableNotify() {
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(ChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public void removeChangeListener(ChangeListener listener) {
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return new TableIterator(0, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getMetadata() {
        if (metadata == null) {
            metadata = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public MutableAttributeSet getClientProperty() {
        if (clientProperty == null) {
            clientProperty = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(int arg0, int arg1) {
        return 0;
    }

}
