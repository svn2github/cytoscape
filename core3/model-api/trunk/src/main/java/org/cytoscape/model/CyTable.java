/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.model;

import java.util.List;
import java.util.Map;


/** 
 * A simple representation of a table object consisting of rows
 * and columns. Columns have names and specific types and rows
 * contain the data for a specific index.
 */
public interface CyTable extends Identifiable {
	/**
	 * A public CyTable is a table that is accessible to the user through the user
	 * interface.  Private or non-public CyTables will not be visible to the user from the
	 * normal user interface, although they will be accessible to plugin writers through the API.
	 *
	 * @return Whether or not this CyTable should be publicly accessible.
	 */
	boolean isPublic();

	/**
	 * Returns a human readable name for the CyTable.
	 * @return A human readable name for the CyTable.
	 */
	String getTitle();

	/**
	 * Allows the title of the table to be set. The title is meant to be
	 * human readable and suitable for use in a user interface.
	 * @param title The human readable title for the CyTable suitable for use in a user
	 *        interface.
	 */
	void setTitle(String title);

	/**
	 * Returns the name of the primary key column for this table.
	 * @return The name of the primary key column for this table.
	 */
	String getPrimaryKey();

	/**
	 * Returns the class type of the primary key column for this table.
	 * @return The class type of the primary key column for this table.
	 */
	Class<?> getPrimaryKeyType();

	/**
	 * The keySet of this map defines all columns in the CyTable and the
	 * the values of this map define the types of the columns.
	 * @return A map of column names to the {@link Class} objects that defines
	 * the column type.
	 */
	Map<String, Class<?>> getColumnTypeMap();

	/**
	 * Returns the Class object that represents the element type for the List type of
	 * column identified by "columnName".
	 * @param columnName The name of the column to check.
	 * @return The Class object that is defined for this column. Will
	 * Since a call to this method only makes sense if the column type is some kind of List,
	 * we throw and exception if this is called on a column that is not of type List!
	 */
	Class<?> getListElementType(String columnName);

	/**
	 * Will delete the column of the specified name.
	 * @param columnName The name identifying the attribute.
	 */
	void deleteColumn(String columnName);

	/**
	 * Create a column of the specified name and the specified type.
	 * @param columnName The name identifying the attribute.
	 * @param type The type of the column.
	 */
	<T> void createColumn(String columnName, Class<?extends T> type);

	/**
	 * Create a column of Lists with the specified name and the specified element type.
	 * @param columnName The name identifying the attribute.
	 * @param listElementType The type of the elements of the list.
	 */
	<T> void createListColumn(String columnName, Class<T> listElementType);

	/**
	 * Returns the list of all values contained in the specified column.
	 * @param columnName The name identifying the attribute.
	 * @param type The type of the column.
	 * @return the list of all values contained in the specified column.
	 */
	<T> List<T> getColumnValues(String columnName, Class<?extends T> type);

	/**
	 * Returns the row specified by the primary key object and if a row
	 * for the specified key does not yet exist in the table, a new row
	 * will be created and the new row will be returned.
	 * @param primaryKey The primary key index of the row to return.
	 * @return The {@link CyRow} identified by the specified key or a new
	 * row identified by the key if one did not already exist.
	 */
	CyRow getRow(Object primaryKey);

	/**
	 * Return a list of all the rows stored in this data table.
	 * @return a list of all the rows stored in this data table.
	 */
	List<CyRow> getAllRows();

	/**
	 * Returns a descriptive message for certain internal errors.  Please
	 * note that only the very last message will be retrieved.
	 * @return if available, a message describing an internal error, otherwise null
	 */
	String getLastInternalError();
}
