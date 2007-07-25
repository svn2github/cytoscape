/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis;

import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;

import javax.swing.event.ChangeListener;


/**
 * A column is an indexed collection of values of homogeneous type.
 *
 * <p>Values can also be undefined, which is useful for data sets
 * with missing values.</p>
 * 
 * <p>A column has a name, can contain metadata and user properties,
 * and triggers notification when its contents is changed.  By
 * convention,
 * when the name of a column starts with a '#' character (sharp sign),
 * the column is considered "internal", i.e not user supplied.
 * 
 * <p>The interface defines columns as a collection of Strings.  However,
 * the intent is that every concrete column has to provide a textual
 * representation for the data type it contains.  The translation from
 * the concrete data type to the string and parsing of a string 
 * representation to the concrete type is controlled by a 
 * {@link java.text.Format}.
 *
 * <p>Each column class containing objects of type TYPE implements
 * type-specific methods with the same names (Java has no true
 * parametric
 * types so this has to be hand made). These methods are: <dl>
 * <dt><code>TYPE get(int index)</code>
 * 
 * <dd>Returns the element at the specified position in this column.
 *
 * <dt><code>void set(int index, TYPE element)</code>
 *
 * <dd>Replaces the element at the specified position in this column
 * with the specified element.
 *
 * <dt><code>void setExtend(int index, TYPE element)</code>
 *
 * <dd>Replaces the element at the specified position in this column
 * with the specified element, growing the column if necessary.
 *
 * <dt><code>void add(TYPE element)</code>
 * <dd>Add a new element in the column.
  *
 * <dt><code>TYPE parse(String v) throws ParseException</code>
 * <dd>Parse a string and return the value for the column.
 *
 * <dt><code>String format(TYPE v)</code>
 *
 * <dd>Returns the string representation of a value according to the
 * current format.
 *
 * <dt><code>static TYPEColumn getColumn(Table t, String name)</code>
 *
 * <dd>Returns a column of type TYPE from a {@link infovis.Table}
 * or <code>null</code> if none exist.
 *
 * <dt><code>TYPEColumn getColumn(Table t, int index)</code>
 *
 * <dd>Return a column with the concrete type from an
 * {@link infovis.Table}.
 * 
 * <dt><code>TYPEColumn findColumn(Table t, String name)</code>
 *
 * <dd>Return a column with the concrete type from an
 * {@link infovis.Table}, creating it if needed.  </dl> </p>
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.41 $
 */
public interface Column extends Metadata, RowComparator, Serializable {
    /**
     * Returns the column name.
     * @return the column name.
     */
    String getName();

    /**
     * Sets the column name.
     * @param name The name to set
     */
    void setName(String name);

    /**
     * Returns true if the column name starts with a '#'.
     *
     * @return true if the column name starts with a '#'.
     */
    boolean isInternal();

    /**
     * Returns true if the ith value is undefined.
     *
     * @param i the index
     * @return true if the ith value is undefined.
     */
    boolean isValueUndefined(int i);

    /**
     * Sets the ith element to have an undefined value.
     *
     * @param i the index
     * @param undef true if the value should be undefined,
     *                 false otherwise.
     */
    void setValueUndefined(int i, boolean undef);

    /**
     * Returns <code>true</code> if the columns contains
     * undefined values, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if the columns contains
     * undefined values, <code>false</code> otherwise.
     */
    boolean hasUndefinedValue();
    
    /**
     * Returns the format.
     * @return Format
     */
    Format getFormat();

    /**
     * Sets the format.
     * @param format The format to set
     */
    void setFormat(Format format);

    /**
     * Returns <tt>true</tt> if this column contains no elements.
     *
     * @return <tt>true</tt> if this column contains no elements.
     */
    boolean isEmpty();

    /**
     * Returns the number of elements in this column.
     *
     * @return the number of elements in this column.
     */
    int size();
    
    /**
     * Sets the size of this column.
     * 
     * <p>Some column will ignore this request.
     * 
     * @param newSize the new size.
     */
    
    void setSize(int newSize);

