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
package org.cytoscape.model.events;


import java.util.List;

import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.event.CyMicroListener;


/** Listener for aggregated row updates. */
public interface CyTableRowUpdateMicroListener extends CyMicroListener {
	public final static class RowSet {
		private final CyRow row;
		private final String column;
		private final Object value;
		private final Object rawValue;

		public RowSet(final CyRow row, final String column, final Object value,
			       final Object rawValue)
		{
			this.row      = row;
			this.column   = column;
			this.value    = value;
			this.rawValue = rawValue;
		}

		public CyRow getRow() { return row; }
		public String getColumn() { return column; }
		public Object getValue() { return value; }
		public Object getRawValue() { return rawValue; }
	}

	/**
	 * @param table    the table whose updates we would like to track
	 * @param newRows  the list of new rows
	 */
	void handleRowCreations(final CyTable table, final List<CyRow> newRows);

	/**
	 * @param table    the table whose updates we would like to track
	 * @param rowSets  a list of row updates
	 */
	void handleRowSets(final CyTable table, final List<RowSet> rowSets);
}
