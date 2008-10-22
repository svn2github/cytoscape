
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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
 * 
 */
public interface CyDataTable {
	/**
	 * By default all {@link CyRow}s created have a primary key column of type {@link Integer} 
	 * that gets created at initialization which is identified by this string. If the
	 * CyDataTable is created and immediately bound to a {@link CyNetwork} then the primary key
	 * attribute is populated with the SUID of the  {@link GraphObject}.
	 */
	String PRIMARY_KEY = "AID";

	/**
	 * A public CyDataTable is a table that is accessible to the user through the user
	 * interface.  Private or non-public CyDataTables will not be visible to the user from the
	 * normal user interface, although they will be accessible to plugin writers through the API.
	 *
	 * @return Whether or not this CyDataTable should be publicly accessible.
	 */
	boolean isPublic();

	/**
	 * @return The session unique identifier.
	 */
	long getSUID();

	/**
	 * @return A human readable name for the CyDataTable.
	 */
	String getTitle();

	/**
	 * 
	 * @param title The human readable title for the CyDataTable suitable for use in a user
	 *        interface.
	 */
	void setTitle(String title);

	/**
     * The keySet of this map defines all columns in the CyDataTable and the
     * the values of this map define the types of the columns.
	 *
     * @return A map of column names to the {@link Class} objects that defines
     * the column type.
     */
	Map<String, Class<?>> getColumnTypeMap();

	/**
	 * @param columnName The name identifying the attribute.
	 */
	void deleteColumn(String columnName);

	/**
	 * @param columnName The name identifying the attribute.
	 * @param type The type of the column.
	 * @param unique Whether the values contained in the column must be unique  
	 */
	<T> void createColumn(String columnName, Class<?extends T> type, boolean unique);

	/**
     * Unique columns can be used to map the values from one CyDataTable to another.
     * @return A list of column names where the values within the column are
     * guaranteed to be unique. 
     */
	List<String> getUniqueColumns();

	/**
	 * @param columnName The name identifying the attribute.
	 * @param type The type of the column.
	 *
	 * @return the list of all values contained in the specified column.
     */
	<T> List<T> getColumnValues(String columnName, Class<?extends T> type);

	/**
	 * @param primaryKey The primary key index of the row to return.
	 *
	 * @return The {@link CyRow} identified by the specified index.
	 */
	CyRow getRow(long primaryKey);

	/**
	 * @return A new {@link CyRow} object for this CyDataTable.
	 */
	CyRow addRow();
}
