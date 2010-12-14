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
package org.cytoscape.browser.internal.ui;


import org.cytoscape.browser.internal.DataObjectType;
import org.cytoscape.model.CyTable;
import org.cytoscape.equations.EquationUtil;

import java.util.List;
import java.util.Map;


public class Util {
	/**
	 *  Populates "attribNameToTypeMap" with the names from "table" and their types as mapped
	 *  to the types used by attribute equations.  Types (and associated names) not used by
	 *  attribute equations are ommitted.
	 *
	 *  @param table                the attributes to map
	 *  @param ignore               if not null, skip the attribute with this name
	 *  @param attribNameToTypeMap  the result of the translation from attribute types to
	 *                              attribute equation types
	 */
	public static void initAttribNameToTypeMap(final CyTable table, final String ignore,
	                                           final Map<String, Class> attribNameToTypeMap)
	{
		final Map<String, Class<?>> columnsAndTypes = table.getColumnTypeMap();
		for (final String columnName : columnsAndTypes.keySet()) {
			if (ignore != null && ignore.equals(columnName))
				continue;

			final Class<?> type = columnsAndTypes.get(columnName);
			if (type == Boolean.class)
				attribNameToTypeMap.put(attribName, Boolean.class);
			else if (type == Integer.class || type == Long.class)
				attribNameToTypeMap.put(attribName, Long.class);
			else if (type == Double.class)
				attribNameToTypeMap.put(attribName, Double.class);
			else if (type == String.class)
				attribNameToTypeMap.put(attribName, String.class);
			else if (type == List.class)
				attribNameToTypeMap.put(attribName, List.class);
			else
				/* We intentionally ignore everything else! */;
		}
	}
}
