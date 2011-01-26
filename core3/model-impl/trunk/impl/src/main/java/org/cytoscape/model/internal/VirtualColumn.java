/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.model.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;


final class VirtualColumn {
	private final CyTable sourceTable;
	private final String sourceColumn;
	private final Class<?> sourceColumnType;
	private final Class<?> sourceColumnListElementType;
	private final CyTableImpl targetTable;
	private final String sourceJoinColumn;
	private final Class<?> sourceJoinColumnType;
	private final String targetJoinColumn;
	private final Class<?> targetJoinColumnType;

	VirtualColumn(final CyTable sourceTable, final String sourceColumn,
		      final CyTableImpl targetTable, final String sourceJoinColumn,
		      final String targetJoinColumn)
	{
		this.sourceTable                 = sourceTable;
		this.sourceColumn                = sourceColumn;
		this.sourceColumnType            = sourceTable.getType(sourceColumn);
		this.sourceColumnListElementType = (sourceColumnType == List.class)
			? sourceTable.getListElementType(sourceColumn) : null;
		this.targetTable                 = targetTable;
		this.sourceJoinColumn            = sourceJoinColumn;
		this.sourceJoinColumnType        = sourceTable.getType(sourceJoinColumn);
		this.targetJoinColumn            = targetJoinColumn;
		this.targetJoinColumnType        = targetTable.getType(targetJoinColumn);
	}

	Object getRawValue(final Object targetKey) {
		final CyRow sourceRow = getSourceRow(targetKey);
		return (sourceRow == null) ? null : sourceRow.getRaw(sourceColumn);
	}

	void setValue(final Object targetKey, final Object value) {
		final CyRow sourceRow = getSourceRow(targetKey);
		if (sourceRow == null)
			throw new IllegalArgumentException("can't set a value for a virtual column!");
		sourceRow.set(sourceColumn, value);
	}

	Object getValue(final Object targetKey) {
		final CyRow sourceRow = getSourceRow(targetKey);
		if (sourceRow == null)
			return null;

		final Object retValue = sourceRow.get(sourceColumn, sourceColumnType);
		if (retValue == null)
			targetTable.lastInternalError = sourceTable.getLastInternalError();
		return retValue;
	}

	Object getListValue(final Object targetKey) {
		final CyRow sourceRow = getSourceRow(targetKey);
		if (sourceRow == null)
			return null;
		final Object retValue = sourceRow.getList(sourceColumn, sourceColumnListElementType);
		if (retValue == null)
			targetTable.lastInternalError = sourceTable.getLastInternalError();
		return retValue;
	}

	private CyRow getSourceRow(final Object targetKey) {
		final Object joinKey = targetTable.getValue(targetKey, targetJoinColumn);
		if (joinKey == null)
			return null;
		final Set<CyRow> sourceRows = sourceTable.getMatchingRows(sourceJoinColumn,
									  joinKey);
		if (sourceRows.size() != 1)
			return null;

		return sourceRows.iterator().next();
	}

	Set<CyRow> getMatchingRows(final Object value) {
		final Set<CyRow> sourceRows = sourceTable.getMatchingRows(sourceColumn, value);
		final Set<CyRow> targetRows = new HashSet<CyRow>();
		for (final CyRow sourceRow : sourceRows) {
			final Object targetValue = sourceRow.get(sourceJoinColumn,
								 sourceJoinColumnType);
			if (targetValue != null) {
				final Set<CyRow> rows =
					targetTable.getMatchingRows(targetJoinColumn, targetValue);
				targetRows.addAll(rows);
			}
		}
		return targetRows;
	}

	List getColumnValues() {
		final List targetJoinColumnValues =
			targetTable.getColumnValues(targetJoinColumn, targetJoinColumnType);
		List results = new ArrayList();
		for (final Object targetJoinColumnValue : targetJoinColumnValues) {
			final Set<CyRow> sourceRows =
				sourceTable.getMatchingRows(sourceJoinColumn,
							    targetJoinColumnValue);
			if (sourceRows.size() == 1) {
				final CyRow sourceRow = sourceRows.iterator().next();
				final Object value =
					sourceRow.get(sourceColumn, sourceColumnType);
				if (value != null)
					results.add(value);
			}
		}

		return results;
	}
}