    /**
     * Removes all of the elements from this column.  The Column will
     * be empty after this call returns.
     *
     * WARNING: if there is an associated format that maintains a
     * status, such as a {@link infovis.column.format.CategoricalFormat}, it
     * is not cleared so results may not be what you expected.  The
     * <code>CategoricalFormat</code> can be cleared explicitely if
     * needed.
     */
    void clear();
    
//    /**
//     * Copies the values of a specified column into the column
//     * 
//     * @param from the column to copy from
//     */
//    void copyFrom(Column from);

    /**
     * Increases the capacity of this column, if necessary, to ensure
     * that it can hold at least the number of values specified by the
     * minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity.
     */
    void ensureCapacity(int minCapacity);

    /**
     * Returns the current capacity of this column.
     *
     * @return  the current capacity .
     */
    int capacity();

    /**
     * Returns the String representation of the element at the
     * specified position in this column or <code>null</code> if 
     * the element is undefined.  The representation is
     * generated by the current format.
     *
     * @param index index of element representation to return.
     * 
     * @return the String representation of element at the specified position 
     * in this column or <code>null</code> if the element is undefined.
     */
    String getValueAt(int index);

    /**
     * Replaces the element at the specified position in this column
     * with the element specified in its String representation or
     * sets if undefined if the element is <code>null</code>
     * 
     * It will be read and translated using the Format.  A null value
     * sets the value to undefined.  The column is extended if
     * needed.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     *
     * @exception ParseException if the specified string cannot be parsed.
     */
    void setValueAt(int index, String element)
	throws ParseException;

    /**
     * Replaces the element at the specified position in this column
     * with the element specified in its String representation or set
     * it undefined if the String cannot be parsed.
     *
     * @param index index of element to set
     * @param v element to be appended to this column.
     * 
     * @return true if the element has been set, false if it has been set undefined.
     *
     */
    boolean setValueOrNullAt(int index, String v);
    
    /**
     * Appends the element specified in its String representation to
     * the end of this column.  
     * 
     * <p><code>col.addValue(a)</code> is equivalent to 
     * <code>col.setValueAt(col.size(), a)</code>.
     *
     * @param v element to be appended to this column.
     *
     * @exception ParseException if the specified string
     *            cannot be parsed.
     */
    void addValue(String v) throws ParseException;

    /**
     * Appends the element specified in its String representation to
     * the end of this column or adds an undefined object if the
     * String cannot be parsed.
     * 
     * <p><code>col.addValueOrNull(a)</code> is equivalent to 
     * <code>col.setValueAtOrNull(col.size(), a)</code>.
     *
     * @param v element to be appended to this column.
     * @return true if the value has been defined, false otherwise.
     *
     */
    boolean addValueOrNull(String v);

    /**
     * Returns an the index of a row containing the minimum value
     * of this column or -1 if the column has only undefined values
     * or is empty. 
     *
     * @return the index of a row containing the minium value
     * of this column. 
     */
    int getMinIndex();

    /**
     * Returns an the index of a row containing the maximum value
     * of this column or -1 if the column has only undefined values
     * or is empty. 
     *
     * @return the index of a row containing the maximum value
     * of this column. 
     */
    int getMaxIndex();

    /**
     * Returns the class of the elements.
     *
     * @return the class of the elements.
     */
    Class getValueClass();

    /**
     * Disables notification until enableNotify has been called.
     * 
     * <p>This method is useful if a large number of modifications is
     * going to happen on the column and notifying each time would be
     * too time consuming.  The notification will be deferred until
     * enableNotify is called.</p>
     * 
     * <p>Calls to disableNotify can be nested</p>
     * @see #enableNotify()
     */
    void disableNotify();

    /**
     * Re enable notifications, triggering eventListeners if
     * modifications occur.
     * 
     * @see #disableNotify()
     */
    void enableNotify();

    /**
     * Adds a listener to the list that's notified each time a change occurs.
     *
     * @param listener the listener
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes a listener from the list that's notified each time a change occurs.
     *
     * @param listener the listener
     */
    void removeChangeListener(ChangeListener listener);

    /**
     * Returns a RowIterator over all the valid rows of this column.
     * 
     * @return a RowIterator over all the valid rows of this column.
     */
    RowIterator iterator();
}
