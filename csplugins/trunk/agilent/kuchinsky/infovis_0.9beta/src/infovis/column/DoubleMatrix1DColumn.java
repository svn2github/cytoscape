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

import java.text.ParseException;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Wrapper for Colt DoubleMatrix1D columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class DoubleMatrix1DColumn extends BasicColumn {
    public static final String MATRIX2D_METADATA = "matrix2d";
    protected DoubleMatrix1D value;
    
    public static void addColumns(Table table, DoubleMatrix2D matrix) {
        for (int i = 0; i < matrix.columns(); i++) {
            DoubleMatrix1DColumn c = new DoubleMatrix1DColumn(
                    Integer.toString(i),
                    matrix.viewColumn(i));
            c.getMetadata().addAttribute(MATRIX2D_METADATA, matrix);
            table.addColumn(c);
        }
    }
    
    public static void addRows(Table table, DoubleMatrix2D matrix) {
        for (int i = 0; i < matrix.rows(); i++) {
            DoubleMatrix1DColumn c = new DoubleMatrix1DColumn(
                    Integer.toString(i),
                    matrix.viewRow(i));
            c.getMetadata().addAttribute(MATRIX2D_METADATA, matrix);
            table.addColumn(c);
        }
    }

    public DoubleMatrix1DColumn(String name, DoubleMatrix1D matrix) {
        super(name);
        this.value= matrix;
    }
    
    public DoubleMatrix1D getValue() {
        return value;
    }
    
    public void clear() {
        readonly();
    }
    
    public void setSize(int newSize) {
        readonly();
    }

    public double get(int index) {
        assert((index >= 0) && (index < size()));
        return value.getQuick(index);
    }

    /**
     * Replaces the element at the specified position in this column
     * with the specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    public void set(int index, double element) {
        assert((index >= 0) && (index < size()));
        value.setQuick(index, element);
        modified(index);
    }
    
    public void fill(double val) {
        value.assign(val);
        modified();
    }
    
//    public void copyFrom(Column from) {
//        if (from instanceof DoubleMatrix1DColumn) {
//            DoubleMatrix1DColumn new_from = (DoubleMatrix1DColumn)from;
//            value.assign(new_from.value);
//            modified();
//        }
//        else {
//            super.copyFrom(from);
//        }
//    }


    /**
     * Parse a string and return the value for the column.
     *
     * @param v the string representation of the value
     *
     * @return the value
     *
     * @throws ParseException if the value cannot be parsed
     */
    public double parse(String v) throws ParseException {
        if (v == null)
            throw new ParseException("null value as a double", 0);
        try {
            if (getFormat() != null) {
                return ((Number) getFormat().parseObject(v))
                    .doubleValue();
            }

            return Double.parseDouble(v);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /**
     * Returns the string representation of a value according to the current format.
     *
     * @param v the value
     *
     * @return the string representation.
     */
    public String format(double v) {
        if (getFormat() != null) {
            return getFormat().format(new Double(v));
        }

        return Double.toString(v);
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
        set(i, parse(v));
    }
    
    public Class getValueClass() {
        return Double.class;
    }

    public boolean isValueUndefined(int i) {
        return false;
    }

    public void setValueUndefined(int i, boolean undef) {
        readonly();
    }

    public boolean hasUndefinedValue() {
        return false;
    }

    public int size() {
        return value.size();
    }

    public void ensureCapacity(int minCapacity) {
        value.ensureCapacity(minCapacity);
    }

    public int capacity() {
        return value.size();
    }

    
    /**
     * Returns a column as a <code>DoubleMatrix1DColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param index index in the <code>Table</code>
     *
     * @return a <code>DoubleMatrix1DColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>DoubleMatrix1DColumn</code>.
     */
    public static DoubleMatrix1DColumn getColumn(Table t, int index) {
        Column c = t.getColumnAt(index);

        if (c instanceof DoubleColumn) {
            return (DoubleMatrix1DColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a column as a <code>DoubleColumn</code> from a
     * <code>Table</code>.
     *
     * @param t the <code>Table</code>
     * @param name the column name.
     *
     * @return a <code>DoubleColumn</code> or null if no such column
     *         exists or the column is not a
     *         <code>DoubleColumn</code>.
     */
    public static DoubleColumn getColumn(Table t, String name) {
        Column c = t.getColumn(name);

        if (c instanceof DoubleColumn) {
            return (DoubleColumn) c;
        }
        else {
            return null;
        }
    }

    /**
     * @see infovis.column.NumberColumn#getDoubleAt(int)
     */
    public double getDoubleAt(int row) {
        return get(row);
    }

    /**
     * @see infovis.column.NumberColumn#getIntAt(int)
     */
    public int getIntAt(int row) {
        return (int) get(row);
    }

    /**
     * @see infovis.column.NumberColumn#getLongAt(int)
     */
    public long getLongAt(int row) {
        return (long) get(row);
    }

    /**
     * @see infovis.column.NumberColumn#setDoubleAt(int, double)
     */
    public void setDoubleAt(int row, double v) {
        set(row, v);
    }

    public double round(double value) {
        return value;
    }


    /**
     * Returns the array of double values copied from the column.
     *
     * @param a an array of double with at least the column size
     *  or <code>null</code>.
     *
     * @return the array of doubles values copied from the column.
     */
    public double[] toArray(double[] a) {
        if (a == null) {
            a = new double[size()];
        }
        value.toArray(a);
        return a;
    }
    
    /**
     * Returns the array of double values used by the column.
     * 
     * @return the array of double values used by the column.
     */
    public double[] toArray() {
        return value.toArray();
    }
    

}
