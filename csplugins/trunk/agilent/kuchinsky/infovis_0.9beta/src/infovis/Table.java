/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis;

import infovis.utils.RowIterator;

import javax.swing.table.TableModel;


/**
 * The <code>Table</code> is the base class of all infovis dataset
 * containers.  It is basically a {@link infovis.Column} of columns. 
 *
 * <p>An abstract table manages a list of columns.  Each column is an
 * indexed container of homogeneous type such as integers (int),
 * floats or Objects.  Each column has also a name.  By convention,
 * when the name of a column starts with a '#' character (sharp sign),
 * the column is considered "internal", i.e not user supplied.
 * Internal columns are used to manage trees and graphs using a table
 * structure, where the internal structure of the tree or the graph
 * should not be confused with user supplied attributes.</p>
 *
 * <p>Tables, like columns, also manage metadata and client data.
 * Metadata are useful to store information about the nature of the
 * table that could be saved along with the data, i.e.  a textual
 * description of the origin of the data.  Client data is useful to
 * store run-time information associated with the table.</p>
 *
 * <p>Tables contain data in a similar way as database contain data.
 * Creating a simple table containing a name, a birthdate and a salary
 * can be done with the following code:</p>
 * <pre>
 * Table table = new DefaultTable();
 * StringColumn name = new StringColumn("name");
 * table.addColumn(name);
 * DateColumn birthdate = new DateColumn("birthdate");
 * table.addColumn(birthdate);
 * FloatColumn salary = new FloatColumn("salary");
 * table.addColumn(salary);
 * </pre>
 * 
 * <p>Adding new data into the table can be done using the following
 * code:</p>
 * <pre>
 * name.addValue("Doe, John");
 * birthdate.addValue("12 Jan 1980 12:23:00");
 * salary.add(1500);
 * </pre>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.30 $
 */
public interface Table extends Column, Metadata, TableModel {
    /**
     * NIL value for a table index.
     */
    int NIL = -1;
	
    /**
     * Prefixes the name of columns used for internal purposes.
     */
    char INTERNAL_PREFIX = '#';
	
    /**
     * Name of the BooleanColumn managing the selection, if one exists.
     */
    String SELECTION_COLUMN = "#selection";

    /**
     * Name of the LongColumn managing the dynamic filtering, if one exists.
     */	
    String FILTER_COLUMN = "#filter";

    /**
     * Returns the number of columns of the table.
     *
     * @return the number of columns of the table.
     */
    int getColumnCount();
    
    /**
     * Clears the table.
     * 
     * <p>After this method, the table is almost in the same state as if 
     * it had been created afresh except it contains the same columns as before 
     * but they are all cleared.
     * 
     */
    void clear();

    /**
     * Adds a column.
     *
     * @param c the column.
     */
    void addColumn(Column c);

    /**
     * Returns the column at a specified index.
     *
     * @param index the index.
     *
     * @return the column at a specified index
     * or null if the index is out of range.
     */
    Column getColumnAt(int index);

    /**
     * Replaces the column at a specified index.
     *
     * @param i the index.
     * @param c the column.
     */
    void setColumnAt(int i, Column c);

    /**
     * Returns the index of a column of a specified name.
     *
     * @param name the name.
     *
     * @return the index of a column of a specified name
     * 	or -1 if no such column exist.
     */
    int indexOf(String name);
	
    /**
     * Returns the index of a specified column.
     *
     * @param column the column
     *
     * @return the index of a specified column
     * 	or -1 if the column is not in the table.
     */
    int indexOf(Column column);

    /**
     * Returns the column of a specified name.
     *
     * @param name the name.
     *
     * @return the column of a specified name.
     */
    Column getColumn(String name);

    /**
     * Removes a column from the table.
     *
     * @param c the column.
     *
     * @return <code>true</code> if the column has been removed.
     */
    boolean removeColumn(Column c);

    /**
     * Returns the number of rows in the table.
     * 
     * @return the number of rows in the table.
     */
    int getRowCount();

    /**
     * Returns the index of the last row in the table.
     * 
     * @return the index of the last row in the table
     */
    int getLastRow();

    /**
     * Returns an iterator over the columns of this table in reverse order.
     *
     * @return an iterator over the columns of this table in reverse order.
     */
    RowIterator reverseIterator();    
    
    /**
     * Returns the real table for Proxies and this for a concrete table.
     * 
     * @return the real table for Proxies and this for a concrete table.
     */
    Table getTable();
    
    /**
    * Checks whether a specified row is valid.
    *
    * @param row the row.
    * 
    * @return <code>true</code> if it is.
    */
   boolean isRowValid(int row);
}
