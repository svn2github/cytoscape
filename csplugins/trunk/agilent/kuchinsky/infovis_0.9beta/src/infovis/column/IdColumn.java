/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import java.text.ParseException;

/**
 * Class IdColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.19 $
 */
public class IdColumn extends ConstantColumn {
    protected int size;
    
    public IdColumn(int size) {
        this.size = size;
    }
    
    public IdColumn() {
        this(0);
    }
    
    public String getName() {
        return "#id";
    }
    
    public int getMinIndex() {
        return 0;
    }
    
    public int getMaxIndex() {
        return size()-1;
    }
    
    public int get(int row) {
        return row;
    }
    
    public void set(int row) {
        if (row >= size) {
            size = Math.max(size, row+1);
        }
    }

    public double getDoubleAt(int row) {
        return get(row);
    }

    public double round(double value) {
        return Math.round(value);
    }

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
    public int parse(String v) throws ParseException {
        if (v == null) {
            //throw new ParseException("null string as in int", 0);
            return 0;
        }
        try {
            if (getFormat() != null) {
                return ((Number) getFormat().parseObject(v)).intValue();
            }

            return Integer.parseInt(v);
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
    public String format(int v) {
        if (getFormat() != null) {
            return getFormat().format(new Integer(v));
        }

        return Integer.toString(v);
    }

    public int size() {
        return size;
    }
    
    public boolean hasUndefinedValue() {
        return false;
    }

    public Class getValueClass() {
        return Integer.class;
    }

    public boolean isValueUndefined(int i) {
        return i < 0 || i >= size;
    }

    public int lastValidRow() {
        return size-1;
    }

    public void setValueAt(int index, String element)
        throws ParseException {
        set(index);
    }

    public void setValueUndefined(int i, boolean undef) {
        set(i);
    }

    public int compare(int row1, int row2) {
        return row1 - row2;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int i) {
        if (size != i) {
            size = i;
        }
    }

}
