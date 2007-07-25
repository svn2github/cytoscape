/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.utils.RowIterator;

import java.text.Format;
import java.text.ParseException;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

import org.apache.log4j.Logger;


/**
 * BasicColumn implements the management of name, Metadata,
 * clientProperty and Format.
 * It also implements generically the copyFrom method.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public abstract class BasicColumn extends AbstractColumn {
    private static final Logger logger = Logger.getLogger(BasicColumn.class);
    /**
     * The column name.
     */
    protected String name;

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
     * Variable used to specify that the min and max
     * values should be recomputed.
     */
    protected transient boolean min_max_updated = false;
    protected transient int minIndex = -1;
    protected transient int maxIndex = -1;
    

    /**
     * Constructor for Column.
     * 
     * @param name the column name
     */
    public BasicColumn(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        min_max_updated = false;
        minIndex = -1;
        maxIndex = -1;
        modified(MODIFIED_ALL);
    }
    /**
     * Returns the name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns true if the column name starts with a '#'.
     *
     * @return true if the column name starts with a '#'.
     */
    public boolean isInternal() {
        return name != null 
            && name.length() != 0
            && name.charAt(0) == '#';
    }

    /**
     * Returns the clientPropery.
     * @return AttributeSet
     */
    public MutableAttributeSet getClientProperty() {
        if (clientProperty == null) {
            clientProperty = new SimpleAttributeSet();
        }
        return clientProperty;
    }

    /**
     * Returns the metadata.
     * @return Map
     */
    public MutableAttributeSet getMetadata() {
        if (metadata == null) {
            metadata = new SimpleAttributeSet();
        }
        return metadata;
    }

    /**
     * Returns the format.
     * @return Format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Sets the format.
     * @param format The format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

    /**
     * Returns <tt>true</tt> if this column contains no elements.
     *
     * @return <tt>true</tt> if this column contains no elements.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Sets the sizeColumn of this column. If the new sizeColumn is
     * greater than the current sizeColumn, new <code>0</code> items
     * are added to the end of the column. If the new sizeColumn is
     * less than the current sizeColumn, all components at index
     * <code>newSize</code> and greater are discarded.
     *
     * @param   newSize   the new sizeColumn of this column.
     */
    public abstract void setSize(int newSize);

    /**
     * {@inheritDoc}
     */
    public final void addValue(String v) throws ParseException {
        setValueAt(size(), v);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setValueOrNullAt(int index, String v) {
        try {
            setValueAt(index, v);
        } catch (ParseException e) {
            setValueUndefined(index, true);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean addValueOrNull(String v) {
        try {
            addValue(v);
        } catch (ParseException e) {
            try {
                addValue(null);
            } catch (ParseException pe) {
                logger.error("Cannot set value to null", pe);
            }
            setValueUndefined(size() - 1, true);
            return false;
        }
        return true;
    }

//    public void copyFrom(Column from) {
//        try {
//            disableNotify();
//            setName(from.getName());
//            for (int i = 0; i < from.size(); i++) {
//                if (from.isValueUndefined(i)) {
//                    setValueUndefined(i, true);
//                }
//                else {
//                    setValueOrNullAt(i, from.getValueAt(i));
//                }
//            }
//        }
//        finally {
//            enableNotify();
//        }
//    }


    /**
     * Returns the class of the elements.
     *
     * @return the class of the elements.
     */
    public abstract Class getValueClass();

    /**
     * Returns the index of the first row which has a defined value
     *  or Integer.MAX_VALUE if there is none.
     *
     * @return the index of the first row which has a defined value
     *  or Integer.MAX_VALUE if there is none.
     */
    public int firstValidRow() {
        int i;
        for (i = 0; i < size(); i++) {
            if (!isValueUndefined(i))
                return i;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the index of the last row which has a defined value
     *  or -1 if there is none.
     *
     * @return the index of the last row which has a defined value
     *  or -1 if there is none.
     */
    public int lastValidRow() {
        int i;
        for (i = size() - 1; i >= 0; i--) {
            if (!isValueUndefined(i))
                return i;
        }
        return i;
    }
    
    /**
     * {@inheritDoc}
     */
    public int compare(int row1, int row2) {
        // Undefined values are always set higher than defined values
        // so that their rank is always greater than defined
        // values.  When both are undefined, they are sorted in
        // ascending row order.
        if (isValueUndefined(row1)) {
            if (isValueUndefined(row2))
                return row1 - row2;
            else
                return 1;
        } else if (isValueUndefined(row2))
            return -1;
        return 0;
    }

    /**
     * Returns a RowIterator over all the valid rows of this column.
     *
     * @return a RowIterator over all the valid rows of this column.
     */
    public RowIterator iterator() {
        return new ColumnIterator(
                this, 
                firstValidRow(), 
                lastValidRow()+1, 
                true);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return getName();
    }
    

    protected void updateMinMax() {
        if (min_max_updated) {
            return;
        }

        minIndex = -1;
        maxIndex = -1;

        for (RowIterator iter = iterator(); iter.hasNext();) {
            int i = iter.nextRow();
            if (minIndex == -1 || compare(i, minIndex) < 0) {
                minIndex = i;
            }

            if (maxIndex == -1 || compare(i, maxIndex) > 0) {
                maxIndex = i;
            }
        }
        min_max_updated = true;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMinIndex() {
        updateMinMax();
        return minIndex;
    }
    
    /**
     * {@inheritDoc}
     */
    public int getMaxIndex() {
        updateMinMax();
        return maxIndex;
    }
}
