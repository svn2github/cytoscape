
/*
 Copyright (c) 2008, 2011, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.model.internal.builder;


import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.cytoscape.model.builder.CyTableBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.HashMultimap;

public final class CyTableBuilderImpl implements CyTableBuilder {


	private final Map<String, Class<?>> columnTypes;
	private final Map<String, Boolean> columnMutability;
	private String primaryKeyColumnName; 
	private final Map<String, Map<Object,Object>> columnData;
	private final Map<String, SetMultimap<Object,Object>> columnIndices;
	private final Set<Object> primaryKeys;

	private final int DEFAULT_ALLOCATION = 10000;

	public CyTableBuilderImpl() {
		columnTypes = new HashMap<String,Class<?>>();
		columnMutability = new HashMap<String,Boolean>();
		columnData = new HashMap<String,Map<Object,Object>>();
		columnIndices = new HashMap<String,SetMultimap<Object,Object>>();
		primaryKeys = new HashSet<Object>(DEFAULT_ALLOCATION);
	}

	public void createColumn(String name, Class<?> type, boolean mutability) {
		columnTypes.put(name, type);
		columnMutability.put(name, mutability);
	}

	public void setPrimaryKeyColumnName( String name ) {
		primaryKeyColumnName = name;
	}

	public String getPrimaryKeyColumnName() {
		return primaryKeyColumnName;
	}

	public Collection<String> getColumnNames() {
		return columnTypes.keySet();
	}

	public Class<?> getColumnType(String columnName) {
		return columnTypes.get(columnName);
	}

	public boolean getColumnMutability(String columnName) {
		return columnMutability.get(columnName);
	}

	public Collection<Object> getPrimaryKeys() {
		return primaryKeys; 
	}

	public Map<Object,Object> getColumnData(String columnName) {
		Map<Object,Object> data = columnData.get(columnName);
		if ( data == null ) {
			data = new HashMap<Object,Object>(DEFAULT_ALLOCATION);
			columnData.put(columnName,data);
		}
		return data;
	}

	public SetMultimap<Object,Object> getColumnIndex(String columnName) {
		SetMultimap<Object,Object> ind =  columnIndices.get(columnName);
		if ( ind == null ) {
			ind = HashMultimap.create();
			columnIndices.put(columnName,ind);
		}
		return ind;
	}

	public void setColumnValue(String columnName, Object primaryKey, Object value) {
		getColumnData(columnName).put(primaryKey,value);
		getColumnIndex(columnName).put(value,primaryKey);
		primaryKeys.add(primaryKey);
	}
}
