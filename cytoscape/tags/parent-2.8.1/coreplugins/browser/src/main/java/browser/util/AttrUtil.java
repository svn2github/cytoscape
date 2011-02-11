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
package browser.util;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import cytoscape.data.CyAttributes;


public class AttrUtil {
	/**
	 *  @returns a map of attribute names and Java types corresponding to the types internally used by attribute equations
	 */
	public static Map<String, Class> getAttrNamesAndTypes(final CyAttributes attribs) {
		final String[] attrNames = attribs.getAttributeNames();
		final Map<String, Class> attribNameToTypeMap = new TreeMap<String, Class>();
		for (final String attrName : attrNames) {
			final byte type = attribs.getType(attrName);
			if (type == CyAttributes.TYPE_INTEGER)
				attribNameToTypeMap.put(attrName, Long.class);
			if (type == CyAttributes.TYPE_BOOLEAN)
				attribNameToTypeMap.put(attrName, Boolean.class);
			if (type == CyAttributes.TYPE_FLOATING)
				attribNameToTypeMap.put(attrName, Double.class);
			if (type == CyAttributes.TYPE_SIMPLE_LIST)
				attribNameToTypeMap.put(attrName, List.class);
			if (type == CyAttributes.TYPE_STRING)
				attribNameToTypeMap.put(attrName, String.class);
		}

		return attribNameToTypeMap;
	}
}